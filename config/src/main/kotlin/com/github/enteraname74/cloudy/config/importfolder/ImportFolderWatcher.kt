package com.github.enteraname74.cloudy.config.importfolder

import com.github.enteraname74.cloudy.domain.filepersistence.ImportFolder
import com.github.enteraname74.cloudy.domain.service.MusicImportService
import com.github.enteraname74.cloudy.domain.service.MusicImportService.ImportResult
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.logging.CloudyLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.nio.file.ClosedWatchServiceException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.StandardWatchEventKinds.OVERFLOW
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Watches the import folder and imports every music file put in it.
 *
 * Layout: `<root>/<username>/<music file>`.
 * Every processed file is deleted from the import folder, whether it was imported or not,
 * so that files that cannot be imported don't accumulate.
 * Folders matching no user are deleted.
 *
 * File system events are only used to react quickly: they can be lost (overflow, network mounts...),
 * so the whole folder is also scanned at startup and periodically.
 * A file is only imported once it hasn't changed for [stabilityDelay], to avoid importing a file still being written.
 */
class ImportFolderWatcher(
    private val importFolder: ImportFolder,
    private val musicImportService: MusicImportService,
    private val userService: UserService,
    private val scanInterval: Duration = 5.minutes,
    private val stabilityDelay: Duration = 5.seconds,
) {
    private val logger = CloudyLogger(this::class)

    private val root: File = requireNotNull(importFolder.root) { "Import folder is disabled" }

    private val candidates = Channel<File>(Channel.UNLIMITED)

    /**
     * Files waiting to be stable before being imported.
     * Only accessed from the processing coroutine.
     */
    private val pending: MutableMap<File, FileSnapshot> = mutableMapOf()

    /**
     * Files that couldn't be deleted after being processed, and must not be processed again until modified.
     * Only accessed from the processing coroutine.
     */
    private val ignored: MutableSet<FileSnapshot.Key> = mutableSetOf()

    private var watchService: WatchService? = null

    fun start(scope: CoroutineScope): Job = scope.launch(Dispatchers.IO) {
        importFolder.ensureRootExists()
        logger.info("Watching import folder ${root.absolutePath}")

        launch { watch() }
        launch { scanPeriodically() }
        processCandidates()
    }

    fun stop() {
        watchService?.close()
    }

    private suspend fun scanPeriodically() {
        while (true) {
            scanAll()
            delay(scanInterval)
        }
    }

    /**
     * Creates a folder for each user, deletes the folders matching no user and scans the others.
     */
    private suspend fun scanAll() {
        val usernames: Set<String>? = runCatching { userService.getAll() }
            .onFailure { logger.error("Failed to fetch users for import folders: $it") }
            .getOrNull()
            ?.map { it.username }
            ?.filter { ImportFolder.isValidUserFolderName(it) }
            ?.toSet()

        usernames?.forEach { importFolder.createUserFolder(it) }

        root.listFiles()
            ?.filter { it.isUserFolder() }
            ?.forEach { folder ->
                // If users couldn't be fetched, we don't know which folders are valid: we keep them all.
                if (usernames == null || folder.name in usernames) {
                    scanUserFolder(folder)
                } else {
                    deleteUnknownUserFolder(folder)
                }
            }
    }

    private fun deleteUnknownUserFolder(folder: File) {
        logger.warn("No user named ${folder.name}, deleting import folder ${folder.path}")
        if (!folder.deleteRecursively()) {
            logger.error("Failed to delete import folder ${folder.path}")
        }
    }

    private fun scanUserFolder(folder: File) {
        folder.listFiles()
            ?.filter { it.isFile }
            ?.forEach { candidates.trySend(it) }
    }

    // region Watch

    private suspend fun watch() {
        val service: WatchService = runCatching { FileSystems.getDefault().newWatchService() }.getOrElse {
            logger.warn("Cannot watch import folder, relying on periodic scans only: $it")
            return
        }
        watchService = service
        val watchedFolders: MutableMap<WatchKey, Path> = mutableMapOf()

        fun register(folder: File) {
            runCatching {
                val path = folder.toPath()
                watchedFolders[path.register(service, ENTRY_CREATE, ENTRY_MODIFY)] = path
            }.onFailure { logger.warn("Cannot watch folder ${folder.path}: $it") }
        }

        register(root)
        root.listFiles()?.filter { it.isUserFolder() }?.forEach { register(it) }

        try {
            while (currentCoroutineContext().isActive) {
                val key: WatchKey = withContext(Dispatchers.IO) {
                    service.poll(1, TimeUnit.SECONDS)
                } ?: continue
                val folder: Path? = watchedFolders[key]

                for (event in key.pollEvents()) {
                    if (event.kind() == OVERFLOW || folder == null) {
                        scanAll()
                        continue
                    }
                    val changed: File = folder.resolve(event.context() as Path).toFile()

                    if (folder.toFile() == root) {
                        // A new user folder.
                        if (changed.isUserFolder()) {
                            register(changed)
                            scanUserFolder(changed)
                        }
                    } else if (changed.isFile) {
                        candidates.trySend(changed)
                    }
                }

                if (!key.reset()) {
                    watchedFolders.remove(key)
                }
            }
        } catch (_: ClosedWatchServiceException) {
            // Stopped.
        }
    }

    // endregion

    // region Processing

    /**
     * Files are imported one by one, to avoid fingerprinting many files at once
     * and concurrent creations of the same artist or album.
     */
    private suspend fun processCandidates() {
        while (true) {
            withTimeoutOrNull(CHECK_INTERVAL) { candidates.receive() }?.let { addPending(it) }
            while (true) {
                addPending(candidates.tryReceive().getOrNull() ?: break)
            }
            importStableFiles()
        }
    }

    private fun addPending(file: File) {
        if (file in pending || !file.isImportCandidate()) return
        val snapshot = FileSnapshot.of(file)
        if (snapshot.key in ignored) return
        pending[file] = snapshot
    }

    private suspend fun importStableFiles() {
        val now = System.currentTimeMillis()
        val stableFiles: List<File> = pending.toList().mapNotNull { (file, snapshot) ->
            if (!file.exists()) {
                pending.remove(file)
                return@mapNotNull null
            }
            val current = FileSnapshot.of(file)
            if (current.key != snapshot.key) {
                // Still being written.
                pending[file] = current
                null
            } else if (now - snapshot.unchangedSinceMillis >= stabilityDelay.inWholeMilliseconds) {
                file
            } else {
                null
            }
        }

        stableFiles.forEach { file ->
            pending.remove(file)
            import(file)
        }
    }

    private suspend fun import(file: File) {
        val username: String = file.parentFile.name
        logger.info("Importing ${file.name} for user $username")

        val result: ImportResult = runCatching {
            musicImportService.importFile(username = username, file = file)
        }.getOrElse {
            logger.error("Unexpected error while importing ${file.path}: $it")
            ImportResult.Failed
        }

        when (result) {
            is ImportResult.Imported -> {
                logger.info("Imported ${file.name} as ${result.music.name} for user $username")
                delete(file)
            }
            ImportResult.UnknownUser -> deleteUnknownUserFolder(file.parentFile)
            ImportResult.UnsupportedFile -> {
                logger.warn("Unsupported file, deleting ${file.path}")
                delete(file)
            }
            ImportResult.StorageFull -> {
                logger.warn("Storage of user $username is full, deleting ${file.path}")
                delete(file)
            }
            ImportResult.Unreadable -> {
                // Not ignored: fixing permissions doesn't change the file size or modification date,
                // so the file is retried at each scan until readable.
                logger.error("Cannot read ${file.path}, check its permissions. It will be imported once readable")
            }
            ImportResult.Failed -> {
                logger.error("Failed to import, deleting ${file.path}")
                delete(file)
            }
        }
    }

    private fun delete(file: File) {
        if (!file.delete()) {
            logger.warn("File ${file.path} couldn't be deleted, it won't be processed again until modified")
            ignored += FileSnapshot.of(file).key
        }
    }

    // endregion

    private fun File.isUserFolder(): Boolean =
        isDirectory && ImportFolder.isValidUserFolderName(name)

    private fun File.isImportCandidate(): Boolean =
        isFile &&
            parentFile?.parentFile?.absoluteFile == root.absoluteFile &&
            !name.startsWith(".") &&
            TEMPORARY_EXTENSIONS.none { name.endsWith(".$it", ignoreCase = true) }

    private data class FileSnapshot(
        val key: Key,
        val unchangedSinceMillis: Long,
    ) {
        data class Key(
            val path: String,
            val size: Long,
            val lastModified: Long,
        )

        companion object {
            fun of(file: File): FileSnapshot =
                FileSnapshot(
                    key = Key(
                        path = file.absolutePath,
                        size = file.length(),
                        lastModified = file.lastModified(),
                    ),
                    unchangedSinceMillis = System.currentTimeMillis(),
                )
        }
    }

    companion object {
        private val CHECK_INTERVAL: Duration = 1.seconds

        /**
         * Extensions used by download tools for files still being downloaded.
         */
        private val TEMPORARY_EXTENSIONS: Set<String> = setOf(
            "part",
            "tmp",
            "temp",
            "crdownload",
            "download",
            "partial",
            "!qb",
        )
    }
}

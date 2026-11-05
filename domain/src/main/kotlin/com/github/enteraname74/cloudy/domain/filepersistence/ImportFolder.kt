package com.github.enteraname74.cloudy.domain.filepersistence

import com.github.enteraname74.cloudy.logging.CloudyLogger
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermissions

/**
 * Folder watched by the server to import music files put directly on the server.
 * Layout: `<root>/<username>/<music file>`.
 *
 * Folders created by the server are writable by everyone: the server (the `cloudy` user in docker)
 * and the people or services putting files in them (other users of the host) usually don't share the same user.
 *
 * @param root the root of the import folder, or null if the feature is disabled.
 */
class ImportFolder(val root: File?) {
    private val logger = CloudyLogger(this::class)

    /**
     * Creates the root of the import folder if it doesn't exist.
     */
    fun ensureRootExists() {
        root?.let { createSharedFolder(it) }
    }

    /**
     * Creates the import folder of a user if it doesn't exist.
     */
    fun createUserFolder(username: String) {
        val root: File = root ?: return
        if (!isValidUserFolderName(username)) return
        createSharedFolder(File(root, username))
    }

    /**
     * Deletes the import folder of a user and all its content.
     */
    fun deleteUserFolder(username: String) {
        val folder: File = root
            ?.takeIf { isValidUserFolderName(username) }
            ?.let { File(it, username) }
            ?.takeIf { it.exists() }
            ?: return

        if (!folder.deleteRecursively()) {
            logger.error("Failed to delete import folder ${folder.path}")
        }
    }

    /**
     * Creates a folder writable by everyone.
     * Permissions of an existing folder are left untouched, as they may have been set by the server admin.
     */
    private fun createSharedFolder(folder: File) {
        if (folder.exists()) return

        if (!folder.mkdirs()) {
            logger.error("Failed to create import folder ${folder.path}, check the permissions of its parent folder")
            return
        }

        runCatching {
            // Explicitly set, as permissions given at creation are restricted by the umask.
            Files.setPosixFilePermissions(folder.toPath(), PosixFilePermissions.fromString("rwxrwxrwx"))
        }.onFailure {
            logger.warn("Failed to make import folder ${folder.path} writable by everyone: $it")
        }
    }

    companion object {
        fun fromEnvironment(): ImportFolder =
            ImportFolder(
                root = System.getenv("IMPORT_FOLDER_PATH")
                    ?.takeIf { it.isNotBlank() }
                    ?.let { File(it) },
            )

        /**
         * A username can only be used as a folder name if it is a single, non-hidden path segment.
         */
        fun isValidUserFolderName(name: String): Boolean =
            name.isNotBlank() && !name.startsWith(".") && File(name).name == name
    }
}

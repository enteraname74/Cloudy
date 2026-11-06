package com.github.enteraname74.cloudy.domain.filepersistence

import com.github.enteraname74.cloudy.logging.CloudyLogger
import java.io.File

/**
 * Folder watched by the server to import music files put directly on the server.
 * Layout: `<root>/<username>/<music file>`.
 *
 * Permissions of created folders are not set here: they come from the umask and the parent folder.
 * With docker, the root is shared with the group of the host user (setgid, see compose.yaml),
 * so user folders created in it are shared with that group too.
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
     * Creates a folder, keeping the permissions inherited from its parent.
     * No chmod is done: it would remove the setgid bit inherited from the parent.
     */
    private fun createSharedFolder(folder: File) {
        if (folder.exists()) return

        if (!folder.mkdirs()) {
            logger.error("Failed to create import folder ${folder.path}, check the permissions of its parent folder")
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

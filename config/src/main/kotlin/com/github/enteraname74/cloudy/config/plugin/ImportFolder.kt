package com.github.enteraname74.cloudy.config.plugin

import com.github.enteraname74.cloudy.config.importfolder.ImportFolderWatcher
import com.github.enteraname74.cloudy.domain.filepersistence.ImportFolder
import com.github.enteraname74.cloudy.domain.service.MusicImportService
import com.github.enteraname74.cloudy.domain.service.UserService
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import org.koin.ktor.ext.inject

/**
 * Starts watching the import folder if the IMPORT_FOLDER_PATH env variable is set.
 * Music files put in `<IMPORT_FOLDER_PATH>/<username>/` are imported for the matching user.
 */
internal fun Application.configureImportFolder() {
    val importFolder by inject<ImportFolder>()
    if (importFolder.root == null) return

    val musicImportService by inject<MusicImportService>()
    val userService by inject<UserService>()

    val watcher = ImportFolderWatcher(
        importFolder = importFolder,
        musicImportService = musicImportService,
        userService = userService,
    )
    val job = watcher.start(scope = this)

    monitor.subscribe(ApplicationStopping) {
        watcher.stop()
        job.cancel()
    }
}

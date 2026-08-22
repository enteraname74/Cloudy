package com.github.enteraname74.cloudy.config.di

import com.github.enteraname74.cloudy.domain.auth.HashedPasswordManager
import com.github.enteraname74.cloudy.domain.auth.HashedPasswordManagerImpl
import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationRetriever
import com.github.enteraname74.cloudy.domain.migration.BackendMigrationManager
import com.github.enteraname74.cloudy.domain.websocket.PlayerUserCommunication
import com.github.enteraname74.cloudy.fileaccess.CoverFileManager
import com.github.enteraname74.cloudy.fileaccess.MusicFileManager
import com.github.enteraname74.cloudy.fileaccess.SettingsFileManager
import com.github.enteraname74.cloudy.metadata.filemetadata.MusicFileMetadataManager
import com.github.enteraname74.cloudy.metadata.musicinformation.MusicInformationRetrieverImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val otherModule = module {
    singleOf(::MusicFileManager)
    singleOf(::CoverFileManager)
    singleOf(::MusicFileMetadataManager)
    singleOf(::SettingsFileManager)
    singleOf(::MusicInformationRetrieverImpl) bind MusicInformationRetriever::class
    singleOf(::HashedPasswordManagerImpl) bind HashedPasswordManager::class

    singleOf(::PlayerUserCommunication)

    singleOf(::BackendMigrationManager)
}
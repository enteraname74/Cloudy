package com.github.enteraname74.cloudy.config.di

import com.github.enteraname74.cloudy.domain.auth.*
import com.github.enteraname74.cloudy.domain.filepersistence.CoverFilePersistenceManager
import com.github.enteraname74.cloudy.domain.filepersistence.MusicFilePersistenceManager
import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationRetriever
import com.github.enteraname74.cloudy.metadata.cover.LocalCoverRetriever
import com.github.enteraname74.cloudy.metadata.musicinformation.MusicInformationRetrieverImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val otherModule = module {
    singleOf(::MusicFilePersistenceManager)
    singleOf(::CoverFilePersistenceManager)
    singleOf(::MusicInformationRetrieverImpl) bind MusicInformationRetriever::class
    singleOf(::LocalCoverRetriever)
    singleOf(::HashedPasswordManagerImpl) bind HashedPasswordManager::class
}
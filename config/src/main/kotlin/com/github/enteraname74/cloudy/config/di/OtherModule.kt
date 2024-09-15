package com.github.enteraname74.cloudy.config.di

import com.github.enteraname74.cloudy.domain.auth.*
import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationRetriever
import com.github.enteraname74.cloudy.metadata.musicinformation.MusicInformationRetrieverImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val otherModule = module {
    singleOf(::MusicInformationRetrieverImpl) bind MusicInformationRetriever::class
    singleOf(::HashedPasswordManagerImpl) bind HashedPasswordManager::class
}
package com.github.enteraname74.cloudy.config.di

import com.github.enteraname74.cloudy.domain.usecase.album.*
import com.github.enteraname74.cloudy.domain.usecase.artist.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val useCaseModule = module {
    // Album
    singleOf(::DeleteAlbumIfEmptyUseCase)
    singleOf(::GetOrCreateAlbumUseCase)

    // Artist
    singleOf(::GetOrCreateArtistUseCase)
    singleOf(::DeleteArtistIfEmptyUseCase)
}
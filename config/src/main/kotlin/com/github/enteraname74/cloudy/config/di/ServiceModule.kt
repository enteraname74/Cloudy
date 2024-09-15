package com.github.enteraname74.cloudy.config.di

import com.github.enteraname74.cloudy.domain.service.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val serviceModule = module {
    singleOf(::MusicService)
    singleOf(::MusicFileService)
    singleOf(::ArtistService)
    singleOf(::AlbumService)
    singleOf(::PlaylistService)
    singleOf(::UserService)
}
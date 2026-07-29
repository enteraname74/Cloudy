package com.github.enteraname74.cloudy.config.di

import com.github.enteraname74.cloudy.domain.service.AlbumService
import com.github.enteraname74.cloudy.domain.service.ArtistService
import com.github.enteraname74.cloudy.domain.service.CoverService
import com.github.enteraname74.cloudy.domain.service.MusicArtistService
import com.github.enteraname74.cloudy.domain.service.MusicPlaylistService
import com.github.enteraname74.cloudy.domain.service.MusicService
import com.github.enteraname74.cloudy.domain.service.PlayerService
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import com.github.enteraname74.cloudy.domain.service.UserService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val serviceModule = module {
    singleOf(::MusicService)
    singleOf(::ArtistService)
    singleOf(::AlbumService)
    singleOf(::PlaylistService)
    singleOf(::UserService)
    singleOf(::CoverService)
    singleOf(::MusicArtistService)
    singleOf(::MusicPlaylistService)
    singleOf(::PlayerService)
}
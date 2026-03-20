package com.github.enteraname74.cloudy.config.di

import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.repository.CoverRepository
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.repository.PlaylistRepository
import com.github.enteraname74.cloudy.domain.repository.UserRepository
import com.github.enteraname74.cloudy.repository.repositoryImpl.AlbumRepositoryImpl
import com.github.enteraname74.cloudy.repository.repositoryImpl.ArtistRepositoryImpl
import com.github.enteraname74.cloudy.repository.repositoryImpl.CoverRepositoryImpl
import com.github.enteraname74.cloudy.repository.repositoryImpl.MusicArtistRepositoryImpl
import com.github.enteraname74.cloudy.repository.repositoryImpl.MusicPlaylistRepositoryImpl
import com.github.enteraname74.cloudy.repository.repositoryImpl.MusicRepositoryImpl
import com.github.enteraname74.cloudy.repository.repositoryImpl.PlaylistRepositoryImpl
import com.github.enteraname74.cloudy.repository.repositoryImpl.UserRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val repositoryModule = module {
    singleOf(::MusicRepositoryImpl) bind MusicRepository::class
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::AlbumRepositoryImpl) bind AlbumRepository::class
    singleOf(::ArtistRepositoryImpl) bind ArtistRepository::class
    singleOf(::PlaylistRepositoryImpl) bind PlaylistRepository::class
    singleOf(::CoverRepositoryImpl) bind CoverRepository::class
    singleOf(::MusicArtistRepositoryImpl) bind MusicArtistRepository::class
    singleOf(::MusicPlaylistRepositoryImpl) bind MusicPlaylistRepository::class
}
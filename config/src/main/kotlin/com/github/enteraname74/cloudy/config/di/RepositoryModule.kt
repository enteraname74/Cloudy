package com.github.enteraname74.cloudy.config.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.github.enteraname74.cloudy.repository.repositoryImpl.*
import com.github.enteraname74.cloudy.domain.repository.*
import org.koin.dsl.bind

internal val repositoryModule = module {
    singleOf(::MusicRepositoryImpl) bind MusicRepository::class
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::AlbumRepositoryImpl) bind AlbumRepository::class
    singleOf(::ArtistRepositoryImpl) bind ArtistRepository::class
}
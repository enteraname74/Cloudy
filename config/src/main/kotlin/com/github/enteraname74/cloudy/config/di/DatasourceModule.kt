package com.github.enteraname74.cloudy.config.di

import org.koin.dsl.module

import com.github.enteraname74.cloudy.localdb.datasourceimpl.*
import com.github.enteraname74.cloudy.repository.datasource.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal val datasourceModule = module {
    singleOf(::MusicDataSourceImpl) bind MusicDataSource::class
    singleOf(::UserDataSourceImpl) bind UserDataSource::class
    singleOf(::ArtistDataSourceImpl) bind ArtistDataSource::class
    singleOf(::AlbumDataSourceImpl) bind AlbumDataSource::class
    singleOf(::PlaylistDataSourceImpl) bind PlaylistDataSource::class
}
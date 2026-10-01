package com.github.enteraname74.cloudy.config.di

import com.github.enteraname74.cloudy.localdb.datasourceimpl.AlbumDataSourceImpl
import com.github.enteraname74.cloudy.localdb.datasourceimpl.ArtistDataSourceImpl
import com.github.enteraname74.cloudy.localdb.datasourceimpl.CoverDataSourceImpl
import com.github.enteraname74.cloudy.localdb.datasourceimpl.ListeningStatisticsDataSourceImpl
import com.github.enteraname74.cloudy.localdb.datasourceimpl.MusicArtistDataSourceImpl
import com.github.enteraname74.cloudy.localdb.datasourceimpl.MusicDataSourceImpl
import com.github.enteraname74.cloudy.localdb.datasourceimpl.MusicPlaylistDataSourceImpl
import com.github.enteraname74.cloudy.localdb.datasourceimpl.PlayerDataSourceImpl
import com.github.enteraname74.cloudy.localdb.datasourceimpl.PlaylistDataSourceImpl
import com.github.enteraname74.cloudy.localdb.datasourceimpl.SettingsDataSourceImpl
import com.github.enteraname74.cloudy.localdb.datasourceimpl.UserDataSourceImpl
import com.github.enteraname74.cloudy.localdb.datasourceimpl.UserInscriptionCodeDataSourceImpl
import com.github.enteraname74.cloudy.repository.datasource.AlbumDataSource
import com.github.enteraname74.cloudy.repository.datasource.ArtistDataSource
import com.github.enteraname74.cloudy.repository.datasource.CoverDataSource
import com.github.enteraname74.cloudy.repository.datasource.ListeningStatisticsDataSource
import com.github.enteraname74.cloudy.repository.datasource.MusicArtistDataSource
import com.github.enteraname74.cloudy.repository.datasource.MusicDataSource
import com.github.enteraname74.cloudy.repository.datasource.MusicPlaylistDataSource
import com.github.enteraname74.cloudy.repository.datasource.PlayerDataSource
import com.github.enteraname74.cloudy.repository.datasource.PlaylistDataSource
import com.github.enteraname74.cloudy.repository.datasource.SettingsDataSource
import com.github.enteraname74.cloudy.repository.datasource.UserDataSource
import com.github.enteraname74.cloudy.repository.datasource.UserInscriptionCodeDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val datasourceModule = module {
    singleOf(::MusicDataSourceImpl) bind MusicDataSource::class
    singleOf(::UserDataSourceImpl) bind UserDataSource::class
    singleOf(::ArtistDataSourceImpl) bind ArtistDataSource::class
    singleOf(::AlbumDataSourceImpl) bind AlbumDataSource::class
    singleOf(::PlaylistDataSourceImpl) bind PlaylistDataSource::class
    singleOf(::MusicArtistDataSourceImpl) bind MusicArtistDataSource::class
    singleOf(::MusicPlaylistDataSourceImpl) bind MusicPlaylistDataSource::class
    singleOf(::PlayerDataSourceImpl) bind PlayerDataSource::class
    singleOf(::UserInscriptionCodeDataSourceImpl) bind UserInscriptionCodeDataSource::class
    singleOf(::SettingsDataSourceImpl) bind SettingsDataSource::class
    singleOf(::CoverDataSourceImpl) bind CoverDataSource::class
    singleOf(::ListeningStatisticsDataSourceImpl) bind ListeningStatisticsDataSource::class
}
package com.github.enteraname74.cloudy.config.di

import com.github.enteraname74.cloudy.domain.usecase.DeleteEmptyAlbumsAndArtistsUseCase
import com.github.enteraname74.cloudy.domain.usecase.album.*
import com.github.enteraname74.cloudy.domain.usecase.artist.*
import com.github.enteraname74.cloudy.domain.usecase.music.UpdateMusicUseCase
import com.github.enteraname74.cloudy.domain.usecase.music.UploadMusicUseCase
import com.github.enteraname74.cloudy.domain.usecase.musicartist.SetArtistsOfMusicUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val useCaseModule = module {
    // Album
    factoryOf(::DeleteAlbumIfEmptyUseCase)
    factoryOf(::GetOrCreateAlbumUseCase)
    factoryOf(::UploadAlbumUseCase)
    factoryOf(::UpdateAlbumUseCase)

    // Artist
    factoryOf(::GetOrCreateArtistUseCase)
    factoryOf(::DeleteArtistIfEmptyUseCase)
    factoryOf(::UploadArtistUseCase)
    factoryOf(::UpdateArtistUseCase)

    // Music
    factoryOf(::UploadMusicUseCase)
    factoryOf(::UpdateMusicUseCase)
    
    // MusicArtist
    factoryOf(::SetArtistsOfMusicUseCase)

    factoryOf(::DeleteEmptyAlbumsAndArtistsUseCase)
}
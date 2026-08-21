package com.github.enteraname74.cloudy.config.di

import com.github.enteraname74.cloudy.domain.usecase.DeleteEmptyAlbumsAndArtistsUseCase
import com.github.enteraname74.cloudy.domain.usecase.DeleteUserDataUseCase
import com.github.enteraname74.cloudy.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.album.UpdateAlbumUseCase
import com.github.enteraname74.cloudy.domain.usecase.album.UploadAlbumUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.UpdateArtistUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.UploadArtistUseCase
import com.github.enteraname74.cloudy.domain.usecase.music.UpdateMusicUseCase
import com.github.enteraname74.cloudy.domain.usecase.music.UploadMusicUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.SetArtistsOfMusicUseCase
import com.github.enteraname74.cloudy.domain.usecase.playlist.AddMusicsToPlaylistUseCase
import com.github.enteraname74.cloudy.domain.usecase.playlist.SetPlaylistMusicsUseCase
import com.github.enteraname74.cloudy.domain.usecase.playlist.UploadPlaylistUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val useCaseModule = module {
    // Album
    factoryOf(::DeleteAlbumIfEmptyUseCase)
    factoryOf(::UploadAlbumUseCase)
    factoryOf(::UpdateAlbumUseCase)

    // Artist
    factoryOf(::DeleteArtistIfEmptyUseCase)
    factoryOf(::UploadArtistUseCase)
    factoryOf(::UpdateArtistUseCase)
    factoryOf(::SetArtistsOfMusicUseCase)


    // Music
    factoryOf(::UploadMusicUseCase)
    factoryOf(::UpdateMusicUseCase)

    // Playlist
    factoryOf(::SetPlaylistMusicsUseCase)
    factoryOf(::AddMusicsToPlaylistUseCase)
    factoryOf(::UploadPlaylistUseCase)

    factoryOf(::DeleteEmptyAlbumsAndArtistsUseCase)
    factoryOf(::DeleteUserDataUseCase)
}
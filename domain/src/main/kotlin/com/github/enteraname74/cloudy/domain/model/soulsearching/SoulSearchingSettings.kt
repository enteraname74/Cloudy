package com.github.enteraname74.cloudy.domain.model.soulsearching

import kotlinx.serialization.Serializable

@Serializable
data class SoulSearchingSettings(
    val isMusicFileModificationOn: Boolean,
    val musicSortSetup: SortSetup,
    val albumSortSetup: SortSetup,
    val artistSortSetup: SortSetup,
    val playlistSortSetup: SortSetup,
    val playerSettings: PlayerSettings,
    val colorTheme: ColorTheme,
    val mainPage: MainPage,
    val release: Release,
    val album: Album,
) {
    @Serializable
    data class PlayerSettings(
        val isPlayerSwipeEnabled: Boolean,
        val soulMixTotalByList: Int,
        val isRewindEnabled: Boolean,
        val isMinimisedSongProgressionShown: Boolean,
        val isRemoteLyricsFetchEnabled: Boolean,
    )

    @Serializable
    data class ColorTheme(
        val type: ColorThemeType,
        val dynamicPlayerTheme: Boolean,
        val dynamicPlaylistTheme: Boolean,
        val dynamicOtherViewsTheme: Boolean,
        val forceDarkTheme: Boolean,
        val forceLightTheme: Boolean,
    )

    @Serializable
    data class MainPage(
        val isQuickAccessTabShown: Boolean,
        val isArtistsTabShown: Boolean,
        val isAlbumsTabShown: Boolean,
        val isPlaylistsTabShown: Boolean,
        val isFoldersTabShown: Boolean,
        val areMusicsByMonthShown: Boolean,
        val isUsingVerticalAccessBar: Boolean,
    )

    @Serializable
    data class Release(
        val isFetchReleaseFromGithubEnabled: Boolean,
    )

    @Serializable
    data class Album(
        val shouldShowTrackPositionInAlbumView: Boolean,
    )
}

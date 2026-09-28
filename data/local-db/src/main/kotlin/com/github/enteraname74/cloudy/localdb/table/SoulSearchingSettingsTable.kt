package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.soulsearching.ColorThemeType
import com.github.enteraname74.cloudy.domain.model.soulsearching.SortDirection
import com.github.enteraname74.cloudy.domain.model.soulsearching.SortSetup
import com.github.enteraname74.cloudy.domain.model.soulsearching.SortType
import com.github.enteraname74.cloudy.domain.model.soulsearching.SoulSearchingSettings
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.upsert
import kotlin.uuid.Uuid

internal object SoulSearchingSettingsTable : IdTable<Uuid>() {
    override val id = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(id)

    val isMusicFileModificationOn = bool("isMusicFileModificationOn")

    val musicSortType = integer("musicSortType")
    val musicSortDirection = integer("musicSortDirection")
    val albumSortType = integer("albumSortType")
    val albumSortDirection = integer("albumSortDirection")
    val artistSortType = integer("artistSortType")
    val artistSortDirection = integer("artistSortDirection")
    val playlistSortType = integer("playlistSortType")
    val playlistSortDirection = integer("playlistSortDirection")

    val isPlayerSwipeEnabled = bool("isPlayerSwipeEnabled")
    val soulMixTotalByList = integer("soulMixTotalByList")
    val isRewindEnabled = bool("isRewindEnabled")
    val isMinimisedSongProgressionShown = bool("isMinimisedSongProgressionShown")
    val isRemoteLyricsFetchEnabled = bool("isRemoteLyricsFetchEnabled")

    val colorThemeType = integer("colorThemeType")
    val dynamicPlayerTheme = bool("dynamicPlayerTheme")
    val dynamicPlaylistTheme = bool("dynamicPlaylistTheme")
    val dynamicOtherViewsTheme = bool("dynamicOtherViewsTheme")
    val forceDarkTheme = bool("forceDarkTheme")
    val forceLightTheme = bool("forceLightTheme")

    val isQuickAccessTabShown = bool("isQuickAccessTabShown")
    val isArtistsTabShown = bool("isArtistsTabShown")
    val isAlbumsTabShown = bool("isAlbumsTabShown")
    val isPlaylistsTabShown = bool("isPlaylistsTabShown")
    val isFoldersTabShown = bool("isFoldersTabShown")
    val areMusicsByMonthShown = bool("areMusicsByMonthShown")
    val isUsingVerticalAccessBar = bool("isUsingVerticalAccessBar")

    val isFetchReleaseFromGithubEnabled = bool("isFetchReleaseFromGithubEnabled")
    val shouldShowTrackPositionInAlbumView = bool("shouldShowTrackPositionInAlbumView")

    fun upsert(
        userId: Uuid,
        settings: SoulSearchingSettings,
    ) {
        upsert {
            it[id] = userId
            it[isMusicFileModificationOn] = settings.isMusicFileModificationOn

            it[musicSortType] = settings.musicSortSetup.type.value
            it[musicSortDirection] = settings.musicSortSetup.direction.value
            it[albumSortType] = settings.albumSortSetup.type.value
            it[albumSortDirection] = settings.albumSortSetup.direction.value
            it[artistSortType] = settings.artistSortSetup.type.value
            it[artistSortDirection] = settings.artistSortSetup.direction.value
            it[playlistSortType] = settings.playlistSortSetup.type.value
            it[playlistSortDirection] = settings.playlistSortSetup.direction.value

            it[isPlayerSwipeEnabled] = settings.playerSettings.isPlayerSwipeEnabled
            it[soulMixTotalByList] = settings.playerSettings.soulMixTotalByList
            it[isRewindEnabled] = settings.playerSettings.isRewindEnabled
            it[isMinimisedSongProgressionShown] = settings.playerSettings.isMinimisedSongProgressionShown
            it[isRemoteLyricsFetchEnabled] = settings.playerSettings.isRemoteLyricsFetchEnabled

            it[colorThemeType] = settings.colorTheme.type.raw
            it[dynamicPlayerTheme] = settings.colorTheme.dynamicPlayerTheme
            it[dynamicPlaylistTheme] = settings.colorTheme.dynamicPlaylistTheme
            it[dynamicOtherViewsTheme] = settings.colorTheme.dynamicOtherViewsTheme
            it[forceDarkTheme] = settings.colorTheme.forceDarkTheme
            it[forceLightTheme] = settings.colorTheme.forceLightTheme

            it[isQuickAccessTabShown] = settings.mainPage.isQuickAccessTabShown
            it[isArtistsTabShown] = settings.mainPage.isArtistsTabShown
            it[isAlbumsTabShown] = settings.mainPage.isAlbumsTabShown
            it[isPlaylistsTabShown] = settings.mainPage.isPlaylistsTabShown
            it[isFoldersTabShown] = settings.mainPage.isFoldersTabShown
            it[areMusicsByMonthShown] = settings.mainPage.areMusicsByMonthShown
            it[isUsingVerticalAccessBar] = settings.mainPage.isUsingVerticalAccessBar

            it[isFetchReleaseFromGithubEnabled] = settings.release.isFetchReleaseFromGithubEnabled
            it[shouldShowTrackPositionInAlbumView] = settings.album.shouldShowTrackPositionInAlbumView
        }
    }
}

internal class SoulSearchingSettingsEntity(id: EntityID<Uuid>) : Entity<Uuid>(id) {
    companion object : EntityClass<Uuid, SoulSearchingSettingsEntity>(SoulSearchingSettingsTable)

    var isMusicFileModificationOn by SoulSearchingSettingsTable.isMusicFileModificationOn

    var musicSortType by SoulSearchingSettingsTable.musicSortType
    var musicSortDirection by SoulSearchingSettingsTable.musicSortDirection
    var albumSortType by SoulSearchingSettingsTable.albumSortType
    var albumSortDirection by SoulSearchingSettingsTable.albumSortDirection
    var artistSortType by SoulSearchingSettingsTable.artistSortType
    var artistSortDirection by SoulSearchingSettingsTable.artistSortDirection
    var playlistSortType by SoulSearchingSettingsTable.playlistSortType
    var playlistSortDirection by SoulSearchingSettingsTable.playlistSortDirection

    var isPlayerSwipeEnabled by SoulSearchingSettingsTable.isPlayerSwipeEnabled
    var soulMixTotalByList by SoulSearchingSettingsTable.soulMixTotalByList
    var isRewindEnabled by SoulSearchingSettingsTable.isRewindEnabled
    var isMinimisedSongProgressionShown by SoulSearchingSettingsTable.isMinimisedSongProgressionShown
    var isRemoteLyricsFetchEnabled by SoulSearchingSettingsTable.isRemoteLyricsFetchEnabled

    var colorThemeType by SoulSearchingSettingsTable.colorThemeType
    var dynamicPlayerTheme by SoulSearchingSettingsTable.dynamicPlayerTheme
    var dynamicPlaylistTheme by SoulSearchingSettingsTable.dynamicPlaylistTheme
    var dynamicOtherViewsTheme by SoulSearchingSettingsTable.dynamicOtherViewsTheme
    var forceDarkTheme by SoulSearchingSettingsTable.forceDarkTheme
    var forceLightTheme by SoulSearchingSettingsTable.forceLightTheme

    var isQuickAccessTabShown by SoulSearchingSettingsTable.isQuickAccessTabShown
    var isArtistsTabShown by SoulSearchingSettingsTable.isArtistsTabShown
    var isAlbumsTabShown by SoulSearchingSettingsTable.isAlbumsTabShown
    var isPlaylistsTabShown by SoulSearchingSettingsTable.isPlaylistsTabShown
    var isFoldersTabShown by SoulSearchingSettingsTable.isFoldersTabShown
    var areMusicsByMonthShown by SoulSearchingSettingsTable.areMusicsByMonthShown
    var isUsingVerticalAccessBar by SoulSearchingSettingsTable.isUsingVerticalAccessBar

    var isFetchReleaseFromGithubEnabled by SoulSearchingSettingsTable.isFetchReleaseFromGithubEnabled
    var shouldShowTrackPositionInAlbumView by SoulSearchingSettingsTable.shouldShowTrackPositionInAlbumView

    fun toSoulSearchingSettings(): SoulSearchingSettings =
        SoulSearchingSettings(
            isMusicFileModificationOn = isMusicFileModificationOn,
            musicSortSetup = SortSetup(
                type = SortType.fromOrDefault(musicSortType),
                direction = SortDirection.fromOrDefault(musicSortDirection),
            ),
            albumSortSetup = SortSetup(
                type = SortType.fromOrDefault(albumSortType),
                direction = SortDirection.fromOrDefault(albumSortDirection),
            ),
            artistSortSetup = SortSetup(
                type = SortType.fromOrDefault(artistSortType),
                direction = SortDirection.fromOrDefault(artistSortDirection),
            ),
            playlistSortSetup = SortSetup(
                type = SortType.fromOrDefault(playlistSortType),
                direction = SortDirection.fromOrDefault(playlistSortDirection),
            ),
            playerSettings = SoulSearchingSettings.PlayerSettings(
                isPlayerSwipeEnabled = isPlayerSwipeEnabled,
                soulMixTotalByList = soulMixTotalByList,
                isRewindEnabled = isRewindEnabled,
                isMinimisedSongProgressionShown = isMinimisedSongProgressionShown,
                isRemoteLyricsFetchEnabled = isRemoteLyricsFetchEnabled,
            ),
            colorTheme = SoulSearchingSettings.ColorTheme(
                type = ColorThemeType.fromRawOrDefault(colorThemeType),
                dynamicPlayerTheme = dynamicPlayerTheme,
                dynamicPlaylistTheme = dynamicPlaylistTheme,
                dynamicOtherViewsTheme = dynamicOtherViewsTheme,
                forceDarkTheme = forceDarkTheme,
                forceLightTheme = forceLightTheme,
            ),
            mainPage = SoulSearchingSettings.MainPage(
                isQuickAccessTabShown = isQuickAccessTabShown,
                isArtistsTabShown = isArtistsTabShown,
                isAlbumsTabShown = isAlbumsTabShown,
                isPlaylistsTabShown = isPlaylistsTabShown,
                isFoldersTabShown = isFoldersTabShown,
                areMusicsByMonthShown = areMusicsByMonthShown,
                isUsingVerticalAccessBar = isUsingVerticalAccessBar,
            ),
            release = SoulSearchingSettings.Release(
                isFetchReleaseFromGithubEnabled = isFetchReleaseFromGithubEnabled,
            ),
            album = SoulSearchingSettings.Album(
                shouldShowTrackPositionInAlbumView = shouldShowTrackPositionInAlbumView,
            ),
        )
}

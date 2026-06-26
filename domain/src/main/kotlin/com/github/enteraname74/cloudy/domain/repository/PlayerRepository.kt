package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.player.PlayedList
import com.github.enteraname74.cloudy.domain.model.player.PlayedListUpdate
import com.github.enteraname74.cloudy.domain.model.player.PlayerMusic
import com.github.enteraname74.cloudy.domain.model.player.PlayerUser
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

interface PlayerRepository {
    suspend fun create(
        hostId: Uuid,
        deviceId: String,
        initialMusicIds: List<String>,
    ): PlayedList

    suspend fun update(
        playedListUpdate: PlayedListUpdate,
    ): PlayedList

    suspend fun addUser(
        userId: Uuid,
        listId: Uuid,
        deviceId: String,
    )

    suspend fun removeUser(
        userId: Uuid,
        listId: Uuid,
        deviceId: String,
    )

    suspend fun deleteAllOfUser(
        userId: Uuid
    )

    suspend fun deleteIfEmpty(listId: Uuid) : Boolean

    suspend fun delete(listId: Uuid)

    /**
     * Owner of a played list is designated with the following rules:
     * - must be in the list
     * - must be connected (see PlayerUser.Status)
     * - is the oldest user respecting the previous conditions
     */
    suspend fun isOwnerOfPlayedList(
        userId: Uuid,
        listId: Uuid,
        deviceId: String,
    ): Boolean

    suspend fun isUserInList(
        userId: Uuid,
        listId: Uuid,
        deviceId: String,
    ): Boolean

    suspend fun getUser(
        userId: Uuid,
        listId: Uuid,
        deviceId: String
    ): PlayerUser?

    suspend fun setUserStatus(
        userId: Uuid,
        listId: Uuid,
        deviceId: String,
        status: PlayerUser.Status,
    )

    suspend fun getFromCode(
        code: String,
    ): PlayedList?

    suspend fun getFromUser(
        id: Uuid,
        userId: Uuid,
        deviceId: String,
    ): PlayedList?

    suspend fun getAllMusicOfList(
        listId: Uuid,
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<PlayerMusic>

    suspend fun isUserInPlayedList(
        userId: Uuid,
        listId: Uuid,
        deviceId: String,
    ): Boolean

    suspend fun addMusics(
        userId: Uuid,
        listId: Uuid,
        musics: List<Music>
    )

    /**
     * @return true if the played list was deleted because of empty songs, false otherwise
     */
    suspend fun removeMusics(
        userId: Uuid,
        listId: Uuid,
        musicIds: List<String>,
    ) : Boolean

    suspend fun hasReadPermission(
        userId: Uuid,
        musicId: String
    ): Boolean

    suspend fun getExistingMusicIds(
        listId: Uuid,
        musicIds: List<String>
    ): List<String>

    suspend fun setCurrentMusic(
        musicId: String,
        listId: Uuid,
        userId: Uuid,
    )
}
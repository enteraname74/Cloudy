package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.player.PlayedList
import com.github.enteraname74.cloudy.domain.model.player.PlayedListUpdate
import com.github.enteraname74.cloudy.domain.model.player.PlayerMusic
import com.github.enteraname74.cloudy.domain.model.player.PlayerUser
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

interface PlayerDataSource {
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
        deviceId: String,
        listId: Uuid,
    )

    suspend fun removeUser(
        userId: Uuid,
        deviceId: String,
        listId: Uuid,
    )

    suspend fun deleteAllOfUser(
        userId: Uuid
    )

    suspend fun deleteIfEmpty(listId: Uuid)

    suspend fun delete(listId: Uuid)

    suspend fun isOwnerOfPlayedList(
        userId: Uuid,
        deviceId: String,
        listId: Uuid,
    ): Boolean

    suspend fun isUserInList(
        userId: Uuid,
        listId: Uuid,
        deviceId: String,
    ): Boolean

    suspend fun getFromCode(
        code: String,
    ): PlayedList?

    suspend fun getAllMusicOfList(
        listId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<PlayerMusic>

    suspend fun getFromUser(
        id: Uuid,
        userId: Uuid,
        deviceId: String,
    ): PlayedList?

    suspend fun getAllAfterCurrentMusic(
        listId: Uuid,
    ): List<PlayerMusic>

    suspend fun getCurrentMusic(listId: Uuid): PlayerMusic?

    suspend fun getExistingMusicIds(
        listId: Uuid,
        musicIds: List<String>
    ): List<String>

    suspend fun getAllUsersByJoinedAt(
        listId: Uuid,
    ): List<PlayerUser>

    suspend fun upsertMusics(
        playerMusics: List<PlayerMusic>,
    )

    suspend fun getNextMusic(
        listId: Uuid,
        idsToSkip: List<String>,
    ): PlayerMusic?

    suspend fun deleteMusics(
        listId: Uuid,
        musicIds: List<String>
    )

    suspend fun areAnyMusicAfterCurrentOne(
        listId: Uuid,
        musicIds: List<String>
    ): Boolean
}
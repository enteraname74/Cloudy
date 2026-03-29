package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.player.PlayedList
import com.github.enteraname74.cloudy.domain.model.player.PlayedListUpdate
import com.github.enteraname74.cloudy.domain.model.player.PlayerMusic
import com.github.enteraname74.cloudy.domain.repository.PlayerRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.PlayerDataSource
import kotlin.uuid.Uuid

class PlayerRepositoryImpl(
    private val playerDataSource: PlayerDataSource
) : PlayerRepository {
    override suspend fun create(
        hostId: Uuid,
        deviceId: String,
        initialMusicIds: List<String>
    ): PlayedList =
        playerDataSource.create(
            hostId = hostId,
            initialMusicIds = initialMusicIds,
            deviceId = deviceId,
        )

    override suspend fun update(playedListUpdate: PlayedListUpdate): PlayedList =
        playerDataSource.update(playedListUpdate)

    override suspend fun addUser(
        userId: Uuid,
        listId: Uuid,
        deviceId: String,
    ) {
        playerDataSource.addUser(
            userId = userId,
            listId = listId,
            deviceId = deviceId,
        )
    }

    override suspend fun removeUser(
        userId: Uuid,
        listId: Uuid,
        deviceId: String,
    ) {
        playerDataSource.removeUser(
            userId = userId,
            listId = listId,
            deviceId = deviceId,
        )
    }

    override suspend fun deleteAllOfUser(userId: Uuid) {
        playerDataSource.deleteAllOfUser(
            userId = userId,
        )
    }

    override suspend fun deleteIfEmpty(listId: Uuid) {
        playerDataSource.deleteIfEmpty(listId)
    }

    override suspend fun delete(listId: Uuid) {
        playerDataSource.delete(listId)
    }

    override suspend fun isOwnerOfPlayedList(
        userId: Uuid,
        listId: Uuid,
        deviceId: String,
    ): Boolean =
        playerDataSource.isOwnerOfPlayedList(
            userId = userId,
            listId = listId,
            deviceId = deviceId,
        )

    override suspend fun isUserInList(
        userId: Uuid,
        listId: Uuid,
        deviceId: String
    ): Boolean =
        playerDataSource.isUserInList(
            userId = userId,
            listId = listId,
            deviceId = deviceId,
        )

    override suspend fun getFromCode(code: String): PlayedList? =
        playerDataSource.getFromCode(code = code)

    override suspend fun getAllMusicOfList(
        listId: Uuid,
        paginatedRequest: PaginatedRequest
    ): List<PlayerMusic> =
        playerDataSource.getAllMusicOfList(
            listId = listId,
            paginatedRequest = paginatedRequest,
        )

    override suspend fun clearAndSetMusics(
        listId: Uuid,
        musics: List<PlayerMusic>
    ) {
        playerDataSource.clearAndSetMusics(
            listId = listId,
            musics = musics
        )
    }

    override suspend fun getFromUser(
        id: Uuid,
        userId: Uuid,
        deviceId: String,
    ): PlayedList? =
        playerDataSource.getFromUser(
            id = id,
            userId = userId,
            deviceId = deviceId,
        )

    override suspend fun isUserInPlayedList(
        userId: Uuid,
        listId: Uuid,
        deviceId: String,
    ): Boolean =
        playerDataSource.getFromUser(
            id = listId,
            userId = userId,
            deviceId = deviceId,
        ) != null
}
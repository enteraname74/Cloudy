package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.player.PlayedList
import com.github.enteraname74.cloudy.domain.model.player.PlayedListUpdate
import com.github.enteraname74.cloudy.domain.model.player.PlayerMusic
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.repository.PlayerRepository
import com.github.enteraname74.cloudy.domain.repository.UserRepository
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.domain.util.toCloudyResult
import com.github.enteraname74.cloudy.domain.util.toCloudySuccess
import com.github.enteraname74.cloudy.logging.CloudyLogger
import kotlin.uuid.Uuid

class PlayerService(
    private val musicRepository: MusicRepository,
    private val playerRepository: PlayerRepository,
    private val userRepository: UserRepository,
) {

    suspend fun create(
        hostId: Uuid,
        deviceId: String,
        initialMusicIds: List<String>,
        routingMessages: RoutingMessages,
    ): CloudyResult<PlayedList> {
        if (userRepository.getFromId(hostId) == null) {
            return CloudyResult.Error(routingMessages.CANNOT_FIND_USER)
        }

        val existingMusicIds = musicRepository.getExistingIds(
            userId = hostId,
            ids = initialMusicIds,
        )

        return playerRepository.create(
            hostId = hostId,
            deviceId = deviceId,
            initialMusicIds = existingMusicIds,
        ).toCloudyResult()
    }

    suspend fun update(
        userId: Uuid,
        deviceId: String,
        playedListUpdate: PlayedListUpdate,
        routingMessages: RoutingMessages,
    ): CloudyResult<PlayedList> {
        val isOwner: Boolean = playerRepository.isOwnerOfPlayedList(
            userId = userId,
            listId = playedListUpdate.listId,
            deviceId = deviceId
        )
        return if (isOwner) {
            val updatedPlayedList: PlayedList = playerRepository.update(playedListUpdate)
            CloudyResult.Success(updatedPlayedList)
        } else {
            CloudyResult.Error(routingMessages.NOT_OWNER_OF_PLAYED_LIST)
        }
    }

    suspend fun join(
        userId: Uuid,
        code: String,
        deviceId: String,
        routingMessages: RoutingMessages,
    ): CloudyResult<Uuid> {
        val playedList: PlayedList = playerRepository.getFromCode(code) ?: return CloudyResult.Error(
            routingMessages.PLAYED_LIST_NOT_FOUND,
        )
        val alreadyInList: Boolean = playerRepository.isUserInList(
            userId = userId,
            listId = playedList.id,
            deviceId = deviceId,
        )
        if (alreadyInList) {
            return CloudyResult.Error(routingMessages.USER_ALREADY_IN_PLAYED_LIST)
        }

        playerRepository.addUser(
            userId = userId,
            listId = playedList.id,
            deviceId = deviceId,
        )

        return CloudyResult.Success(playedList.id)
    }

    suspend fun getPlayedList(
        listId: Uuid,
        userId: Uuid,
        deviceId: String,
        routingMessages: RoutingMessages,
    ): CloudyResult<PlayedList> {
        val playedList: PlayedList? = playerRepository.getFromUser(
            id = listId,
            userId = userId,
            deviceId = deviceId,
        )

        if (playedList == null || playedList.isEmpty()) {
            return CloudyResult.Error(routingMessages.PLAYED_LIST_NOT_FOUND_OR_NOT_IN_LIST)
        }

        return CloudyResult.Success(playedList)
    }

    suspend fun getMusics(
        listId: Uuid,
        userId: Uuid,
        deviceId: String,
        paginatedRequest: PaginatedRequest,
        routingMessages: RoutingMessages,
    ): CloudyResult<List<PlayerMusic>> {
        val isInList: Boolean = playerRepository.isUserInPlayedList(
            userId = userId,
            listId = listId,
            deviceId = deviceId,
        )
        if (!isInList) {
            return CloudyResult.Error(routingMessages.PLAYED_LIST_NOT_FOUND_OR_NOT_IN_LIST)
        }

        return playerRepository.getAllMusicOfList(
            listId = listId,
            paginatedRequest = paginatedRequest,
        ).toCloudySuccess()
    }

    suspend fun delete(
        listId: Uuid,
        userId: Uuid,
        deviceId: String,
        routingMessages: RoutingMessages,
    ): CloudyResult<Unit> {
        val isOwner: Boolean = playerRepository.isOwnerOfPlayedList(
            userId = userId,
            listId = listId,
            deviceId = deviceId,
        )

        if (!isOwner) {
            return CloudyResult.Error(routingMessages.NOT_OWNER_OF_PLAYED_LIST)
        }
        playerRepository.delete(listId)

        return CloudyResult.Success(Unit)
    }

    suspend fun remove(
        userId: Uuid,
        listId: Uuid,
        deviceId: String,
        deviceIdToRemove: String,
        userIdToRemove: Uuid,
        routingMessages: RoutingMessages,
    ): CloudyResult<Unit> {
        // A user can quit a played list or the host can remove a user.
        val userQuitting = userId == userIdToRemove && deviceId == deviceIdToRemove
        val isOwner = playerRepository.isOwnerOfPlayedList(
            userId = userId,
            listId = listId,
            deviceId = deviceId,
        )

        return if (userQuitting || isOwner) {
            playerRepository.removeUser(
                userId = userIdToRemove,
                listId = listId,
                deviceId = deviceIdToRemove,
            )
            playerRepository.deleteIfEmpty(listId)
            CloudyResult.Success(Unit)
        } else {
            CloudyResult.Error(routingMessages.NO_PERMISSION_TO_REMOVE_USER_IN_PLAYED_LIST)
        }
    }
}
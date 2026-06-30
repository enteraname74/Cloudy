package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.player.PlayedList
import com.github.enteraname74.cloudy.domain.model.player.PlayedListUpdate
import com.github.enteraname74.cloudy.domain.model.player.PlayerMusic
import com.github.enteraname74.cloudy.domain.model.player.PlayerUser
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.repository.PlayerRepository
import com.github.enteraname74.cloudy.domain.repository.UserRepository
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.domain.util.toCloudyResult
import com.github.enteraname74.cloudy.domain.util.toCloudySuccess
import com.github.enteraname74.cloudy.domain.websocket.PlayerUserCommunication
import kotlin.uuid.Uuid

class PlayerService(
    private val musicRepository: MusicRepository,
    private val playerRepository: PlayerRepository,
    private val userRepository: UserRepository,
    private val playerUserCommunication: PlayerUserCommunication,
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

        val existingMusicIds = musicRepository.getExistingIdsOfUser(
            userId = hostId,
            ids = initialMusicIds,
        )

        if (existingMusicIds.isEmpty()) return CloudyResult.Error(routingMessages.SONG_NOT_POSSESSED_BY_USER)

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
            playerUserCommunication.broadcastEvent(
                listId = playedListUpdate.listId,
                exceptDeviceId = deviceId,
                event = PlayerUserCommunication.Event.SyncPlayedList,
            )
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
    ): CloudyResult<PlayedList> {
        val playedList: PlayedList = playerRepository.getFromCode(code) ?: return CloudyResult.Error(
            routingMessages.PLAYED_LIST_NOT_FOUND,
        )
        val user: PlayerUser? = playerRepository.getUser(
            userId = userId,
            listId = playedList.id,
            deviceId = deviceId,
        )
        // Nothing to do, user is already in the played list and connected

        when (user?.status) {
            // Nothing to do, user is already in the played list and connected
            PlayerUser.Status.Connected -> {
                return CloudyResult.Success(playedList)
            }
            // We will need to register the user as connected again
            PlayerUser.Status.Disconnected -> {
                playerRepository.setUserStatus(
                    userId = userId,
                    listId = playedList.id,
                    deviceId = deviceId,
                    status = PlayerUser.Status.Connected,
                )
            }
            // Else, we will add the fresh user to the played list
            null -> {
                playerRepository.addUser(
                    userId = userId,
                    listId = playedList.id,
                    deviceId = deviceId,
                )
            }
        }

        // For a new user or a reconnection, we will broadcast a sync event
        playerUserCommunication.broadcastEvent(
            listId = playedList.id,
            exceptDeviceId = deviceId,
            event = PlayerUserCommunication.Event.SyncPlayedList,
        )

        val updatedList = playerRepository.getFromCode(code)
        return if (updatedList != null) {
            CloudyResult.Success(updatedList)
        } else {
            CloudyResult.Error()
        }
    }

    suspend fun setUserStatus(
        userId: Uuid,
        listId: Uuid,
        deviceId: String,
        status: PlayerUser.Status,
    ) {
        playerRepository.setUserStatus(
            userId = userId,
            listId = listId,
            deviceId = deviceId,
            status = status,
        )
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
            userId = userId,
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
        playerUserCommunication.broadcastEvent(
            listId = listId,
            exceptDeviceId = deviceId,
            event = PlayerUserCommunication.Event.PlayedListDeleted,
        )

        return CloudyResult.Success(Unit)
    }

    suspend fun removeOrDisconnect(
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

        if (!userQuitting && !isOwner) return CloudyResult.Error(routingMessages.NO_PERMISSION_TO_REMOVE_USER_IN_PLAYED_LIST)

        /*
        If the user quits himself the played list, we will just mark him as disconnected.
        Else, if the user is removed from the host, we will delete him.
         */
        if (userQuitting) {
            /*
            We must check if the user was the admin.
            If so, we must pause the played list,
            ad we do not want the playback to continue directly on someone else phone.
             */
            val userQuittingIsOwner = playerRepository.isOwnerOfPlayedList(
                userId = userIdToRemove,
                listId = listId,
                deviceId = deviceIdToRemove,
            )
            if (userQuittingIsOwner) {
                playerRepository.update(
                    playedListUpdate = PlayedListUpdate(
                        listId = listId,
                        state = PlayedList.State.Paused,
                    )
                )
            }

            playerRepository.setUserStatus(
                userId = userIdToRemove,
                listId = listId,
                deviceId = deviceIdToRemove,
                status = PlayerUser.Status.Disconnected,
            )
        } else {
            playerRepository.removeUser(
                userId = userIdToRemove,
                listId = listId,
                deviceId = deviceIdToRemove,
            )
        }

        val playedListDeleted = playerRepository.deleteIfEmpty(listId)

        // If all users are disconnected, we will pause the list.
        if (!playedListDeleted) {
            pauseIfAllDisconnected(listId)
        }
        playerUserCommunication.broadcastEvent(
            listId = listId,
            event = if (playedListDeleted) {
                PlayerUserCommunication.Event.PlayedListDeleted
            } else {
                PlayerUserCommunication.Event.SyncPlayedList
            },
        )
        return CloudyResult.Success(Unit)
    }

    private suspend fun pauseIfAllDisconnected(listId: Uuid) {
        val list: PlayedList = playerRepository.getPlayedList(listId) ?: return
        val allDisconnected: Boolean = list.users.all { it.status == PlayerUser.Status.Disconnected }
        if (allDisconnected) {
            playerRepository.update(
                playedListUpdate = PlayedListUpdate(
                    listId = listId,
                    state = PlayedList.State.Paused,
                )
            )
        }
    }

    suspend fun addMusics(
        userId: Uuid,
        deviceId: String,
        listId: Uuid,
        musicIds: List<String>,
        routingMessages: RoutingMessages,
    ): CloudyResult<Unit> {

        val isInList: Boolean = playerRepository.isUserInPlayedList(
            userId = userId,
            listId = listId,
            deviceId = deviceId,
        )
        if (!isInList) {
            return CloudyResult.Error(routingMessages.PLAYED_LIST_NOT_FOUND_OR_NOT_IN_LIST)
        }
        val musics: List<Music> = musicRepository.getAll(musicIds)

        if (musics.isEmpty()) return CloudyResult.Success(Unit)

        playerRepository.addMusics(
            listId = listId,
            musics = musics,
        )
        playerUserCommunication.broadcastEvent(
            listId = listId,
            exceptDeviceId = deviceId,
            event = PlayerUserCommunication.Event.SyncMusics,
        )
        return CloudyResult.Success(Unit)
    }

    suspend fun removeMusics(
        userId: Uuid,
        deviceId: String,
        listId: Uuid,
        musicIds: List<String>,
        routingMessages: RoutingMessages,
    ): CloudyResult<Unit> {

        val isInList: Boolean = playerRepository.isUserInPlayedList(
            userId = userId,
            listId = listId,
            deviceId = deviceId,
        )
        if (!isInList) {
            return CloudyResult.Error(routingMessages.PLAYED_LIST_NOT_FOUND_OR_NOT_IN_LIST)
        }

        val isOwner = playerRepository.isOwnerOfPlayedList(
            userId = userId,
            listId = listId,
            deviceId = deviceId,
        )
        val availableMusicIds: List<String> = if (isOwner) {
            musicRepository.getExistingIds(musicIds)
        } else {
            musicRepository.getExistingIdsOfUser(
                userId = userId,
                ids = musicIds,
            )
        }
        if (availableMusicIds.isEmpty()) return CloudyResult.Success(Unit)

        playerRepository.removeMusics(
            musicIds = musicRepository.getExistingIds(musicIds),
            listIds = listOf(listId),
            // Broadcast will be sent to all users (for playlist deletion or update event)
            socketDeviceIdToIgnore = null,
        )
        return CloudyResult.Success(Unit)
    }

    suspend fun getDeletedMusicIds(
        listId: Uuid,
        userId: Uuid,
        deviceId: String,
        routingMessages: RoutingMessages,
        musicIds: List<String>
    ): CloudyResult<List<String>> {
        val isInList: Boolean = playerRepository.isUserInPlayedList(
            userId = userId,
            listId = listId,
            deviceId = deviceId,
        )
        if (!isInList) {
            return CloudyResult.Error(routingMessages.PLAYED_LIST_NOT_FOUND_OR_NOT_IN_LIST)
        }

        val existingIds: List<String> = playerRepository.getExistingMusicIds(
            listId = listId,
            musicIds = musicIds,
        )

        return CloudyResult.Success(musicIds - existingIds.toSet())
    }

    suspend fun updateCurrentMusic(
        listId: Uuid,
        userId: Uuid,
        deviceId: String,
        routingMessages: RoutingMessages,
        musicId: String,
    ): CloudyResult<Unit> {
        val isInList: Boolean = playerRepository.isUserInPlayedList(
            userId = userId,
            listId = listId,
            deviceId = deviceId,
        )
        if (!isInList) {
            return CloudyResult.Error(routingMessages.PLAYED_LIST_NOT_FOUND_OR_NOT_IN_LIST)
        }

        playerRepository.setCurrentMusic(
            listId = listId,
            musicId = musicId,
            userId = userId,
        )

        return CloudyResult.Success(Unit)
    }
}
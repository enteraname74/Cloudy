package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.player.PlayedList
import com.github.enteraname74.cloudy.domain.model.player.PlayedListUpdate
import com.github.enteraname74.cloudy.domain.model.player.PlayerMusic
import com.github.enteraname74.cloudy.domain.model.player.PlayerUser
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


    override suspend fun addMusics(
        userId: Uuid,
        listId: Uuid,
        musics: List<Music>
    ) {
        val musicsAfterCurrent: List<PlayerMusic> = playerDataSource.getAllAfterCurrentMusic(
            listId = listId,
        )

        val alreadyExistingMusicIds: List<String> = playerDataSource.getExistingMusicIds(
            listId = listId,
            musicIds = musics.map { it.fingerprint }
        )

        /*
        We build an initial temporary list of PlayerMusic with the ones to add.
        We filter them to avoid adding ones already in the played list.
         */
        val temporaryPlayerMusics: List<PlayerMusic> = musics
            .filter { music ->
                alreadyExistingMusicIds.none { it == music.fingerprint }
            }
            .map {
                PlayerMusic(
                    playedListId = listId,
                    music = it,
                    order = Double.MAX_VALUE,
                    lastPlayedMillis = null,
                )
            }


        // We merge the existing list and the new musics to add into a temporary list.
        val temporaryList: List<PlayerMusic> = musicsAfterCurrent + temporaryPlayerMusics

        // We group the musics by users, to help with the ordering of the new list.
        val byUsers: Map<Uuid, ArrayDeque<PlayerMusic>> = temporaryList
            .groupBy { it.music.userId }
            .mapValues { (_, musics) -> ArrayDeque(musics) }

        val users: List<PlayerUser> = playerDataSource.getAllUsersByJoinedAt(listId)

        val currentMusic = playerDataSource.getCurrentMusic(listId) ?: return
        var currentOrder: Double = currentMusic.order + 1
        val result = mutableListOf<PlayerMusic>()
        while (true) {
            var addedAtLeastOne = false

            for (user in users) {
                val music = byUsers[user.id]?.removeFirstOrNull() ?: continue
                result.add(
                    music.copy(
                        order = currentOrder
                    )
                )
                currentOrder += 1.0
                addedAtLeastOne = true
            }

            if (!addedAtLeastOne) break
        }
        playerDataSource.upsertMusics(result)
    }
}
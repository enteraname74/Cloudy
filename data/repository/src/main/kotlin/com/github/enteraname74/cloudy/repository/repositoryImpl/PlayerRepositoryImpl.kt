package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.player.PlayedList
import com.github.enteraname74.cloudy.domain.model.player.PlayedListUpdate
import com.github.enteraname74.cloudy.domain.model.player.PlayerMusic
import com.github.enteraname74.cloudy.domain.model.player.PlayerUser
import com.github.enteraname74.cloudy.domain.repository.PlayerRepository
import com.github.enteraname74.cloudy.domain.util.DateUtils
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
        userId: Uuid,
        paginatedRequest: PaginatedRequest
    ): List<PlayerMusic> =
        playerDataSource.getAllMusicOfList(
            listId = listId,
            userId = userId,
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
        reorderMusicsInList(
            listId = listId,
            musics = temporaryList,
        )
    }

    private suspend fun reorderMusicsInList(
        listId: Uuid,
        musics: List<PlayerMusic>
    ) {
        if (musics.isEmpty()) return
        val currentMusic: PlayerMusic = playerDataSource.getCurrentMusic(listId) ?: return

        // We group the musics by users, to help with the ordering of the new list.
        val byUsers: Map<Uuid, ArrayDeque<PlayerMusic>> = musics
            .groupBy { it.music.userId }
            .mapValues { (_, musics) -> ArrayDeque(musics) }

        val users: List<PlayerUser> = playerDataSource.getAllUsersByJoinedAt(listId)
        if (users.isEmpty()) return

        /*
        We want to rotate the users list to start the distribution of musics with the next user after the current one.
        So, If we have user A - B - C - D, and the current music if of user C, the distribution order for the next ones will be :
        D - A - B - C
         */
        val currentUserIndex: Int = users.indexOfFirst { it.id == currentMusic.music.userId }

        val startIndex = if (currentUserIndex == -1) 0 else (currentUserIndex + 1) % users.size
        val rotatedUsers = users.drop(startIndex) + users.take(startIndex)

        var currentOrder: Double = currentMusic.order + 1
        val result = mutableListOf<PlayerMusic>()
        while (true) {
            var addedAtLeastOne = false

            for (user in rotatedUsers) {
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

    override suspend fun removeMusics(
        userId: Uuid,
        listId: Uuid,
        musicIds: List<String>
    ) {
        val currentMusic: PlayerMusic = playerDataSource.getCurrentMusic(listId) ?: return
        val currentMusicWillBeDeleted: Boolean = musicIds.contains(currentMusic.music.fingerprint)

        // If the current music will be deleted, we must change the current music.
        if (currentMusicWillBeDeleted) {
            val nextMusic: PlayerMusic? = playerDataSource.getNextMusic(
                listId = listId,
                idsToSkip = musicIds,
            )
            // If we can't find a next music to play, the played list is empty, so we delete the played list.
            if (nextMusic == null) {
                playerDataSource.delete(listId)
                return
            } else {
                // Else, we set it to be the new current music.
                playerDataSource.upsertMusics(
                    playerMusics = listOf(
                        nextMusic.copy(
                            lastPlayedMillis = DateUtils.now(),
                        )
                    )
                )
            }
        }

        // We then check if we should re-arrange musics order after the deletion of songs.
        val areAnyMusicToDeleteAfterCurrentOne: Boolean = playerDataSource.areAnyMusicAfterCurrentOne(
            listId = listId,
            musicIds = musicIds,
        )

        playerDataSource.deleteMusics(
            listId = listId,
            musicIds = musicIds,
        )

        val playedListDeleted: Boolean = playerDataSource.deleteIfEmpty(listId)

        if (areAnyMusicToDeleteAfterCurrentOne && !playedListDeleted) {
            reorderMusicsInList(
                listId = listId,
                musics = playerDataSource.getAllAfterCurrentMusic(
                    listId = listId,
                ),
            )
        }
    }

    override suspend fun hasReadPermission(userId: Uuid, musicId: String): Boolean =
        playerDataSource.hasReadPermission(
            userId = userId,
            musicId = musicId,
        )
}
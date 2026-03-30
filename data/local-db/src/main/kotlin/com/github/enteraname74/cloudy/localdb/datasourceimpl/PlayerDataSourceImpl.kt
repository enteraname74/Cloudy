package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.player.PlayedList
import com.github.enteraname74.cloudy.domain.model.player.PlayedListUpdate
import com.github.enteraname74.cloudy.domain.model.player.PlayerMusic
import com.github.enteraname74.cloudy.domain.model.player.PlayerUser
import com.github.enteraname74.cloudy.domain.model.player.SimplePlayerMusic
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.player.*
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.PlayerDataSource
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.uuid.Uuid

class PlayerDataSourceImpl : PlayerDataSource {
    private fun generateInviteCode(listId: Uuid): String =
        listId.toString().split("-").first()

    override suspend fun update(playedListUpdate: PlayedListUpdate): PlayedList =
        workTransaction {
            val entity = PlayedListEntity.findById(playedListUpdate.listId)!!
            entity.state = playedListUpdate.state.value
            entity.lastUpdateAt = DateUtils.now()

            entity.toPlayedList()
        }

    override suspend fun create(
        hostId: Uuid,
        deviceId: String,
        initialMusicIds: List<String>
    ): PlayedList =
        workTransaction {
            val listId = Uuid.random()

            val entry = PlayedListEntity.new(listId) {
                inviteCode = generateInviteCode(listId)
                state = PlayedList.State.Paused.value
            }
            PlayedListUserTable.insert(
                userId = hostId,
                deviceId = deviceId,
                listId = listId,
            )
            PlayedListMusicTable.upsertAllSimple(
                musics = initialMusicIds.mapIndexed { index, id ->
                    SimplePlayerMusic(
                        playedListId = listId,
                        musicId = id,
                        order = index.toDouble(),
                        lastPlayedMillis = if (index == 0) DateUtils.now() else null,
                    )
                }
            )
            entry.toPlayedList()
        }

    override suspend fun deleteAllOfUser(userId: Uuid) {
        workTransaction {
            val plu = PlayedListUserTable.alias("plu")
            val plu2 = PlayedListUserTable.alias("plu2")

            val ownedListIds = plu
                .select(plu[PlayedListUserTable.listId])
                .where {
                    (plu[PlayedListUserTable.userId] eq userId) and
                            notExists(
                                plu2.selectAll().where {
                                    (plu2[PlayedListUserTable.listId] eq plu[PlayedListUserTable.listId]) and
                                            (plu2[PlayedListUserTable.joinedAt] less plu[PlayedListUserTable.joinedAt])
                                }
                            )
                }

            PlayedListTable.deleteWhere {
                PlayedListTable.id inSubQuery ownedListIds
            }
        }
    }


    override suspend fun deleteIfEmpty(listId: Uuid) {
        workTransaction {
            PlayedListTable.deleteWhere {
                (this.id eq listId) and notExists(
                    PlayedListUserTable.selectAll().where {
                        PlayedListUserTable.listId eq listId
                    }
                )
            }
        }
    }

    override suspend fun delete(listId: Uuid) {
        workTransaction {
            PlayedListTable.deleteWhere { this.id eq listId }
        }
    }

    override suspend fun isOwnerOfPlayedList(
        userId: Uuid,
        deviceId: String,
        listId: Uuid,
    ): Boolean =
        workTransaction {
            val firstJoiner = PlayedListUserEntity.find {
                PlayedListUserTable.listId eq listId
            }
                .orderBy(PlayedListUserTable.joinedAt to SortOrder.ASC)
                .limit(1)
                .firstOrNull()

            firstJoiner?.user?.id?.value == userId && firstJoiner.deviceId == deviceId
        }

    override suspend fun isUserInList(
        userId: Uuid,
        listId: Uuid,
        deviceId: String
    ): Boolean =
        workTransaction {
            !PlayedListUserEntity.find {
                (PlayedListUserTable.listId eq listId) and
                        (PlayedListUserTable.userId eq userId) and
                        (PlayedListUserTable.deviceId eq deviceId)
            }.empty()
        }

    override suspend fun getFromCode(code: String): PlayedList? =
        workTransaction {
            PlayedListEntity.find {
                PlayedListTable.inviteCode eq code
            }.firstOrNull()?.toPlayedList()
        }

    override suspend fun getAllMusicOfList(
        listId: Uuid,
        paginatedRequest: PaginatedRequest
    ): List<PlayerMusic> =
        workTransaction {
            PlayedListMusicEntity
                .find {
                    PlayedListMusicTable.listId eq listId
                }
                .orderBy(Pair(PlayedListMusicTable.order, SortOrder.ASC))
                .paginated(paginatedRequest)
                .map { it.toPlayerMusic() }
        }
    override suspend fun addUser(
        userId: Uuid,
        deviceId: String,
        listId: Uuid,
    ) {
        workTransaction {
            PlayedListUserTable.insert(
                userId = userId,
                listId = listId,
                deviceId = deviceId,
            )
        }
    }

    override suspend fun removeUser(
        userId: Uuid,
        deviceId: String,
        listId: Uuid,
    ) {
        workTransaction {
            PlayedListUserTable.deleteWhere {
                (this.listId eq listId) and (this.userId eq userId) and (this.deviceId eq deviceId)
            }
        }
    }

    override suspend fun getFromUser(
        id: Uuid,
        userId: Uuid,
        deviceId: String,
    ): PlayedList? =
        workTransaction {
            (PlayedListUserTable innerJoin PlayedListTable)
                .select(PlayedListTable.columns)
                .where {
                    (PlayedListUserTable.listId eq id) and
                            (PlayedListUserTable.userId eq userId) and
                            (PlayedListUserTable.deviceId eq deviceId)
                }
                .limit(1)
                .firstOrNull()
                ?.let(PlayedListEntity::wrapRow)
                ?.toPlayedList()
        }

    override suspend fun getAllAfterCurrentMusic(listId: Uuid): List<PlayerMusic> =
        workTransaction {
            val current = PlayedListMusicTable
                .selectAll()
                .where { PlayedListMusicTable.listId eq listId }
                .orderBy(PlayedListMusicTable.lastPlayedMillis to SortOrder.DESC)
                .limit(1)
                .firstOrNull()

            val currentOrder = current?.get(PlayedListMusicTable.order) ?: return@workTransaction emptyList()

            PlayedListMusicEntity.find {
                (PlayedListMusicTable.listId eq listId) and
                        (PlayedListMusicTable.order greater currentOrder)
            }
                .orderBy(PlayedListMusicTable.order to SortOrder.ASC)
                .map { it.toPlayerMusic() }
        }

    override suspend fun getCurrentMusic(listId: Uuid): PlayerMusic? =
        workTransaction {
            PlayedListMusicEntity
                .find { (PlayedListMusicTable.listId eq listId) }
                .orderBy(Pair(PlayedListMusicTable.lastPlayedMillis, SortOrder.DESC_NULLS_LAST))
                .limit(1)
                .firstOrNull()
                ?.toPlayerMusic()
        }

    override suspend fun getExistingMusicIds(
        listId: Uuid,
        musicIds: List<String>
    ): List<String> = workTransaction {
        PlayedListMusicTable
            .select(PlayedListMusicTable.musicId)
            .where {
                (PlayedListMusicTable.listId eq listId) and
                        (PlayedListMusicTable.musicId inList musicIds)
            }.mapNotNull { it.getOrNull(PlayedListMusicTable.musicId)?.value }
    }

    override suspend fun getAllUsersByJoinedAt(listId: Uuid): List<PlayerUser> =
        workTransaction {
            PlayedListUserEntity.find {
                PlayedListUserTable.listId eq listId
            }.orderBy(Pair(PlayedListUserTable.joinedAt, SortOrder.ASC))
                .distinctBy { it.user.id }
                .map { it.toPlayerUser() }
        }

    override suspend fun upsertMusics(playerMusics: List<PlayerMusic>) {
        workTransaction {
            PlayedListMusicTable.upsertAll(playerMusics)
        }
    }
}
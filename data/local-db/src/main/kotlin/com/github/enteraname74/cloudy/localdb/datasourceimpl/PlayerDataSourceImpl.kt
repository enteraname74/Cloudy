package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.player.*
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.player.*
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
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

    override suspend fun getPlayedList(listId: Uuid): PlayedList? =
        workTransaction {
            PlayedListEntity.findById(listId)?.toPlayedList()
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

    override suspend fun getMusicIdsOfUser(
        userId: Uuid,
        listId: Uuid
    ): List<String> =
        workTransaction {
            PlayedListMusicEntity.find {
                (PlayedListMusicTable.listId eq listId)
            }.filter { it.music.userId == userId }
                .map { it.music.id.value }
        }

    override suspend fun deleteIfEmpty(listId: Uuid): Boolean =
        workTransaction {
            PlayedListTable.deleteWhere {
                (this.id eq listId) and
                        (
                                notExists(
                                    PlayedListUserTable.selectAll().where {
                                        PlayedListUserTable.listId eq listId
                                    }
                                ) or notExists(
                                    PlayedListMusicTable.selectAll().where {
                                        PlayedListMusicTable.listId eq listId
                                    }
                                )
                                )
            } > 0
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
                (PlayedListUserTable.listId eq listId)
            }
                .orderBy(
                    PlayedListUserTable.status to SortOrder.ASC,
                            PlayedListUserTable.joinedAt to SortOrder.ASC
                )
                .limit(1)
                .firstOrNull()

            firstJoiner?.user?.id?.value == userId && firstJoiner.deviceId == deviceId
        }

    private fun getUserEntity(
        userId: Uuid,
        listId: Uuid,
        deviceId: String
    ): PlayedListUserEntity? =
        PlayedListUserEntity.find {
            (PlayedListUserTable.listId eq listId) and
                    (PlayedListUserTable.userId eq userId) and
                    (PlayedListUserTable.deviceId eq deviceId)
        }.firstOrNull()

    override suspend fun isUserInList(
        userId: Uuid,
        listId: Uuid,
        deviceId: String
    ): Boolean =
        workTransaction {
            getUserEntity(
                userId = userId,
                listId = listId,
                deviceId = deviceId,
            ) != null
        }


    override suspend fun getUser(
        userId: Uuid,
        listId: Uuid,
        deviceId: String
    ): PlayerUser? =
        workTransaction {
            getUserEntity(
                userId = userId,
                listId = listId,
                deviceId = deviceId,
            )?.toPlayerUser()
        }


    override suspend fun setUserStatus(
        userId: Uuid,
        listId: Uuid,
        deviceId: String,
        status: PlayerUser.Status
    ) {
        workTransaction {
            val user = getUserEntity(
                userId = userId,
                listId = listId,
                deviceId = deviceId,
            )
            user?.let {
                it.status = status
            }
        }
    }

    override suspend fun getFromCode(code: String): PlayedList? =
        workTransaction {
            PlayedListEntity.find {
                PlayedListTable.inviteCode eq code
            }.firstOrNull()?.toPlayedList()
        }

    override suspend fun getAllMusicOfList(
        listId: Uuid,
        userId: Uuid,
        paginatedRequest: PaginatedRequest
    ): List<PlayerMusic> =
        workTransaction {
            PlayedListMusicEntity
                .find {
                    (PlayedListMusicTable.listId eq listId) and
                            (PlayedListMusicTable.lastUpdateAt updatedAfter paginatedRequest.lastUpdateAtMillis)
                }
                .orderBy(Pair(PlayedListMusicTable.order, SortOrder.ASC))
                .paginated(paginatedRequest)
                .map {
                    it.toPlayerMusic(
                        buildScope = { musicUserId ->
                            if (musicUserId == userId) {
                                Music.Scope.User
                            } else {
                                Music.Scope.SharedPlayedList
                            }
                        }
                    )
                }
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
                .map { it.toPlayerMusic(buildScope = { Music.Scope.User }) }
        }

    override suspend fun getCurrentMusic(listId: Uuid): PlayerMusic? =
        workTransaction {
            PlayedListMusicEntity
                .find { (PlayedListMusicTable.listId eq listId) }
                .orderBy(Pair(PlayedListMusicTable.lastPlayedMillis, SortOrder.DESC_NULLS_LAST))
                .limit(1)
                .firstOrNull()
                ?.toPlayerMusic(buildScope = { Music.Scope.User })
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

    private suspend fun getFirstMusic(
        listId: Uuid,
    ): PlayerMusic? = workTransaction {
        PlayedListMusicEntity
            .find { (PlayedListMusicTable.listId eq listId) }
            .orderBy(Pair(PlayedListMusicTable.order, SortOrder.ASC_NULLS_LAST))
            .limit(1)
            .firstOrNull()
            ?.toPlayerMusic(
                buildScope = { Music.Scope.User }
            )
    }

    override suspend fun getNextMusic(
        listId: Uuid,
        idsToSkip: List<String>
    ): PlayerMusic? = workTransaction {
        val current: PlayerMusic = getCurrentMusic(listId) ?: return@workTransaction null
        val next: PlayerMusic? = PlayedListMusicEntity
            .find {
                (PlayedListMusicTable.listId eq listId) and
                        (PlayedListMusicTable.order greater current.order)
            }
            .orderBy(Pair(PlayedListMusicTable.order, SortOrder.ASC_NULLS_LAST))
            .limit(1)
            .firstOrNull()
            ?.toPlayerMusic(
                buildScope = { Music.Scope.User }
            )

        next ?: getFirstMusic(listId)
    }

    override suspend fun deleteMusics(listId: Uuid, musicIds: List<String>) {
        workTransaction {
            PlayedListMusicTable.deleteWhere {
                (PlayedListMusicTable.listId eq listId) and
                        (PlayedListMusicTable.musicId inList musicIds)
            }
        }
    }

    override suspend fun areAnyMusicAfterCurrentOne(
        listId: Uuid,
        musicIds: List<String>
    ): Boolean = workTransaction {
        if (musicIds.isEmpty()) return@workTransaction false

        val currentOrder = getCurrentMusic(listId)?.order ?: return@workTransaction false

        PlayedListMusicTable
            .selectAll()
            .where {
                (PlayedListMusicTable.listId eq listId) and
                        (PlayedListMusicTable.order greater currentOrder) and
                        (PlayedListMusicTable.musicId inList musicIds)
            }
            .limit(1)
            .firstOrNull() != null
    }

    override suspend fun hasReadPermission(userId: Uuid, musicId: String): Boolean =
        workTransaction {
            PlayedListUserTable
                .join(
                    PlayedListMusicTable,
                    JoinType.INNER,
                    onColumn = PlayedListUserTable.listId,
                    otherColumn = PlayedListMusicTable.listId
                )
                .selectAll()
                .where {
                    (PlayedListUserTable.userId eq userId) and
                            (PlayedListMusicTable.musicId eq musicId)
                }
                .limit(1)
                .firstOrNull() != null
        }

    override suspend fun getPlayerMusic(
        musicId: String,
        listId: Uuid,
        userId: Uuid,
    ): PlayerMusic? =
        workTransaction {
            PlayedListMusicEntity.find {
                (PlayedListMusicTable.musicId eq musicId) and (
                        (PlayedListMusicTable.listId eq listId)
                        )
            }
                .firstOrNull()
                ?.toPlayerMusic(
                    buildScope = { musicUserId ->
                        if (musicUserId == userId) {
                            Music.Scope.User
                        } else {
                            Music.Scope.SharedPlayedList
                        }
                    }
                )
        }

    override suspend fun getPlayedListIdsOfMusics(musicIds: List<String>): List<Uuid> =
        workTransaction {
            PlayedListMusicEntity.find {
                (PlayedListMusicTable.musicId) inList musicIds
            }
                .distinctBy { it.listId }
                .map { it.listId.value }
        }
}
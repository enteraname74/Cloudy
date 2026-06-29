package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicEntity
import com.github.enteraname74.cloudy.localdb.table.MusicTable
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.MusicDataSource
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.uuid.Uuid

class MusicDataSourceImpl : MusicDataSource {
    override suspend fun upsert(music: Music): Music =
        workTransaction {
            MusicTable.upsertAll(listOf(music))
            MusicEntity.findById(music.fingerprint)!!.toMusic()
        }

    override suspend fun upsertAll(musics: List<Music>) {
        workTransaction {
            MusicTable.upsertAll(musics)
        }
    }

    override suspend fun getFromId(musicId: String): Music? =
        workTransaction {
            MusicEntity
                .find { MusicTable.id eq musicId }
                .firstOrNull()
                ?.toMusic()
        }

    override suspend fun getFromCoverPath(coverPath: String): Music? =
        workTransaction {
            MusicEntity
                .find { MusicTable.coverPath eq coverPath }
                .firstOrNull()
                ?.toMusic()
        }

    override suspend fun getAll(ids: List<String>): List<Music> =
        workTransaction {
            MusicEntity
                .find { MusicTable.id inList ids }
                .map { it.toMusic() }
        }

    override suspend fun deleteAll(ids: List<String>) {
        workTransaction {
            MusicTable.deleteWhere {
                id inList ids
            }
        }
    }

    override suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Music> =
        workTransaction {
            MusicEntity
                .find {
                    (MusicTable.userId eq userId) and
                            (MusicTable.lastUpdateAt updatedAfter paginatedRequest.lastUpdateAtMillis)
                }
                .paginated(paginatedRequest)
                .map { it.toMusic() }
        }

    override suspend fun isMusicPossessedByUser(userId: Uuid, musicId: String): Boolean =
        workTransaction {
            MusicEntity
                .find { (MusicTable.id eq musicId) and (MusicTable.userId eq userId) }
                .count() > 0
        }

    override suspend fun getFromFingerprint(fingerprint: String, userId: Uuid): Music? =
        workTransaction {
            MusicEntity
                .find { (MusicTable.id eq fingerprint) and (MusicTable.userId eq userId) }
                .firstOrNull()
                ?.toMusic()
        }

    override suspend fun allFromAlbum(albumId: Uuid): List<Music> =
        workTransaction {
            MusicEntity
                .find { MusicTable.albumId eq albumId }
                .map { it.toMusic() }
        }

    override suspend fun allFromArtist(artistId: Uuid): List<Music> =
        workTransaction {

            val query = MusicTable.join(
                    otherTable = MusicArtistTable,
                    joinType = JoinType.INNER,
                    onColumn = MusicTable.id,
                    otherColumn = MusicArtistTable.musicId,
                    additionalConstraint = { MusicArtistTable.artistId eq artistId }
                ).selectAll().withDistinct()

            MusicEntity
                .wrapRows(query)
                .map { it.toMusic() }
        }
}
package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.ext.ensureExist
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicId
import com.github.enteraname74.cloudy.domain.util.CommonFileUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicEntity
import com.github.enteraname74.cloudy.localdb.table.MusicTable
import com.github.enteraname74.cloudy.localdb.util.paginated
import com.github.enteraname74.cloudy.localdb.util.updatedAfter
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.CoverDataSource
import com.github.enteraname74.cloudy.repository.datasource.MusicDataSource
import com.github.enteraname74.cloudy.repository.datasource.UserDataSource
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.io.File
import kotlin.uuid.Uuid

class MusicDataSourceImpl(
    private val userDataSource: UserDataSource,
) : MusicDataSource {
    override suspend fun upsert(music: Music): Music =
        workTransaction {
            MusicTable.upsertAll(listOf(music))
            MusicEntity.findById(music.id.raw)!!.toMusic(
                buildScope = { Music.Scope.User }
            )
        }

    override suspend fun upsertAll(musics: List<Music>) {
        workTransaction {
            MusicTable.upsertAll(musics)
        }
    }

    override suspend fun getFromUser(
        musicId: MusicId,
        userId: Uuid
    ): Music? =
        workTransaction {
            MusicEntity
                .find { (MusicTable.id eq musicId.raw) and (MusicTable.userId eq userId) }
                .firstOrNull()
                ?.toMusic(buildScope = { Music.Scope.User })
        }

    override suspend fun getFromId(musicId: MusicId): Music? =
        workTransaction {
            MusicEntity
                .find { MusicTable.id eq musicId.raw }
                .firstOrNull()
                ?.toMusic(buildScope = { Music.Scope.User })
        }

    override suspend fun getFromCoverPath(coverPath: String): Music? =
        workTransaction {
            MusicEntity
                .find { MusicTable.coverPath eq coverPath }
                .firstOrNull()
                ?.toMusic(buildScope = { Music.Scope.User })
        }

    override suspend fun getAll(ids: List<MusicId>): List<Music> =
        workTransaction {
            val musics = MusicEntity
                .find { MusicTable.id inList ids.map { it.raw } }
                .map { it.toMusic(buildScope = { Music.Scope.User }) }

            val byIds = musics.associateBy { it.id }

            ids.mapNotNull { byIds[it] }
        }

    override suspend fun deleteAll(ids: List<MusicId>, userId: Uuid) {
        workTransaction {
            ids.forEach { id ->
                CommonFileUtils.getByNameWithoutExtension(
                    parent = getMusicsFolder(userId),
                    name = id.raw,
                )?.delete()
            }
            MusicTable.deleteWhere {
                (id inList ids.map { it.raw }) and (MusicTable.userId eq userId)
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
                .map { it.toMusic(buildScope = { Music.Scope.User }) }
        }

    override suspend fun getExistingIdsOfUser(
        userId: Uuid,
        ids: List<MusicId>
    ): List<MusicId> =
        workTransaction {
            MusicTable
                .select(MusicTable.id)
                .where {
                    (MusicTable.userId eq userId) and
                        (MusicTable.id inList ids.map { it.raw })
                }
                .map {
                    MusicId(raw = it[MusicTable.id].value)
                }
        }

    override suspend fun getExistingIds(ids: List<MusicId>): List<MusicId> =
        workTransaction {
            MusicTable
                .select(MusicTable.id)
                .where { MusicTable.id inList ids.map { it.raw } }
                .map {
                    MusicId(raw = it[MusicTable.id].value)
                }
        }

    override suspend fun isMusicPossessedByUser(userId: Uuid, musicId: MusicId): Boolean =
        workTransaction {
            MusicEntity
                .find { (MusicTable.id eq musicId.raw) and (MusicTable.userId eq userId) }
                .count() > 0
        }

    override suspend fun allFromAlbum(albumId: Uuid): List<Music> =
        workTransaction {
            MusicEntity
                .find { MusicTable.albumId eq albumId }
                .map { it.toMusic(buildScope = { Music.Scope.User }) }
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
                .map { it.toMusic(buildScope = { Music.Scope.User }) }
        }

    private suspend fun getMusicsFolder(userId: Uuid): File {
        val userFolder = userDataSource.getUserDirectory(userId)
        return File(userFolder, MUSIC_FOLDER).ensureExist()
    }

    override suspend fun saveFile(userId: Uuid, data: FileData): Uuid? =
        CommonFileUtils.save(
            parent = getMusicsFolder(userId),
            fileData = data,
        )

    override suspend fun getFile(name: String, userId: Uuid): File? =
        CommonFileUtils.getByNameWithoutExtension(
            name = name,
            parent = getMusicsFolder(userId),
        )

    override suspend fun deleteFile(name: String, userId: Uuid) {
        CommonFileUtils.delete(
            parent = getMusicsFolder(userId),
            name = name,
        )
    }

    override suspend fun renameFile(from: String, to: String, userId: Uuid) {
        val file = getFile(
            name = from,
            userId = userId,
        )

        val updatedFile = File(
            getMusicsFolder(userId),
            to,
        )

        file?.renameTo(updatedFile)
    }

    companion object {
        const val MUSIC_FOLDER: String = "musics"
    }
}
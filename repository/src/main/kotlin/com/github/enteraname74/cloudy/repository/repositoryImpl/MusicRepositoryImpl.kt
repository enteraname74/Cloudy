package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.MusicDataSource
import com.github.enteraname74.cloudy.repository.util.paginated
import java.time.LocalDateTime
import java.util.*

class MusicRepositoryImpl(
    private val musicDataSource: MusicDataSource,
) : MusicRepository {
    override suspend fun upsert(music: Music) {
        musicDataSource.upsert(
            music.copy(
                lastUpdateAt = LocalDateTime.now()
            )
        )
    }

    override suspend fun upsertAll(musics: List<Music>) {
        musicDataSource.upsertAll(
            musics.map {
                it.copy(
                    lastUpdateAt = LocalDateTime.now(),
                )
            }
        )
    }

    override suspend fun getFromId(musicId: UUID): Music? =
        musicDataSource.getFromId(musicId = musicId)

    override suspend fun deleteById(musicId: UUID) {
        musicDataSource.deleteById(musicId)
    }

    override suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Music> =
        musicDataSource
            .getAllOfUser(userId = userId)
            .paginated(paginatedRequest)

    override suspend fun isMusicPossessedByUser(userId: UUID, musicId: UUID): Boolean =
        musicDataSource.isMusicPossessedByUser(userId, musicId)

    override suspend fun doesMusicExists(fingerprint: String, userId: UUID): Boolean =
        musicDataSource.doesMusicExists(
            fingerprint = fingerprint,
            userId = userId,
        )

    override suspend fun allFromAlbum(albumId: UUID): List<Music> =
        musicDataSource.allFromAlbum(albumId)

    override suspend fun allFromArtist(artistId: UUID): List<Music> =
        musicDataSource.allFromArtist(artistId)
}
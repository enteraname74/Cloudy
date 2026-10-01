package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicId
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.io.File
import kotlin.uuid.Uuid

interface MusicDataSource {

    suspend fun saveFile(
        userId: Uuid,
        data: FileData,
    ): Uuid?

    suspend fun upsert(music: Music): Music
    suspend fun upsertAll(musics: List<Music>)

    suspend fun getFile(
        name: String,
        userId: Uuid,
    ): File?

    suspend fun getFromId(
        musicId: MusicId,
    ): Music?

    suspend fun getFromUser(
        musicId: MusicId,
        userId: Uuid,
    ): Music?

    suspend fun getFromCoverPath(coverPath: String): Music?
    suspend fun getAll(ids: List<MusicId>): List<Music>

    suspend fun deleteFile(
        name: String,
        userId: Uuid,
    )

    suspend fun renameFile(
        from: String,
        to: String,
        userId: Uuid,
    )

    suspend fun deleteAll(
        ids: List<MusicId>,
        userId: Uuid,
    )

    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Music>

    suspend fun getExistingIdsOfUser(
        userId: Uuid,
        ids: List<MusicId>,
    ): List<MusicId>

    suspend fun getExistingIds(
        ids: List<MusicId>,
    ): List<MusicId>

    suspend fun isMusicPossessedByUser(userId: Uuid, musicId: MusicId): Boolean
    suspend fun allFromAlbum(albumId: Uuid): List<Music>
    suspend fun allFromArtist(artistId: Uuid): List<Music>
}
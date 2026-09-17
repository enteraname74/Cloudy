package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.repository.CoverRepository
import com.github.enteraname74.cloudy.metadata.filemetadata.MusicFileMetadataManager
import com.github.enteraname74.cloudy.repository.datasource.AlbumDataSource
import com.github.enteraname74.cloudy.repository.datasource.ArtistDataSource
import com.github.enteraname74.cloudy.repository.datasource.CoverDataSource
import com.github.enteraname74.cloudy.repository.datasource.PlaylistDataSource
import java.io.File
import kotlin.uuid.Uuid

class CoverRepositoryImpl(
    private val musicFileMetadataManager: MusicFileMetadataManager,
    private val playlistDataSource: PlaylistDataSource,
    private val albumDataSource: AlbumDataSource,
    private val artistDataSource: ArtistDataSource,
    private val coverDataSource: CoverDataSource,
) : CoverRepository {
    override suspend fun getMusicFileCover(musicFile: File): ByteArray? =
        musicFileMetadataManager.getMusicFileCover(musicFile)

    override suspend fun getCover(userId: Uuid, name: String): ByteArray? =
        coverDataSource.get(
            userId = userId,
            name = name,
        )

    override suspend fun save(userId: Uuid, coverData: FileData): Uuid? =
        coverDataSource.save(
            userId = userId,
            data = coverData,
        )

    override suspend fun deletedUnusedCovers(userId: Uuid) {
        val coversNames = (
            playlistDataSource.getAllCoverNamesOfUser(userId) +
                albumDataSource.getAllCoverNamesOfUser(userId) +
                artistDataSource.getAllCoverNamesOfUser(userId)
            ).distinct()

        val savedCoverNames = coverDataSource.getAllNames(userId)

        val toDelete = savedCoverNames - coversNames.toSet()
        coverDataSource.deleteAll(
            userId = userId,
            names = toDelete,
        )
    }
}
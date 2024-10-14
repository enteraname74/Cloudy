package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.Music
import java.util.*

interface MusicDataSource {
    suspend fun upsert(music: Music)
    suspend fun upsertAll(musics: List<Music>)
    suspend fun getFromId(musicId: UUID): Music?
    suspend fun deleteById(musicId: UUID)
    suspend fun getAllOfUser(userId: UUID): List<Music>
    suspend fun isMusicPossessedByUser(userId: UUID, musicId: UUID): Boolean
    suspend fun doesMusicExists(fingerprint: String, userId: UUID): Boolean
    suspend fun allFromAlbum(albumId: UUID): List<Music>
    suspend fun allFromArtist(artistId: UUID): List<Music>
}
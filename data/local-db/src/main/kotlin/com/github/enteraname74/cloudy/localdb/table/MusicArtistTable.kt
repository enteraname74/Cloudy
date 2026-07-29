package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.util.DateUtils
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.batchUpsert

/**
 * Table for storing MusicArtists.
 */
internal object MusicArtistTable: IdTable<String>() {
    override val id = text("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE)
    val artistId = reference("artistId", ArtistTable.id, onDelete = ReferenceOption.CASCADE)
    val lastUpdateAt = long("lastUpdateAt")

    fun upsertAll(musicArtists: List<MusicArtist>) {
        batchUpsert(musicArtists) { musicArtist ->
            this[id] = musicArtist.id
            this[musicId] = musicArtist.musicId
            this[artistId] = musicArtist.artistId
            this[userId] = musicArtist.userId
            this[lastUpdateAt] = musicArtist.lastUpdateAtMillis
        }
    }
}

internal class MusicArtistEntity(id: EntityID<String>): Entity<String>(id) {
    companion object : EntityClass<String, MusicArtistEntity>(MusicArtistTable)

    var userId by MusicArtistTable.userId
    var musicId by MusicArtistTable.musicId
    var artistId by MusicArtistTable.artistId
    var lastUpdateAt by MusicArtistTable.lastUpdateAt

    fun toMusicArtist(): MusicArtist =
        MusicArtist(
            musicId = musicId.value,
            artistId = artistId.value,
            userId = userId.value,
            lastUpdateAtMillis = lastUpdateAt,
        )
}

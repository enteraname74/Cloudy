package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.util.DateUtils
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import kotlin.uuid.Uuid

internal object AlbumTable: UuidTable() {
    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val name = text("name")
    val coverPath = text("coverPath").nullable()
    val addedDate = long("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
    val artistId = reference("artistId", ArtistTable.id, ReferenceOption.CASCADE)
    val lastUpdateAt = long("lastUpdateAt").default(DateUtils.now())

    fun upsertAll(albums: List<Album>) {
        batchUpsert(albums) { album ->
            this[id] = album.id
            this[name] = album.name
            this[userId] = album.userId
            this[coverPath] = album.coverPath
            this[addedDate] = album.addedDateMillis
            this[nbPlayed] = album.nbPlayed
            this[isInQuickAccess] = album.isInQuickAccess
            this[artistId] = album.artist.id
            this[lastUpdateAt] = album.lastUpdateAtMillis
        }
    }
}

internal class AlbumEntity(id: EntityID<Uuid>): UuidEntity(id) {
    companion object : UuidEntityClass<AlbumEntity>(AlbumTable)

    var userId by AlbumTable.userId
    var name by AlbumTable.name
    var coverPath by AlbumTable.coverPath
    var addedDate by AlbumTable.addedDate
    var nbPlayed by AlbumTable.nbPlayed
    var isInQuickAccess by AlbumTable.isInQuickAccess
    val artist by ArtistEntity referencedOn AlbumTable.artistId
    var lastUpdateAt by AlbumTable.lastUpdateAt

    fun toAlbum(): Album =
        Album(
            id = id.value,
            userId = userId.value,
            name = name,
            coverPath = coverPath,
            addedDateMillis = addedDate,
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
            artist = artist.toArtist(),
            lastUpdateAtMillis = lastUpdateAt,
        )
}

package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.util.DateUtils
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import kotlin.uuid.Uuid

internal object MusicTable: IdTable<String>() {
    override val id = varchar("id", 128).entityId()
    override val primaryKey = PrimaryKey(id)

    val name = varchar("name", 128)
    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val coverPath = text("coverPath")
    val albumPosition = integer("albumPosition").nullable()
    val path = varchar("path", 255)
    val duration = long("duration")
    val addedDate = long("addedDate")
    val lastUpdateAt = long("lastUpdateAt").default(DateUtils.now())
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
    val albumId = reference("albumId", AlbumTable.id, ReferenceOption.CASCADE)

    fun upsertAll(musics: List<Music>) {
        batchUpsert(musics) { music ->
            this[id] = music.fingerprint
            this[name] = music.name
            this[userId] = music.userId
            this[coverPath] = music.coverPath
            this[albumId] = music.album.id
            this[duration] = music.duration
            this[addedDate] = music.addedDateMillis
            this[nbPlayed] = music.nbPlayed
            this[isInQuickAccess] = music.isInQuickAccess
            this[path] = music.path
            this[lastUpdateAt] = music.lastUpdateAtMillis
        }
    }
}

internal class MusicEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, MusicEntity>(MusicTable)

    var name by MusicTable.name
    var userId by MusicTable.userId
    var coverPath by MusicTable.coverPath
    var albumPosition by MusicTable.albumPosition
    var path by MusicTable.path
    var duration by MusicTable.duration
    var addedDate by MusicTable.addedDate
    var lastUpdateAt by MusicTable.lastUpdateAt
    var nbPlayed by MusicTable.nbPlayed
    var isInQuickAccess by MusicTable.isInQuickAccess

    val album by AlbumEntity referencedOn MusicTable.albumId
    var artists by ArtistEntity via MusicArtistTable

    fun toMusic(
        buildScope: (musicUserId: Uuid) -> Music.Scope
    ): Music =
        Music(
            fingerprint = id.value,
            userId = userId.value,
            name = name,
            album = album.toAlbum(),
            artists = artists.map { it.toArtist() },
            path = path,
            albumPosition = albumPosition,
            coverPath = coverPath,
            duration = duration,
            addedDateMillis = addedDate,
            lastUpdateAtMillis = lastUpdateAt,
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
            scope = buildScope(userId.value),
        )
}
package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.model.cover.Cover
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import kotlin.uuid.Uuid

internal object PlaylistTable : UuidTable() {
    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val name = text("name")
    val isFavorite = bool("isFavorite")
    val nbPlayed = integer("nbPlayed")
    val coverUrl0 = text("coverPath").nullable()
    val coverUrl1 = text("coverUrl1").nullable()
    val coverUrl2 = text("coverUrl2").nullable()
    val coverUrl3 = text("coverUrl3").nullable()
    val addedDate = long("addedDate")
    val isInQuickAccess = bool("isInQuickAccess")
    val lastUpdateAt = long("lastUpdateAt")

    fun upsertAll(playlists: List<Playlist>) {
        batchUpsert(playlists) {
            val coverPaths = it.cover?.paths().orEmpty()
            this[id] = it.id
            this[isFavorite] = it.isFavorite
            this[userId] = it.userId
            this[name] = it.name
            this[nbPlayed] = it.nbPlayed
            this[coverUrl0] = coverPaths.getOrNull(0)
            this[coverUrl1] = coverPaths.getOrNull(1)
            this[coverUrl2] = coverPaths.getOrNull(2)
            this[coverUrl3] = coverPaths.getOrNull(3)
            this[addedDate] = it.addedDateMillis
            this[isInQuickAccess] = it.isInQuickAccess
            this[lastUpdateAt] = it.lastUpdateAtMillis
        }
    }
}

internal class PlaylistEntity(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<PlaylistEntity>(PlaylistTable)

    var userId by PlaylistTable.userId
    var name by PlaylistTable.name
    var isFavorite by PlaylistTable.isFavorite
    var nbPlayed by PlaylistTable.nbPlayed
    var coverUrl0 by PlaylistTable.coverUrl0
    var coverUrl1 by PlaylistTable.coverUrl1
    var coverUrl2 by PlaylistTable.coverUrl2
    var coverUrl3 by PlaylistTable.coverUrl3
    var addedDate by PlaylistTable.addedDate
    var isInQuickAccess by PlaylistTable.isInQuickAccess
    var lastUpdateAt by PlaylistTable.lastUpdateAt

    fun toPlaylist(): Playlist =
        Playlist(
            id = id.value,
            userId = userId.value,
            name = name,
            cover = Cover.from(listOf(coverUrl0, coverUrl1, coverUrl2, coverUrl3)),
            isFavorite = isFavorite,
            addedDateMillis = addedDate,
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
            lastUpdateAtMillis = lastUpdateAt,
        )
}

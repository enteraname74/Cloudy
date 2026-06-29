package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.util.DateUtils
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import kotlin.uuid.Uuid

internal object ArtistTable: UuidTable() {
    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 128)
    val coverPath = text("coverPath").nullable()
    val addedDate = long("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
    val lastUpdateAt = long("lastUpdatedAt").default(DateUtils.now())

    fun upsertAll(artists: List<Artist>) {
        batchUpsert(artists) { artist ->
            this[id] = artist.id
            this[name] = artist.name
            this[coverPath] = artist.coverPath
            this[addedDate] = artist.addedDateMillis
            this[nbPlayed] = artist.nbPlayed
            this[isInQuickAccess] = artist.isInQuickAccess
            this[lastUpdateAt] = DateUtils.now()
        }
    }
}

internal class ArtistEntity(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<ArtistEntity>(ArtistTable)

    var userId by ArtistTable.userId
    var name by ArtistTable.name
    var coverPath by ArtistTable.coverPath
    var addedDate by ArtistTable.addedDate
    var nbPlayed by ArtistTable.nbPlayed
    var isInQuickAccess by ArtistTable.isInQuickAccess
    var lastUpdateAt by ArtistTable.lastUpdateAt

    fun toArtist(): Artist =
        Artist(
            id = id.value,
            userId = userId.value,
            name = name,
            coverPath = coverPath,
            addedDateMillis = addedDate,
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
            lastUpdateAtMillis = lastUpdateAt,
        )
}
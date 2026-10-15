package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.collection.Collection
import com.github.enteraname74.cloudy.domain.model.cover.Cover
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import kotlin.uuid.Uuid

internal object CollectionTable : UuidTable() {
    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val name = text("name")
    val coverUrl0 = text("coverPath").nullable()
    val coverUrl1 = text("coverUrl1").nullable()
    val coverUrl2 = text("coverUrl2").nullable()
    val coverUrl3 = text("coverUrl3").nullable()
    val addedDate = long("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
    val lastUpdateAt = long("lastUpdateAt")

    fun upsertAll(collections: List<Collection>) {
        batchUpsert(collections) { collection ->
            val coverPaths = collection.cover?.paths().orEmpty()
            this[id] = collection.id
            this[userId] = collection.userId
            this[name] = collection.name
            this[coverUrl0] = coverPaths.getOrNull(0)
            this[coverUrl1] = coverPaths.getOrNull(1)
            this[coverUrl2] = coverPaths.getOrNull(2)
            this[coverUrl3] = coverPaths.getOrNull(3)
            this[addedDate] = collection.addedDateMillis
            this[nbPlayed] = collection.nbPlayed
            this[isInQuickAccess] = collection.isInQuickAccess
            this[lastUpdateAt] = collection.lastUpdateAtMillis
        }
    }
}

internal class CollectionEntity(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<CollectionEntity>(CollectionTable)

    var userId by CollectionTable.userId
    var name by CollectionTable.name
    var coverUrl0 by CollectionTable.coverUrl0
    var coverUrl1 by CollectionTable.coverUrl1
    var coverUrl2 by CollectionTable.coverUrl2
    var coverUrl3 by CollectionTable.coverUrl3
    var addedDate by CollectionTable.addedDate
    var nbPlayed by CollectionTable.nbPlayed
    var isInQuickAccess by CollectionTable.isInQuickAccess
    var lastUpdateAt by CollectionTable.lastUpdateAt

    fun toCollection(): Collection = Collection(
        id = id.value,
        userId = userId.value,
        name = name,
        cover = Cover.from(listOf(coverUrl0, coverUrl1, coverUrl2, coverUrl3)),
        addedDateMillis = addedDate,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
        lastUpdateAtMillis = lastUpdateAt,
    )
}

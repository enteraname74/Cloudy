package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.collection.Collection
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
    val coverPath = text("coverPath").nullable()
    val addedDate = long("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
    val lastUpdateAt = long("lastUpdateAt")

    fun upsertAll(collections: List<Collection>) {
        batchUpsert(collections) { collection ->
            this[id] = collection.id
            this[userId] = collection.userId
            this[name] = collection.name
            this[coverPath] = collection.coverPath
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
    var coverPath by CollectionTable.coverPath
    var addedDate by CollectionTable.addedDate
    var nbPlayed by CollectionTable.nbPlayed
    var isInQuickAccess by CollectionTable.isInQuickAccess
    var lastUpdateAt by CollectionTable.lastUpdateAt

    fun toCollection(): Collection = Collection(
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

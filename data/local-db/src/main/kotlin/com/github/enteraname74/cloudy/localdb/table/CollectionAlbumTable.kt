package com.github.enteraname74.cloudy.localdb.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import kotlin.uuid.Uuid

internal object CollectionAlbumTable : IdTable<String>() {
    override val id = text("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val collectionId = reference("collectionId", CollectionTable.id, onDelete = ReferenceOption.CASCADE)
    val albumId = reference("albumId", AlbumTable.id, onDelete = ReferenceOption.CASCADE)
    val lastUpdateAt = long("lastUpdateAt")

    fun upsertAll(collectionId: Uuid, userId: Uuid, albumIds: List<Uuid>, updatedAt: Long) {
        batchUpsert(albumIds) { albumId ->
            this[id] = "$collectionId$albumId"
            this[CollectionAlbumTable.userId] = userId
            this[CollectionAlbumTable.collectionId] = collectionId
            this[CollectionAlbumTable.albumId] = albumId
            this[lastUpdateAt] = updatedAt
        }
    }
}

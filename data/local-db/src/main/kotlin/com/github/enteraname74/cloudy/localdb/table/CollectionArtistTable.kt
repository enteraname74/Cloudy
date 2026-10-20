package com.github.enteraname74.cloudy.localdb.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import kotlin.uuid.Uuid

internal object CollectionArtistTable : IdTable<String>() {
    override val id = text("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val collectionId = reference("collectionId", CollectionTable.id, onDelete = ReferenceOption.CASCADE)
    val artistId = reference("artistId", ArtistTable.id, onDelete = ReferenceOption.CASCADE)
    val lastUpdateAt = long("lastUpdateAt")

    fun upsertAll(collectionId: Uuid, userId: Uuid, artistIds: List<Uuid>, updatedAt: Long) {
        batchUpsert(artistIds) { artistId ->
            this[id] = "$collectionId$artistId"
            this[CollectionArtistTable.userId] = userId
            this[CollectionArtistTable.collectionId] = collectionId
            this[CollectionArtistTable.artistId] = artistId
            this[lastUpdateAt] = updatedAt
        }
    }
}

package com.github.enteraname74.cloudy.domain.model.collection

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CollectionWithElements(
    val collection: Collection,
    val artistIds: List<Uuid>,
    val albumIds: List<Uuid>,
)

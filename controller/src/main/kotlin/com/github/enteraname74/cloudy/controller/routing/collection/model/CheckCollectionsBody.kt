package com.github.enteraname74.cloudy.controller.routing.collection.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CheckCollectionsBody(val ids: List<Uuid>)

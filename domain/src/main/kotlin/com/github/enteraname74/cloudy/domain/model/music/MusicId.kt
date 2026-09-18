package com.github.enteraname74.cloudy.domain.model.music

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
@JvmInline
value class MusicId(
    val raw: String
) {
    constructor(
        fingerprint: String,
        userId: Uuid,
    ) : this("$fingerprint-$userId")
}

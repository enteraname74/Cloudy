package com.github.enteraname74.cloudy.domain.util

import com.github.enteraname74.cloudy.domain.serializer.UUIDSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

val CloudyJson = Json {
    serializersModule = SerializersModule {
        this.contextual(UUIDSerializer)
    }
    encodeDefaults = true
}
package com.github.enteraname74.cloudy.config.plugin

import com.github.enteraname74.cloudy.domain.serializer.UUIDSerializer
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.plus

internal fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            json = Json {
                serializersModule = SerializersModule {
                    this.contextual(UUIDSerializer)
                }
            }
        )
    }
}

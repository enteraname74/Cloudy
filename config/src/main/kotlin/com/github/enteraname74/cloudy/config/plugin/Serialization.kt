package com.github.enteraname74.cloudy.config.plugin

import com.github.enteraname74.cloudy.domain.util.CloudyJson
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

internal fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            json = CloudyJson
        )
    }
}

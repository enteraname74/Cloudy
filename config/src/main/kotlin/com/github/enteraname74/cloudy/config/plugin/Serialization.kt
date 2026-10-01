package com.github.enteraname74.cloudy.config.plugin

import com.github.enteraname74.cloudy.domain.util.CloudyJson
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

internal fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            json = CloudyJson
        )
    }
}

package com.github.enteraname74.cloudy.config.plugin

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import java.time.Duration

internal fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}
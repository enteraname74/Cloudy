package com.github.enteraname74.cloudy.config.plugin

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import kotlin.time.Duration.Companion.seconds

internal fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}
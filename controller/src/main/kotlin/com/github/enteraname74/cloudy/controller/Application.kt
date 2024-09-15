package com.github.enteraname74.cloudy.controller

import com.github.enteraname74.cloudy.config.configureApplication
import com.github.enteraname74.cloudy.controller.routing.configureRouting
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureApplication()
    configureRouting()
}
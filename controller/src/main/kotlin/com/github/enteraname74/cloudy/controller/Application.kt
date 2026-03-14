package com.github.enteraname74.cloudy.controller

import com.github.enteraname74.cloudy.config.configureApplication
import com.github.enteraname74.cloudy.controller.routing.configureRouting
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureApplication()
    configureRouting()
}
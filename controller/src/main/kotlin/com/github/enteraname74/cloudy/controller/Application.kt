package com.github.enteraname74.cloudy.controller

import com.github.enteraname74.cloudy.config.configureApplication
import com.github.enteraname74.cloudy.controller.config.configureController
import com.github.enteraname74.cloudy.controller.routing.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

suspend fun main(args: Array<String>): Unit = EngineMain.main(args)

suspend fun Application.module() {
    configureApplication()
    configureController()
    configureRouting()
}
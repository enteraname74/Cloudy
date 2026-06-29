package com.github.enteraname74.cloudy.controller.config


import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.resources.Resources

internal fun Application.configureResources() {
    install(Resources)
}
package com.github.enteraname74.cloudy.controller.config

import io.ktor.server.application.Application

fun Application.configureController() {
    configureStatusPage()
    configureResources()
    configureRequestValidation()
}
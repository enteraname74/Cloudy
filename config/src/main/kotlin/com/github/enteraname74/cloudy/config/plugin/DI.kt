package com.github.enteraname74.cloudy.config.plugin

import com.github.enteraname74.cloudy.config.di.mainModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin

internal fun Application.configureDI() {
    install(Koin) {
        modules(mainModule)
    }
}
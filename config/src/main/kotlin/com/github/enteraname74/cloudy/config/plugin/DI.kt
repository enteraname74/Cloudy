package com.github.enteraname74.cloudy.config.plugin

import com.github.enteraname74.cloudy.config.di.mainModule
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.ktor.plugin.Koin

internal fun Application.configureDI() {
    install(Koin) {
        modules(mainModule)
    }
}
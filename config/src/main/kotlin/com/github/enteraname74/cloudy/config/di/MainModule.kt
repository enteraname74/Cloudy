package com.github.enteraname74.cloudy.config.di

import org.koin.dsl.module

internal val mainModule = module {
    includes(
        otherModule,
        databaseModule,
        datasourceModule,
        repositoryModule,
        useCaseModule,
        serviceModule,
    )
}
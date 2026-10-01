package com.github.enteraname74.cloudy.config.di

import com.github.enteraname74.cloudy.domain.migration.DatabaseManager
import com.github.enteraname74.cloudy.localdb.DatabaseManagerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val databaseModule: Module = module {
    singleOf(::DatabaseManagerImpl) bind DatabaseManager::class
}
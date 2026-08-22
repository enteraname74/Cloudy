package com.github.enteraname74.cloudy.domain.migration

import com.github.enteraname74.cloudy.domain.util.CloudyResult

interface BackendMigration {
    val forVersion: Int

    suspend fun execute(): CloudyResult<Unit>
}
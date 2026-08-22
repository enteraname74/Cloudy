package com.github.enteraname74.cloudy.localdb

import com.github.enteraname74.cloudy.domain.util.CloudyResult

interface DatabaseMigration {
    val forVersion: Int

    suspend fun execute(): CloudyResult<Unit>
}
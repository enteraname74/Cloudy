package com.github.enteraname74.cloudy.localdb.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

internal suspend fun <T> workTransaction(block: suspend () -> T): T =
    withContext(Dispatchers.IO) {
        suspendTransaction { block() }
    }

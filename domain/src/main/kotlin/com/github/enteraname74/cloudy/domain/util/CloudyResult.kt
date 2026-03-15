package com.github.enteraname74.cloudy.domain.util

import jdk.internal.joptsimple.internal.Messages.message

sealed interface CloudyResult<T> {
    data class Success<T>(val data: T) : CloudyResult<T>
    data class Error<T>(val message: String? = null) : CloudyResult<T>

    fun toSimple(): CloudyResult<Unit> =
        when (this) {
            is Error<*> -> Error(message = message)
            is Success<*> -> Success(Unit)
        }
}
package com.github.enteraname74.cloudy.domain.util

sealed interface CloudyResult<T> {
    data class Success<T>(val data: T) : CloudyResult<T>
    data class Error<T>(val message: String? = null) : CloudyResult<T>
}
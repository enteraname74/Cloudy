package com.github.enteraname74.cloudy.domain.util

sealed interface ServiceResult {
    data class Ok(val data: Any? = null) : ServiceResult
    data class Error(val message: String? = null) : ServiceResult
}
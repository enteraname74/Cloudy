package com.github.enteraname74.cloudy.controller.routing.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class UserAuth(
    val username: String,
    val password: String,
) {
    fun isValid() = username.isNotBlank() && password.isNotBlank()
}

package com.github.enteraname74.cloudy.controller.routing.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSignIn(
    val username: String,
    val password: String,
    val inscriptionCode: String,
) {
    fun isValid() = username.isNotBlank()
            && password.isNotBlank()
            && inscriptionCode.isNotBlank()
}

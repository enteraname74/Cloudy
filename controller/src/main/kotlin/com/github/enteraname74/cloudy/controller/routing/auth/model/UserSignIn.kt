package com.github.enteraname74.cloudy.controller.routing.auth.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UserSignIn(
    val username: String,
    val password: String,
    val inscriptionCode: Uuid,
) {
    fun isValid() = username.isNotBlank()
            && password.isNotBlank()
}

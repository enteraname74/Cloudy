package com.github.enteraname74.cloudy.controller.routing.auth.model

import com.github.enteraname74.cloudy.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class ConnectedUser(
    val username: String,
    val isAdmin: Boolean,
)

fun User.toConnectedUser(): ConnectedUser =
    ConnectedUser(
        username = username,
        isAdmin = isAdmin,
    )

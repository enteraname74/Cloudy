package com.github.enteraname74.cloudy.controller.routing.auth.model

import com.github.enteraname74.cloudy.domain.model.user.User
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class ConnectedUser(
    val id: Uuid,
    val username: String,
    val isAdmin: Boolean,
)

fun User.toConnectedUser(): ConnectedUser =
    ConnectedUser(
        id = id,
        username = username,
        isAdmin = isAdmin,
    )

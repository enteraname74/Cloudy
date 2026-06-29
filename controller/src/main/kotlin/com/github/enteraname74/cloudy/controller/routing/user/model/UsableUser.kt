package com.github.enteraname74.cloudy.controller.routing.user.model

import com.github.enteraname74.cloudy.domain.model.User
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UsableUser(
    val id: Uuid,
    val username: String,
    val isAdmin: Boolean,
)

fun User.toUsableUser(): UsableUser =
    UsableUser(
        id = id,
        username = username,
        isAdmin = isAdmin,
    )

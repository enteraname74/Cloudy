package com.github.enteraname74.cloudy.controller.routing.user.model

import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.model.user.UserType
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class SimpleUser(
    val id: Uuid,
    val username: String,
    val type: UserType,
)

fun User.toSimpleUser(): SimpleUser =
    SimpleUser(
        id = id,
        username = username,
        type = type,
    )

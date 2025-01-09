package com.github.enteraname74.cloudy.controller.routing.user.model

import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UsableUser(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val username: String,
    val isAdmin: Boolean,
)

fun User.toUsableUser(): UsableUser =
    UsableUser(
        id = id,
        username = username,
        isAdmin = isAdmin,
    )

package com.github.enteraname74.cloudy.domain.model.user

import com.github.enteraname74.cloudy.domain.auth.HashedPassword
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class User(
    val id: Uuid,
    val username: String,
    val hashedPassword: HashedPassword,
    val isAdmin: Boolean = false,
)

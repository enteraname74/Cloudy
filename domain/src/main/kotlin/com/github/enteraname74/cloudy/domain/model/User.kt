package com.github.enteraname74.cloudy.domain.model

import com.github.enteraname74.cloudy.domain.auth.HashedPassword
import com.github.enteraname74.cloudy.domain.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val hashedPassword: HashedPassword,
    val isAdmin: Boolean = false,
)

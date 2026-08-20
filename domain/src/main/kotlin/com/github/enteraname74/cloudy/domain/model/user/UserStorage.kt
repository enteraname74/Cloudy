package com.github.enteraname74.cloudy.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserStorage(
    val max: Int,
    val current: Int,
)

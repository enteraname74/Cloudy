package com.github.enteraname74.cloudy.controller.routing.auth.model

import com.github.enteraname74.cloudy.controller.routing.user.model.SimpleUser
import kotlinx.serialization.Serializable

@Serializable
data class UserAuth(
    val user: SimpleUser,
    val tokens: UserTokens,
)

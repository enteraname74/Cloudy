package com.github.enteraname74.cloudy.controller.routing.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class UserAuth(
    val user: ConnectedUser,
    val tokens: UserTokens,
)

package com.github.enteraname74.cloudy.controller.routing.auth.model

import com.github.enteraname74.cloudy.config.ApplicationContext
import com.github.enteraname74.cloudy.config.auth.TokenType
import com.github.enteraname74.cloudy.config.auth.generateToken
import com.github.enteraname74.cloudy.domain.model.user.User
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Serializable
data class UserTokens(
    val accessToken: String,
    val refreshToken: String,
)

fun ApplicationContext.buildUserTokens(
    user: User
): UserTokens =
    UserTokens(
        accessToken = generateToken(
            user = user,
            expireDate = Date.from(Instant.now().plus(15, ChronoUnit.DAYS)),
            type = TokenType.Access,
        ),
        refreshToken = generateToken(
            user = user,
            expireDate = Date.from(Instant.now().plus(90, ChronoUnit.DAYS)),
            type = TokenType.Refresh,
        )
    )
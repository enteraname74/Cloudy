package com.github.enteraname74.cloudy.controller.routing.player.model

import com.github.enteraname74.cloudy.config.ApplicationContext
import com.github.enteraname74.cloudy.config.auth.TokenType
import com.github.enteraname74.cloudy.config.auth.generateToken
import com.github.enteraname74.cloudy.domain.model.user.User
import kotlinx.serialization.Serializable
import java.util.Date
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

@Serializable
data class PlayerToken(
    val token: String,
    val expireAt: Long,
)

fun ApplicationContext.buildPlayerToken(
    user: User,
): PlayerToken {
    val expireDate = Clock.System.now().plus(30.minutes).toEpochMilliseconds()
    return PlayerToken(
        token = generateToken(
            user = user,
            expireDate = Date(expireDate),
            type = TokenType.Player,
        ),
        expireAt = expireDate,
    )
}
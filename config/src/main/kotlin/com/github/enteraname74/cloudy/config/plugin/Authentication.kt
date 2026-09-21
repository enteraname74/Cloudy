package com.github.enteraname74.cloudy.config.plugin

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.enteraname74.cloudy.config.auth.TOKEN_ROLE_CLAIM_KEY
import com.github.enteraname74.cloudy.config.auth.TOKEN_TYPE_CLAIM_KEY
import com.github.enteraname74.cloudy.config.auth.TOKEN_USER_ID_CLAIM_KEY
import com.github.enteraname74.cloudy.config.auth.TokenType
import com.github.enteraname74.cloudy.config.util.Messages
import com.github.enteraname74.cloudy.domain.ext.toUuid
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

internal fun Application.configureAuthentication() {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val myRealm = environment.config.property("jwt.realm").getString()
    val verifier = JWT
        .require(Algorithm.HMAC256(secret))
        .withIssuer(issuer)
        .build()

    install(Authentication) {
        jwt(ACCESS_AUTH_NAME) {
            realm = myRealm
            verifier(verifier)
            validate { credential ->
                if (credential.isValidFor(TokenType.Access)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = Messages.Auth.TOKEN_ERROR,
                )
            }
        }

        jwt(REFRESH_AUTH_NAME) {
            realm = myRealm
            verifier(verifier)
            validate { credential ->
                if (credential.isValidFor(TokenType.Refresh)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = Messages.Auth.TOKEN_ERROR,
                )
            }
        }
    }
}

fun Route.authenticatedRoutes(
    block: Route.() -> Unit
) {
    authenticate(ACCESS_AUTH_NAME) {
        block()
    }
}

fun Route.refreshAuthenticatedRoutes(
    block: Route.() -> Unit
) {
    authenticate(REFRESH_AUTH_NAME) {
        block()
    }
}

private fun JWTCredential.isValidFor(expectedType: TokenType): Boolean {
    val userId = payload.getClaim(TOKEN_USER_ID_CLAIM_KEY).asString()?.toUuid()
    val role = payload.getClaim(TOKEN_ROLE_CLAIM_KEY).asString()
    val tokenType = payload
        .getClaim(TOKEN_TYPE_CLAIM_KEY)
        .asString()
        ?.let(TokenType::fromString)

    return userId != null && !role.isNullOrBlank() && tokenType == expectedType
}

private const val ACCESS_AUTH_NAME = "access-jwt"
private const val REFRESH_AUTH_NAME = "refresh-jwt"

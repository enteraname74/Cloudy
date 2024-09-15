package com.github.enteraname74.cloudy.config.plugin

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.enteraname74.cloudy.config.auth.TOKEN_ROLE_CLAIM_KEY
import com.github.enteraname74.cloudy.config.auth.TOKEN_USERNAME_CLAIM_KEY
import com.github.enteraname74.cloudy.domain.model.UserType
import io.ktor.client.engine.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

internal fun Application.configureAuthentication() {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val myRealm = environment.config.property("jwt.realm").getString()
    install(Authentication) {
        jwt(AUTH_NAME) {
            realm = myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim(TOKEN_USERNAME_CLAIM_KEY).asString() != ""
                    && credential.payload.getClaim(TOKEN_ROLE_CLAIM_KEY).asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = "Token is missing, not valid or has expired",
                )
            }
        }
    }
}

fun Routing.authenticatedRoutes(
    block: Route.() -> Unit
) {
    authenticate(AUTH_NAME) {
        block()
    }
}

fun PipelineContext<Unit, ApplicationCall>.isAdmin(): Boolean {
    val principal = call.principal<JWTPrincipal>()
    val role: String = principal?.payload?.getClaim(TOKEN_ROLE_CLAIM_KEY)?.asString() ?: return false

    return UserType.fromString(role) == UserType.Admin
}

private const val AUTH_NAME = "auth-jwt"
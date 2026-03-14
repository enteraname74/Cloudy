package com.github.enteraname74.cloudy.controller.config

import com.github.enteraname74.cloudy.controller.routing.auth.model.UserLogin
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserSignIn
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<UserLogin> { userLogin ->
            if (userLogin.isValid()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(InvalidRequestType.UserInformation.name)
            }
        }
        validate<UserSignIn> { userSignIn ->
            if (userSignIn.isValid()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(InvalidRequestType.UserInformation.name)
            }
        }
    }
}

enum class InvalidRequestType {
    UserInformation,
    Unknown;

    companion object {
        fun fromRequest(cause: RequestValidationException): InvalidRequestType =
            entries.find { it.name == cause.reasons.firstOrNull().orEmpty() } ?: Unknown
    }
}
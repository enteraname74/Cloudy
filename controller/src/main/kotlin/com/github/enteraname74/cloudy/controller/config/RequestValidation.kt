package com.github.enteraname74.cloudy.controller.config

import com.github.enteraname74.cloudy.controller.routing.auth.model.UserLogin
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserSignIn
import com.github.enteraname74.cloudy.controller.routing.music.model.CheckMusicsBody
import com.github.enteraname74.cloudy.controller.routing.music.model.UpdateMusicsBody
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
        validate<UpdateMusicsBody> { musicUpdates ->
            if (musicUpdates.musics.all { it.isValid() }) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(InvalidRequestType.MusicUpdate.name)
            }
        }
        validate<CheckMusicsBody> { check ->
            println("THERE WITH: $check")
            if (check.ids.all { it.isNotBlank() }) {

                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(InvalidRequestType.MusicCheck.name)
            }
        }
    }
}

enum class InvalidRequestType {
    UserInformation,
    MusicUpdate,
    MusicCheck,
    Unknown;

    companion object {
        fun fromRequest(cause: RequestValidationException): InvalidRequestType =
            entries.find { it.name == cause.reasons.firstOrNull().orEmpty() } ?: Unknown
    }
}
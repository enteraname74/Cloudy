package com.github.enteraname74.cloudy.controller.config

import com.github.enteraname74.cloudy.controller.routing.auth.model.UserLogin
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserSignIn
import com.github.enteraname74.cloudy.controller.routing.music.model.CheckMusicsBody
import com.github.enteraname74.cloudy.controller.routing.music.model.UpdateMusicsBody
import com.github.enteraname74.cloudy.controller.routing.playlist.model.UploadPlaylistBody
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.requestvalidation.ValidationResult

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
                ValidationResult.Invalid(InvalidRequestType.InvalidData.name)
            }
        }
        validate<CheckMusicsBody> { check ->
            if (check.ids.all { it.isNotBlank() }) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(InvalidRequestType.InvalidData.name)
            }
        }
        validate<UploadPlaylistBody> { body ->
            if (body.playlists.all { it.isValid() }) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(InvalidRequestType.InvalidData.name)
            }
        }
    }
}

enum class InvalidRequestType {
    UserInformation,
    InvalidData,
    Unknown;

    companion object {
        fun fromRequest(cause: RequestValidationException): InvalidRequestType =
            entries.find { it.name == cause.reasons.firstOrNull().orEmpty() } ?: Unknown
    }
}
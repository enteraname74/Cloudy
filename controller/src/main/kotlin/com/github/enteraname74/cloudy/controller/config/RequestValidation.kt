package com.github.enteraname74.cloudy.controller.config

import com.github.enteraname74.cloudy.controller.routing.auth.model.UserLogin
import com.github.enteraname74.cloudy.controller.routing.auth.model.UserSignIn
import com.github.enteraname74.cloudy.controller.routing.music.model.CheckMusicsBody
import com.github.enteraname74.cloudy.controller.routing.music.model.FetchFromUrlBody
import com.github.enteraname74.cloudy.controller.routing.player.model.CheckPlayerMusicIdsBody
import com.github.enteraname74.cloudy.controller.routing.player.model.MusicsOperationOnPlayedListBody
import com.github.enteraname74.cloudy.controller.routing.player.model.JoinPlayedListBody
import com.github.enteraname74.cloudy.controller.routing.player.model.NewPlayedListBody
import com.github.enteraname74.cloudy.controller.routing.player.model.RemoveUserFromPlayedListBody
import com.github.enteraname74.cloudy.controller.routing.player.model.UpdateCurrentMusicBody
import com.github.enteraname74.cloudy.controller.routing.player.model.UpdatePlayedListBody
import com.github.enteraname74.cloudy.controller.routing.playlist.model.UploadPlaylistBody
import com.github.enteraname74.cloudy.domain.model.music.MusicUpdate
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
        validate<MusicUpdate> { musicUpdate ->
            if (musicUpdate.isValid()) {
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
        validate<FetchFromUrlBody> { fetch ->
            if (fetch.url.isNotBlank()) {
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
        validate<NewPlayedListBody> { body ->
            if (body.isValid()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(InvalidRequestType.InvalidData.name)
            }
        }
        validate<JoinPlayedListBody> { body ->
            if (body.isValid()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(InvalidRequestType.InvalidData.name)
            }
        }
        validate<RemoveUserFromPlayedListBody> { body ->
            if (body.isValid()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(InvalidRequestType.InvalidData.name)
            }
        }
        validate<UpdatePlayedListBody> { body ->
            if (body.isValid()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(InvalidRequestType.InvalidData.name)
            }
        }
        validate<MusicsOperationOnPlayedListBody> { body ->
            if (body.isValid()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(InvalidRequestType.InvalidData.name)
            }
        }
        validate<CheckPlayerMusicIdsBody> { body ->
            if (body.isValid()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(InvalidRequestType.InvalidData.name)
            }
        }
        validate<UpdateCurrentMusicBody> { body ->
            if (body.isValid()) {
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
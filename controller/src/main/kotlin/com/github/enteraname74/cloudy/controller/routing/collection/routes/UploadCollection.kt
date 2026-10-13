package com.github.enteraname74.cloudy.controller.routing.collection.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.cannotFindUser
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.respond
import com.github.enteraname74.cloudy.controller.routing.collection.resource.CollectionResource
import com.github.enteraname74.cloudy.controller.util.MultiPartDataUtils
import com.github.enteraname74.cloudy.domain.model.collection.CollectionUpload
import com.github.enteraname74.cloudy.domain.service.CollectionService
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.logging.cloudyLogger
import io.ktor.http.content.MultiPartData
import io.ktor.server.request.receiveMultipart
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.uploadCollection() {
    val collectionService by inject<CollectionService>()
    val userService by inject<UserService>()

    post<CollectionResource> {
        val userId: Uuid = getUserIdFromToken() ?: return@post missingTokenInformation()
        userService.getUserFromId(userId) ?: return@post cannotFindUser()

        val multipartData: MultiPartData = call.receiveMultipart()
        when (val uploadData = MultiPartDataUtils.processUpdateRequest<CollectionUpload>(multipartData)) {
            is CloudyResult.Error -> {
                cloudyLogger.error("Error while uploading collection: $uploadData")
                respond(uploadData)
            }
            is CloudyResult.Success -> respond(
                collectionService.upload(
                    collectionUpload = uploadData.data.second,
                    coverData = uploadData.data.first,
                    userId = userId,
                )
            )
        }
    }
}

package com.github.enteraname74.cloudy.controller.routing.collection.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.collection.resource.CollectionResource
import com.github.enteraname74.cloudy.domain.model.collection.Collection
import com.github.enteraname74.cloudy.domain.service.CollectionService
import com.github.enteraname74.cloudy.domain.service.CoverService
import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.getCollectionCover() {
    val coverService by inject<CoverService>()
    val collectionService by inject<CollectionService>()

    get<CollectionResource.Cover> { resource ->
        val messages = getRoutingMessages()
        val userId = getUserIdFromToken() ?: return@get missingTokenInformation()
        val collection = collectionService.getFromCoverPath(
            "${Collection.COVER_PATH}${resource.coverId}"
        ) ?: return@get response(HttpStatusCode.NotFound, messages.WRONG_ID)

        if (!collectionService.isCollectionPossessedByUser(userId, collection.id)) {
            return@get forbidden(messages.COLLECTION_NOT_POSSESSED_BY_USER)
        }
        val cover = coverService.getByName(resource.coverId, userId)
            ?: return@get badRequest(messages.IMAGE_NOT_FOUND)
        call.respond(cover)
    }
}

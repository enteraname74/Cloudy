package com.github.enteraname74.cloudy.controller.routing.collection.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.collection.resource.CollectionResource
import com.github.enteraname74.cloudy.domain.service.CollectionService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.deleteCollections() {
    val collectionService by inject<CollectionService>()

    delete<CollectionResource> {
        val collectionIds = call.receive<List<String>>().mapNotNull(Uuid::parseOrNull)
        val userId = getUserIdFromToken() ?: return@delete missingTokenInformation()
        val messages = getRoutingMessages()

        collectionIds.forEach { collectionId ->
            if (!collectionService.isCollectionPossessedByUser(userId, collectionId)) {
                return@delete forbidden(messages.collectionNotPossessedByUser(collectionId))
            }
        }
        collectionService.deleteAll(collectionIds, userId)
        response(HttpStatusCode.OK, messages.COLLECTIONS_DELETED)
    }
}

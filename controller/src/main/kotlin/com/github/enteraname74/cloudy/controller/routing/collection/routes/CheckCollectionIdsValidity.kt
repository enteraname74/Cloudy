package com.github.enteraname74.cloudy.controller.routing.collection.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.collection.model.CheckCollectionsBody
import com.github.enteraname74.cloudy.controller.routing.collection.resource.CollectionResource
import com.github.enteraname74.cloudy.domain.service.CollectionService
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.checkCollectionIdsValidity() {
    val collectionService by inject<CollectionService>()

    post<CollectionResource.Check> {
        val userId = getUserIdFromToken() ?: return@post missingTokenInformation()
        val body: CheckCollectionsBody = call.receive()
        call.respond(collectionService.getDeletedCollectionIds(body.ids, userId))
    }
}

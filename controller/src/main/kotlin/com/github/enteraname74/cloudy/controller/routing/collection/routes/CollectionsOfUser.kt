package com.github.enteraname74.cloudy.controller.routing.collection.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.collection.resource.CollectionResource
import com.github.enteraname74.cloudy.domain.service.CollectionService
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.collectionsOfUser() {
    val collectionService by inject<CollectionService>()

    get<CollectionResource.OfUser> { resource ->
        val userId = getUserIdFromToken() ?: return@get missingTokenInformation()
        call.respond(
            collectionService.getAllOfUser(
                userId = userId,
                paginatedRequest = resource.toPaginatedRequest(),
            )
        )
    }
}

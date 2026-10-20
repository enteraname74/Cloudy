package com.github.enteraname74.cloudy.controller.routing.collection

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.collection.routes.checkCollectionIdsValidity
import com.github.enteraname74.cloudy.controller.routing.collection.routes.collectionsOfUser
import com.github.enteraname74.cloudy.controller.routing.collection.routes.deleteCollections
import com.github.enteraname74.cloudy.controller.routing.collection.routes.getCollectionCover
import com.github.enteraname74.cloudy.controller.routing.collection.routes.uploadCollection
import io.ktor.server.routing.Routing

fun Routing.collectionRouting() {
    authenticatedRoutes {
        deleteCollections()
        collectionsOfUser()
        uploadCollection()
        getCollectionCover()
        checkCollectionIdsValidity()
    }
}

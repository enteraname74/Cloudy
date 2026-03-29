package com.github.enteraname74.cloudy.controller.routing.player

import com.github.enteraname74.cloudy.config.plugin.authenticatedRoutes
import com.github.enteraname74.cloudy.controller.routing.player.routes.addMusicsToPlayedList
import com.github.enteraname74.cloudy.controller.routing.player.routes.createPlayedList
import com.github.enteraname74.cloudy.controller.routing.player.routes.deletePlayedList
import com.github.enteraname74.cloudy.controller.routing.player.routes.getPlayedList
import com.github.enteraname74.cloudy.controller.routing.player.routes.getPlayedListMusics
import com.github.enteraname74.cloudy.controller.routing.player.routes.joinPlayedList
import com.github.enteraname74.cloudy.controller.routing.player.routes.removeUserFromPlayedList
import com.github.enteraname74.cloudy.controller.routing.player.routes.updatePlayedList
import io.ktor.server.routing.Routing

fun Routing.playerRouting() {
    authenticatedRoutes {
        createPlayedList()
        joinPlayedList()
        getPlayedList()
        getPlayedListMusics()
        deletePlayedList()
        removeUserFromPlayedList()
        updatePlayedList()
        addMusicsToPlayedList()
    }
}
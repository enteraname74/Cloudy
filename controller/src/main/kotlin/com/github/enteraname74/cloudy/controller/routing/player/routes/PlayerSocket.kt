package com.github.enteraname74.cloudy.controller.routing.player.routes

import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.domain.model.player.PlayedList
import com.github.enteraname74.cloudy.domain.model.player.PlayerSocketUser
import com.github.enteraname74.cloudy.domain.service.PlayerService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.websocket.PlayerUserCommunication
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.playerSocket() {
    val playerService: PlayerService by inject()
    val playerUserCommunication: PlayerUserCommunication by inject()

    webSocket(path = "/player/{listId}") {
        val routingMessages = call.getRoutingMessages()

        val listId = call.parameters["listId"]?.let(Uuid::parse) ?: run {
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, routingMessages.PLAYED_LIST_NOT_FOUND))
            return@webSocket
        }

        val userId = call.request.queryParameters["userId"]?.let(Uuid::parse) ?: run {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, routingMessages.CANNOT_FIND_USER))
            return@webSocket
        }

        val deviceId = call.request.queryParameters["deviceId"] ?: run {
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, routingMessages.MISING_DEVICE_ID))
            return@webSocket
        }

        val playedListResult: CloudyResult<PlayedList> = playerService.getPlayedList(
            userId = userId,
            listId = listId,
            deviceId = deviceId,
            routingMessages = routingMessages,
        )

        (playedListResult as? CloudyResult.Error<PlayedList>)?.let {
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, playedListResult.message.orEmpty()))
            return@webSocket
        }

        playerUserCommunication.add(
            connection = PlayerSocketUser(
                listId = listId,
                userId = userId,
                deviceId = deviceId,
                session = this,
            )
        )

        try {
            // Suspend until the socket is closed
            closeReason.await()
        } finally {
            playerUserCommunication.remove(
                listId = listId,
                userId = userId,
                deviceId = deviceId,
            )
            /*
            We will always try to disconnect the user,
            for case where he was improperly disconnected (missing internet, quiting the app before quitting the list)
             */
            playerService.removeOrDisconnect(
                userId = userId,
                listId = listId,
                deviceId = deviceId,
                deviceIdToRemove = deviceId,
                userIdToRemove = userId,
                routingMessages = routingMessages,
            )
        }
    }
}
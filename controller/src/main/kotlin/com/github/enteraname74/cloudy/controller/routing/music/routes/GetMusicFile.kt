package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.music.resource.MusicResource
import com.github.enteraname74.cloudy.domain.service.MusicService
import com.github.enteraname74.cloudy.logging.cloudyLogger
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFile
import io.ktor.server.plugins.partialcontent.PartialContent
import io.ktor.server.resources.get
import io.ktor.server.response.header
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import io.ktor.server.routing.contentType
import org.koin.ktor.ext.inject
import java.io.File

fun Route.getMusicFile() {
    val musicService by inject<MusicService>()

    install(PartialContent)

    get<MusicResource.File> { musicResource ->
        cloudyLogger.debug("Get file requested")
        call.request.headers.forEach { key, values ->
            cloudyLogger.debug("request header: $key -- $values")
        }

        val range = call.request.headers[HttpHeaders.Range]
        cloudyLogger.debug("range header? $range")

        val username: String = getUsernameFromToken() ?: return@get missingTokenInformation()

        val musicFile: File = musicService.getMusicFile(
            musicId = musicResource.id,
            username = username,
        ) ?: return@get response(
            status = HttpStatusCode.NotFound,
            message = getRoutingMessages().FILE_NOT_FOUND,
        )
        val contentType: ContentType = ContentType.defaultForFile(musicFile)

        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Inline.withParameter(
                ContentDisposition.Parameters.FileName, musicFile.name
            ).toString()
        )
        call.response.header(
            HttpHeaders.ContentType,
            contentType.toString(),
        )
        call.respondFile(musicFile)
    }
}
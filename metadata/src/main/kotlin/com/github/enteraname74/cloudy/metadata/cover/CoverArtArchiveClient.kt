package com.github.enteraname74.cloudy.metadata.cover

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.logging.CloudyLogger
import com.github.enteraname74.cloudy.metadata.htppclient.defaultHttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException

/**
 * Retrieves covers from the Cover Art Archive, using MusicBrainz ids.
 */
internal class CoverArtArchiveClient {
    private val logger = CloudyLogger(this::class)

    /**
     * Retrieve the front cover of a release group.
     *
     * @param releaseGroupId the MusicBrainz id of the release group.
     * @return the cover, or nothing if the release group has no cover or if the request failed.
     */
    suspend fun getReleaseGroupCover(releaseGroupId: String): FileData? {
        if (!releaseGroupId.matches(mbidRegex)) {
            logger.warn("Invalid release group id: $releaseGroupId")
            return null
        }

        return try {
            // The Cover Art Archive redirects to the image hosted on archive.org.
            val response: HttpResponse = defaultHttpClient.get("$BASE_URL/release-group/$releaseGroupId/front-$COVER_SIZE") {
                header(HttpHeaders.UserAgent, USER_AGENT)
            }

            when {
                response.status == HttpStatusCode.NotFound -> {
                    logger.info("No cover found for release group $releaseGroupId")
                    null
                }
                !response.status.isSuccess() -> {
                    logger.error("Failed to retrieve cover of release group $releaseGroupId with status ${response.status}")
                    null
                }
                else -> FileData(
                    extension = response.imageExtension(),
                    data = response.readRawBytes(),
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.error("Failed to retrieve cover of release group $releaseGroupId with error: ${e.message}")
            null
        }
    }

    private fun HttpResponse.imageExtension(): String =
        when (contentType()?.withoutParameters()) {
            ContentType.Image.PNG -> "png"
            ContentType.Image.GIF -> "gif"
            else -> "jpg"
        }

    private companion object {
        const val BASE_URL = "https://coverartarchive.org"
        const val COVER_SIZE = 1200

        // MusicBrainz asks clients to identify themselves.
        const val USER_AGENT = "Cloudy ( https://github.com/enteraname74/Cloudy )"

        val mbidRegex = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    }
}

package com.github.enteraname74.cloudy.metadata.cover

import com.github.enteraname74.cloudy.logging.CloudyLogger
import com.github.enteraname74.cloudy.metadata.htppclient.defaultHttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class RemoteMusicCoverRetriever {
    private val logger = CloudyLogger(this::class)

    /**
     * Tries to retrieve a URL of the cover corresponding of a given music name and artist.
     */
    suspend fun getCoverURL(
        musicName: String,
        musicArtist: String,
    ): String? = try {
        // TODO: Find better source for cover
        val path = "https://lyrist.vercel.app/api/$musicName/$musicArtist".replace(" ", "%20")
        val remoteCover: RemoteCover = defaultHttpClient.get(path).body()
        remoteCover.image
    } catch (e: Exception) {
        logger.error("Error while fetching cover url for music $musicName of artist $musicArtist. Error: ${e.message}")
        null
    }
}
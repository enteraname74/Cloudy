package com.github.enteraname74.cloudy.metadata.cover

import com.github.enteraname74.cloudy.metadata.htppclient.defaultHttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*

internal class RemoteMusicCoverRetriever {

    /**
     * Tries to retrieve a URL of the cover corresponding of a given music name and artist.
     */
    suspend fun getCoverURL(
        musicName: String,
        musicArtist: String,
    ): String? = try {
        val path = "https://lyrist.vercel.app/api/$musicName/$musicArtist".replace(" ", "%20")
        val remoteCover: RemoteCover = defaultHttpClient.get(path).body()
        remoteCover.image
    } catch (_: Exception) {
        null
    }
}
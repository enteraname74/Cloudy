package com.github.enteraname74.cloudy.metadata.acoustid

import com.github.enteraname74.cloudy.domain.model.FingerprintData
import com.github.enteraname74.cloudy.logging.CloudyLogger
import com.github.enteraname74.cloudy.metadata.acoustid.model.AcoustidLookupRequestResult
import com.github.enteraname74.cloudy.metadata.acoustid.model.AcoustidResultAnalyzer
import com.github.enteraname74.cloudy.metadata.htppclient.defaultHttpClient
import com.github.enteraname74.cloudy.metadata.model.MusicMetadata
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class AcoustidApiClient {
    private val logger = CloudyLogger(this::class)
    private val apiKey: String? = System.getenv("ACOUSTID_API_KEY")?.takeIf { it.isNotBlank() }

    /**
     * Retrieve music metadata from the Acoustid API.
     *
     * @param fingerprintData the fingerprint data used for the Acoustid API.
     * @param fileMetadata the metadata of the music file.
     *
     * @return the found metadata from Acoustid or the default file metadata if nothing was found.
     */
    suspend fun getMusicMetadata(
        fingerprintData: FingerprintData,
        fileMetadata: MusicMetadata,
    ): MusicMetadata {
        if (apiKey == null) {
            logger.warn("ACOUSTID_API_KEY is not set, cannot retrieve music metadata from Acoustid")
            return fileMetadata
        }

        return try {
            val response: HttpResponse = throttled {
                defaultHttpClient.get(LOOKUP_URL) {
                    parameter("client", apiKey)
                    parameter("meta", "recordings releasegroups sources compress")
                    parameter("duration", fingerprintData.duration)
                    parameter("fingerprint", fingerprintData.fingerprint)
                }
            }

            logger.debug("Got response: ${response.bodyAsText()}")

            val requestResult: AcoustidLookupRequestResult = response.body()
            if (!requestResult.isSuccess) {
                logger.error("Acoustid lookup failed with error: ${requestResult.error?.message}")
                return fileMetadata
            }

            AcoustidResultAnalyzer(
                requestResult = requestResult,
                initialMetadata = fileMetadata,
            ).getMusicMetadataFromRequest()
        } catch (e: Exception) {
            logger.error("Failed to retrieve music metadata from Acoustid with error: ${e.message}")
            fileMetadata
        }
    }

    /**
     * Acoustid allows at most 3 requests per second, shared by all clients of the server.
     * Only the request slot is reserved under the lock, so a slow response doesn't block the next requests.
     */
    private suspend fun <T> throttled(request: suspend () -> T): T {
        requestMutex.withLock {
            val elapsedTime = System.currentTimeMillis() - lastRequestTime
            if (elapsedTime < MIN_DELAY_BETWEEN_REQUESTS_MS) {
                delay(MIN_DELAY_BETWEEN_REQUESTS_MS - elapsedTime)
            }
            lastRequestTime = System.currentTimeMillis()
        }
        return request()
    }

    private companion object {
        const val LOOKUP_URL = "https://api.acoustid.org/v2/lookup"
        const val MIN_DELAY_BETWEEN_REQUESTS_MS = 334L

        val requestMutex = Mutex()
        var lastRequestTime = 0L
    }
}

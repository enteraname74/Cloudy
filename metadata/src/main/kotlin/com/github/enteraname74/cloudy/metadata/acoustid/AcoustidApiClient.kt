package com.github.enteraname74.cloudy.metadata.acoustid

import com.github.enteraname74.cloudy.metadata.acoustid.model.AcoustidLookupRequestResult
import com.github.enteraname74.cloudy.metadata.acoustid.model.AcoustidResultAnalyzer
import com.github.enteraname74.cloudy.metadata.fingerprint.FingerprintData
import com.github.enteraname74.cloudy.metadata.htppclient.defaultHttpClient
import com.github.enteraname74.cloudy.metadata.model.MusicMetadata
import io.ktor.client.call.*
import io.ktor.client.request.*

internal class AcoustidApiClient {

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
    ): MusicMetadata =
        try {
            val apiKey = System.getenv("ACOUSTID_API_KEY")
            val uri = "https://api.acoustid.org/v2/lookup?client=$apiKey" +
                    "&meta=recordings+releasegroups+compress&duration=" +
                    fingerprintData.duration +
                    "&fingerprint=${fingerprintData.fingerprint}"

            val requestResult: AcoustidLookupRequestResult = defaultHttpClient.get(
                apiKey
            ).body()

            val resultAnalyzer = AcoustidResultAnalyzer(
                requestResult = requestResult,
                initialMetadata = fileMetadata,
            )

           resultAnalyzer.getMusicMetadataFromRequest()
        } catch (_: Exception) {
            fileMetadata
        }
}
package com.github.enteraname74.cloudy.metadata.acoustid.model

import com.github.enteraname74.cloudy.metadata.acoustid.matching.TextSimilarity
import com.github.enteraname74.cloudy.metadata.model.MusicMetadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AcoustidRecording(
    val artists: List<AcoustidArtist>? = null,
    @SerialName("releasegroups")
    val releaseGroups: List<AcoustidReleaseGroup>? = null,
    val title: String? = null,
    // Number of fingerprints submitted for this recording, used as a popularity indicator.
    val sources: Int = 0,
) {
    /**
     * A recording without a title, artists or release groups would force us
     * to mix remote information with the file one, so we ignore it.
     */
    fun hasUsefulInformation(): Boolean =
        !title.isNullOrBlank()
            && !artists.isNullOrEmpty()
            && !releaseGroups.isNullOrEmpty()

    /**
     * Similarity between the recording and the tags of the file.
     * Only the tags present in the file are taken into account.
     *
     * @return a value between 0 and 1, or null if the file has no useful tags.
     */
    fun tagSimilarity(musicMetadata: MusicMetadata): Float? {
        val expectedArtists = TextSimilarity.splitArtists(musicMetadata.artists.map { it.name })

        val weightedScores: List<Pair<Float, Float>> = buildList {
            if (TextSimilarity.isKnown(musicMetadata.name)) {
                add(TITLE_WEIGHT to (title?.let { TextSimilarity.similarity(it, musicMetadata.name) } ?: 0f))
            }
            if (expectedArtists.isNotEmpty()) {
                add(ARTISTS_WEIGHT to artistsSimilarity(expectedArtists))
            }
            if (TextSimilarity.isKnown(musicMetadata.album.name)) {
                add(ALBUM_WEIGHT to (releaseGroups?.maxOfOrNull { it.titleSimilarity(musicMetadata.album.name) } ?: 0f))
            }
        }
        if (weightedScores.isEmpty()) return null

        return weightedScores.sumOf { (weight, score) -> (weight * score).toDouble() }.toFloat() /
            weightedScores.sumOf { (weight, _) -> weight.toDouble() }.toFloat()
    }

    /**
     * Part of the recording artists matching the expected ones.
     * Returns a value between 0 and 1.
     */
    private fun artistsSimilarity(expectedArtists: List<String>): Float {
        val recordingArtists = artists.orEmpty()
        if (recordingArtists.isEmpty()) return 0f

        return recordingArtists
            .map { artist -> expectedArtists.maxOf { TextSimilarity.similarity(artist.name, it) } }
            .average()
            .toFloat()
    }

    /**
     * Retrieve the release group that suits the best the given metadata.
     * The album name matters more than the type of the release.
     */
    fun optimalRelease(musicMetadata: MusicMetadata): AcoustidReleaseGroup? =
        releaseGroups?.maxByOrNull { it.releaseScore(musicMetadata) }

    /**
     * Retrieve the preferred release group using only the Acoustid data.
     */
    fun optimalRelease(): AcoustidReleaseGroup? =
        releaseGroups?.maxByOrNull { it.preference() }

    private fun AcoustidReleaseGroup.releaseScore(musicMetadata: MusicMetadata): Float =
        if (TextSimilarity.isKnown(musicMetadata.album.name)) {
            RELEASE_TITLE_WEIGHT * titleSimilarity(musicMetadata.album.name) + RELEASE_PREFERENCE_WEIGHT * preference()
        } else {
            preference()
        }

    private companion object {
        const val TITLE_WEIGHT = .45f
        const val ARTISTS_WEIGHT = .35f
        const val ALBUM_WEIGHT = .2f

        const val RELEASE_TITLE_WEIGHT = .7f
        const val RELEASE_PREFERENCE_WEIGHT = .3f
    }
}

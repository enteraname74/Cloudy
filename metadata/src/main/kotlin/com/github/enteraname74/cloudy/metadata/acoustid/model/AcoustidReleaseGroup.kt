package com.github.enteraname74.cloudy.metadata.acoustid.model

import com.github.enteraname74.cloudy.metadata.acoustid.matching.TextSimilarity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AcoustidReleaseGroup(
    // MusicBrainz id of the release group, used to fetch its cover.
    val id: String? = null,
    val title: String? = null,
    val type: String? = null,
    // Acoustid omits the field when the release group has no secondary types.
    @SerialName("secondarytypes") val secondaryTypes: List<String> = emptyList(),
    val artists: List<AcoustidArtist>? = null,
) {
    /**
     * Similarity between the title of the release group and the expected album name.
     * Returns a value between 0 and 1.
     */
    fun titleSimilarity(expectedTitle: String): Float =
        title?.let { TextSimilarity.similarity(it, expectedTitle) } ?: 0f

    /**
     * How much we prefer this release group over another one, regardless of the expected metadata.
     * A regular studio album is preferred over a single, a live or a compilation.
     * Returns a value between 0 and 1.
     */
    fun preference(): Float {
        val typeWeight = Type.fromString(type).weight
        // A release with several secondary types (ex: a live compilation) is as bad as its worst one.
        val secondaryTypeWeight = secondaryTypes.minOfOrNull { SecondaryType.fromString(it).weight } ?: 1f

        return typeWeight * secondaryTypeWeight
    }

    enum class Type(
        val value: String,
        val weight: Float,
    ) {
        Album("Album", 1f),
        EP("EP", .8f),
        Single("Single", .7f),
        Other("Other", .4f),
        Unknown("Unknown", .4f);

        companion object {
            fun fromString(value: String?): Type =
                entries.find { it.value.equals(value, ignoreCase = true) } ?: Unknown
        }
    }

    enum class SecondaryType(
        val value: String,
        val weight: Float,
    ) {
        Live("Live", .6f),
        Compilation("Compilation", .5f),
        Soundtrack("Soundtrack", .5f),
        Remix("Remix", .4f),
        DjMix("DJ-mix", .3f),
        Unknown("Unknown", .3f);

        companion object {
            fun fromString(value: String): SecondaryType =
                entries.find { it.value.equals(value, ignoreCase = true) } ?: Unknown
        }
    }
}

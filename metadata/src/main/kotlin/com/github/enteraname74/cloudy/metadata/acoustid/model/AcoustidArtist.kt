package com.github.enteraname74.cloudy.metadata.acoustid.model

import com.github.enteraname74.cloudy.metadata.acoustid.matching.TextSimilarity
import kotlinx.serialization.Serializable

/**
 * An artist credit from Acoustid.
 *
 * @param id the MusicBrainz id of the artist. The same artist can be credited with different names
 * depending on the recording or release (ex: "芳野藤丸" and "Yoshino Fujimal"), but always has the same id.
 * @param name the name of the artist, as credited.
 */
@Serializable
internal data class AcoustidArtist(
    val id: String? = null,
    val name: String,
) {
    /**
     * Tells if two credits are for the same artist.
     * - If both have an id, only the ids are compared: two artists with the same name and different ids are different artists.
     * - Otherwise, their normalized names are compared.
     */
    fun isSameArtistAs(other: AcoustidArtist): Boolean =
        if (id != null && other.id != null) {
            id == other.id
        } else {
            val normalizedName = TextSimilarity.normalize(name)
            normalizedName.isNotEmpty() && normalizedName == TextSimilarity.normalize(other.name)
        }
}

/**
 * Removes the credits of the same artist (see [AcoustidArtist.isSameArtistAs]), keeping the first one.
 */
internal fun List<AcoustidArtist>.distinctArtists(): List<AcoustidArtist> =
    fold(emptyList()) { kept, artist ->
        if (kept.any { it.isSameArtistAs(artist) }) kept else kept + artist
    }

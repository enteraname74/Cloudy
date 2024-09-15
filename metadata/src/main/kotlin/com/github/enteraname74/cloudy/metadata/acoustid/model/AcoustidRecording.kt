package com.github.enteraname74.cloudy.metadata.acoustid.model

import com.github.enteraname74.cloudy.metadata.model.MusicMetadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AcoustidRecording(
    val artists: List<AcoustidArtist>?,
    @SerialName("releasegroups")
    val releaseGroups: List<AcoustidReleaseGroup>?,
    val title: String?,
) {
    fun isMatchingMetadata(musicMetadata: MusicMetadata): Boolean {
        if (!hasUsefulInformation()) return false

        val isMatchingWithArtist = artists!!.any { artist -> artist.name == musicMetadata.artist }
        val isMatchingWithAlbum = releaseGroups!!.any { release -> release.title == musicMetadata.album }

        return isMatchingWithAlbum && isMatchingWithArtist
    }


    fun hasUsefulInformation(): Boolean =
        title != null && !artists.isNullOrEmpty() && !releaseGroups.isNullOrEmpty()

    fun doesContainsAlbum(): Boolean =
        releaseGroups?.any { it.type == "Album" } == true
}

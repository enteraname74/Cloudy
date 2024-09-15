package com.github.enteraname74.cloudy.metadata.acoustid.model

import com.github.enteraname74.cloudy.metadata.model.MusicMetadata

internal class AcoustidResultAnalyzer(
    private val requestResult: AcoustidLookupRequestResult,
    private val initialMetadata: MusicMetadata,
) {

    /**
     * Retrieve the music title from a recording.
     *
     * @param recording the recording containing the music title.
     * @return the title of the music. If nothing was found, returns the title from the initial metadata.
     */
    private fun getMusicTitle(recording: AcoustidRecording): String =
        recording.title ?: initialMetadata.name

    /**
     * Retrieve the album name of a recording.
     *
     * @param recording the recording containing the album name.
     * @return the name of the album. If nothing was found, returns the album from the initial metadata.
     */
    private fun getAlbumName(recording: AcoustidRecording): String =
        if (recording.releaseGroups.isNullOrEmpty()) {
            initialMetadata.album
        } else {
            recording.releaseGroups.find { album ->
                album.title == initialMetadata.album
            }?.title ?: recording.releaseGroups.first().title
        }

    /**
     * Retrieve the artist name of a recording.
     *
     * @param recording the recording containing the artist name.
     * @return the name of the artist. If nothing was found, returns the artist from the initial metadata.
     */
    private fun getArtistName(recording: AcoustidRecording): String =
        if (recording.artists.isNullOrEmpty()) {
            initialMetadata.artist
        } else {
            recording.artists.find { artist ->
                artist.name == initialMetadata.artist
            }?.name ?: recording.artists.first().name
        }

    /**
     * Retrieve the optimal match from the request result.
     * The optimal match is the one with the better score.
     *
     * @return the optimal match from the request result. If no matches are found, return nothing.
     */
    private fun getOptimalMatch(): AcoustidMatch? {
        if (requestResult.results.isEmpty()) return null

        val sortedResult = requestResult.results.sortedByDescending { it.score }
        return sortedResult.first()
    }

    /**
     * Tries to fetch an optimal recording matching with the initial metadata given.
     *
     * @param match the match to analyze.
     * @return a recording matching with the initial metadata or nothing.
     */
    private fun getOptimalRecordingMatchingMetadata(match: AcoustidMatch): AcoustidRecording? =
        match.recordings.firstOrNull { recording ->
            recording.hasUsefulInformation()
                    && recording.doesContainsAlbum()
                    && recording.isMatchingMetadata(musicMetadata = initialMetadata)
        }

    /**
     * Retrieve the optimal recording between all the one proposed.
     * - The optimal recording includes an album.
     * - The optimal recording matches, if possible, with the given metadata.
     *
     * @param match the match containing all recordings.
     * @return the optimal recording or nothing if there is no recordings.
     */
    private fun getOptimalRecording(match: AcoustidMatch): AcoustidRecording? {
        if (match.recordings.isEmpty()) return null

        // We first try to retrieve a recording matching with the initial metadata of the file.
        val matchingMetadataRecording: AcoustidRecording? = getOptimalRecordingMatchingMetadata(
            match = match
        )
        if (matchingMetadataRecording != null) return matchingMetadataRecording

        // If nothing was found, we try to find another useful recording.
        return match.recordings.firstOrNull { recording ->
            recording.hasUsefulInformation() && recording.doesContainsAlbum()
        }
    }

    /**
     * Retrieve useful music information from the result of a lookup request.
     *
     * @return the music metadata from a request. If nothing is found, return the initial metadata.
     */
    fun getMusicMetadataFromRequest() : MusicMetadata{
        val optimalMatch: AcoustidMatch = getOptimalMatch() ?: return initialMetadata
        val optimalRecording: AcoustidRecording = getOptimalRecording(match = optimalMatch) ?: return initialMetadata

        return MusicMetadata(
            name = getMusicTitle(optimalRecording),
            album = getAlbumName(optimalRecording),
            artist = getArtistName(optimalRecording),
            duration = initialMetadata.duration,
        )
    }
}
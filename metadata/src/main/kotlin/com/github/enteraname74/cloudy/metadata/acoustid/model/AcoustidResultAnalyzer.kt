package com.github.enteraname74.cloudy.metadata.acoustid.model

import com.github.enteraname74.cloudy.metadata.acoustid.matching.TextSimilarity
import com.github.enteraname74.cloudy.metadata.model.MusicMetadata

internal class AcoustidResultAnalyzer(
    private val requestResult: AcoustidLookupRequestResult,
    private val initialMetadata: MusicMetadata,
) {
    /**
     * A possible recording to use, with the release group picked for it.
     * The score is between 0 and 1, whether it was computed with the tags of the file or not.
     */
    private data class Candidate(
        val recording: AcoustidRecording,
        val release: AcoustidReleaseGroup?,
        val tagSimilarity: Float?,
        val score: Float,
    )

    private val byScore: Comparator<Candidate> =
        compareBy<Candidate> { it.score }.thenBy { it.recording.sources }

    /**
     * Retrieve the useful recordings of the matches with a sufficient fingerprint score, with their match score.
     */
    private fun getUsefulRecordings(): List<Pair<AcoustidRecording, Float>> =
        requestResult.results
            .filter { it.score >= MIN_MATCH_SCORE }
            .flatMap { match -> match.recordings.map { it to match.score } }
            .filter { (recording, _) -> recording.hasUsefulInformation() }

    /**
     * Retrieve the best candidate when comparing the recordings with the tags of the file.
     *
     * @return the best candidate, or nothing if the file has no useful tags.
     */
    private fun getBestCandidateWithTags(recordings: List<Pair<AcoustidRecording, Float>>): Candidate? =
        recordings
            .mapNotNull { (recording, matchScore) ->
                val tagSimilarity = recording.tagSimilarity(initialMetadata) ?: return@mapNotNull null
                val release = recording.optimalRelease(initialMetadata)
                Candidate(
                    recording = recording,
                    release = release,
                    tagSimilarity = tagSimilarity,
                    score = TAGS_MATCH_WEIGHT * matchScore +
                        TAGS_WEIGHT * tagSimilarity +
                        TAGS_RELEASE_WEIGHT * (release?.preference() ?: 0f),
                )
            }
            .maxWithOrNull(byScore)

    /**
     * Retrieve the best candidate using only the Acoustid data, without looking at the tags of the file.
     */
    private fun getBestCandidateFromAcoustid(recordings: List<Pair<AcoustidRecording, Float>>): Candidate? =
        recordings
            .map { (recording, matchScore) ->
                val release = recording.optimalRelease()
                Candidate(
                    recording = recording,
                    release = release,
                    tagSimilarity = null,
                    score = ACOUSTID_MATCH_WEIGHT * matchScore +
                        ACOUSTID_RELEASE_WEIGHT * (release?.preference() ?: 0f),
                )
            }
            .maxWithOrNull(byScore)

    /**
     * Retrieve the best candidate among all the useful recordings.
     * - If a recording matches the tags of the file, it is used.
     * - Otherwise, the tags are missing or make no sense, so we also look for the best recording using only
     * the Acoustid data and keep the one with the highest score.
     * - A candidate with a score too low for its pass is ignored, so if none is left, the file metadata is kept.
     */
    private fun getOptimalCandidate(): Candidate? {
        val recordings = getUsefulRecordings()

        val bestWithTags: Candidate? = getBestCandidateWithTags(recordings)
            ?.takeIf { it.score >= MIN_SCORE_WITH_TAGS }
        if (bestWithTags?.tagSimilarity != null && bestWithTags.tagSimilarity >= MIN_TAG_SIMILARITY) {
            return bestWithTags
        }

        val bestFromAcoustid: Candidate? = getBestCandidateFromAcoustid(recordings)
            ?.takeIf { it.score >= MIN_SCORE_FROM_ACOUSTID }
        return listOfNotNull(bestWithTags, bestFromAcoustid).maxWithOrNull(byScore)
    }

    /**
     * Retrieve useful music information from the result of a lookup request.
     *
     * @return the music metadata from a request. If nothing is found, return the initial metadata.
     */
    fun getMusicMetadataFromRequest(): MusicMetadata {
        val candidate: Candidate = getOptimalCandidate() ?: return initialMetadata
        val recording = candidate.recording

        // A candidate always has a title, artists and release groups.
        val songArtists: List<AcoustidArtist> = recording.artists.orEmpty().distinctArtists()
        val allArtists = songArtists.map { it.toMusicMetadataArtist() }
        val albumName = candidate.release?.title ?: initialMetadata.album.name
        // The release group artist handles compilations ("Various Artists").
        // If it is one of the song artists credited differently (ex: "ARTISTE UN" and "ARTIST ONE"),
        // the song artist is used, to avoid having two artists for the same person.
        val albumArtist = candidate.release?.artists?.firstOrNull()
            ?.let { releaseArtist -> songArtists.firstOrNull { it.isSameArtistAs(releaseArtist) } ?: releaseArtist }
            ?.toMusicMetadataArtist()
            ?: allArtists.first()

        val isSameAlbumAsFile = TextSimilarity.isKnown(initialMetadata.album.name) &&
            TextSimilarity.similarity(albumName, initialMetadata.album.name) >= SAME_ALBUM_SIMILARITY

        return MusicMetadata(
            name = recording.title ?: initialMetadata.name,
            album = MusicMetadata.Album(
                name = albumName,
                artist = albumArtist,
                nbPlayed = 0,
                isInQuickAccess = false,
            ),
            artists = allArtists,
            duration = initialMetadata.duration,
            // The position from the file is only relevant if the album stays the same.
            albumPosition = initialMetadata.albumPosition.takeIf { isSameAlbumAsFile },
            nbPlayed = 0,
            isInQuickAccess = false,
            releaseGroupId = candidate.release?.id,
        ).updateNbPlayedAndQuickAccessFromOther(initialMetadata)
    }

    private fun AcoustidArtist.toMusicMetadataArtist(): MusicMetadata.Artist =
        MusicMetadata.Artist(
            name = name,
            nbPlayed = 0,
            isInQuickAccess = false,
        )

    private companion object {
        // Matches below this fingerprint score are too uncertain to be used.
        const val MIN_MATCH_SCORE = .8f

        // Below this tag similarity, the tags of the file are considered missing or wrong.
        const val MIN_TAG_SIMILARITY = .5f
        const val SAME_ALBUM_SIMILARITY = .9f

        // Below these scores, the candidate of a pass is too uncertain to replace the file metadata.
        const val MIN_SCORE_WITH_TAGS = .6f
        const val MIN_SCORE_FROM_ACOUSTID = .7f

        const val TAGS_MATCH_WEIGHT = .3f
        const val TAGS_WEIGHT = .5f
        const val TAGS_RELEASE_WEIGHT = .2f

        const val ACOUSTID_MATCH_WEIGHT = .6f
        const val ACOUSTID_RELEASE_WEIGHT = .4f
    }
}

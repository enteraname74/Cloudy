package com.github.enteraname74.cloudy.metadata.model

internal data class MusicMetadata(
    val name: String,
    val artist: String,
    val album: String,
    val duration: Long,
) {
    fun replaceBlank(): MusicMetadata =
        this.copy(
            name = name.ifBlank { "Unknown" },
            artist = artist.ifBlank { "Unknown" },
            album = album.ifBlank { "Unknown" },
        )

    companion object {
        fun unknownMusicMetadata(): MusicMetadata =
            MusicMetadata(
                name = "Unknown",
                artist = "Unknown",
                album = "Unknown",
                duration = 0L,
            )
    }
}

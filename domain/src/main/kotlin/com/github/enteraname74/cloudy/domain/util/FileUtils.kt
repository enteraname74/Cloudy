package com.github.enteraname74.cloudy.domain.util

import io.ktor.http.content.PartData

object FileUtils {
    fun getFileExtension(fileName: String): String? =
        fileName
            .substringAfterLast('.', "")
            .takeIf { it.isNotEmpty() }

    fun isMusicFile(part: PartData.FileItem): Boolean {
        val type: String = part.contentType?.toString() ?: return false
        val authorizedMimeTypes =
            listOf(
                "audio/mpeg",        // MP3 files
                "audio/mp4",         // MP4 files with audio (M4A)
                "audio/wav",         // WAV files
                "audio/ogg",         // Ogg Vorbis files
                "audio/flac",        // FLAC files
                "audio/aac",         // AAC files
                "audio/opus",        // Opus files
                "audio/x-ms-wma",    // Windows Media Audio files
                "audio/aiff",        // AIFF files
                "audio/webm",        // WebM files with audio
                "audio/amr",         // AMR files
                "audio/vnd.rn-realaudio", // RealAudio files
                "audio/midi",        // MIDI files
                "audio/3gpp",        // 3GP files with audio
                "audio/x-m4a",       // M4A files
                "audio/x-xmf",       // XMF files
                "audio/x-ms-asf",    // ASF files with audio
                "audio/x-wav",       // RAW audio format (often used for WAV)
                "audio/eac3"         // Enhanced AC-3 files
            )
        return authorizedMimeTypes.contains(type)
    }

    fun isImageFile(part: PartData.FileItem): Boolean {
        val type: String = part.contentType?.toString() ?: return false
        val authorizedMimeTypes =
            listOf(
                "image/jpeg",
                "image/png",
                "image/gif",
                "image/bmp",
                "image/webp",
                "image/heif",
                "image/heic",
                "image/tiff",
                "image/svg+xml",
                "image/x-icon",
                "image/vnd.microsoft.icon",
                "image/avif",
                "image/jxr" // JPEG XR
            )

        return authorizedMimeTypes.contains(type)
    }
}
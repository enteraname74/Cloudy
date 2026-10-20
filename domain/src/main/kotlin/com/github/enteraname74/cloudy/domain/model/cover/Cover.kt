package com.github.enteraname74.cloudy.domain.model.cover

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Cover {
    fun paths(): List<String?>

    @Serializable
    enum class Mode {
        @SerialName("simple")
        Simple,

        @SerialName("grid")
        Grid,
    }

    @Serializable
    @SerialName("simple")
    data class Simple(
        val path: String,
    ) : Cover {
        override fun paths(): List<String?> = listOf(path, null, null, null)
    }

    @Serializable
    @SerialName("grid")
    data class Grid(
        val topStart: String?,
        val topEnd: String?,
        val bottomStart: String?,
        val bottomEnd: String?,
    ) : Cover {
        override fun paths(): List<String?> = listOf(topStart, topEnd, bottomStart, bottomEnd)
    }

    companion object {
        const val MAX_PATHS: Int = 4

        fun from(paths: List<String?>): Cover? {
            val normalizedPaths = List(MAX_PATHS) { index ->
                paths.getOrNull(index)?.takeIf(String::isNotBlank)
            }
            return when (normalizedPaths.count { it != null }) {
                0 -> null
                1 -> Simple(normalizedPaths.firstNotNullOf { it })
                else -> Grid(
                    topStart = normalizedPaths[0],
                    topEnd = normalizedPaths[1],
                    bottomStart = normalizedPaths[2],
                    bottomEnd = normalizedPaths[3],
                )
            }
        }

        fun update(
            cover: Cover?,
            mode: Mode,
            paths: List<String?>,
        ): Cover? {
            val currentPaths = cover?.paths().orEmpty()
            val updatedPaths = List(MAX_PATHS) { index ->
                paths.getOrNull(index)?.takeIf(String::isNotBlank)
                    ?: currentPaths.getOrNull(index)?.takeIf(String::isNotBlank)
            }
            return when (mode) {
                Mode.Simple -> updatedPaths[0]?.let(::Simple)
                Mode.Grid -> if (updatedPaths.any { it != null }) {
                    Grid(
                        topStart = updatedPaths[0],
                        topEnd = updatedPaths[1],
                        bottomStart = updatedPaths[2],
                        bottomEnd = updatedPaths[3],
                    )
                } else {
                    null
                }
            }
        }
    }
}

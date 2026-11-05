package com.github.enteraname74.cloudy.metadata.acoustid.matching

import java.text.Normalizer

internal object TextSimilarity {
    private const val UNKNOWN_VALUE = "Unknown"

    private val diacriticsRegex = Regex("\\p{M}+")
    private val bracketedPartRegex = Regex("[(\\[][^)\\]]*[)\\]]")
    private val featuringRegex = Regex("\\s(feat\\.?|ft\\.?|featuring)\\s.*$", RegexOption.IGNORE_CASE)
    private val nonAlphanumericRegex = Regex("[^\\p{L}\\p{N}]+")
    private val artistSeparatorRegex = Regex("\\s*(,|;|/|&|\\sfeat\\.?\\s|\\sft\\.?\\s|\\sfeaturing\\s)\\s*", RegexOption.IGNORE_CASE)

    /**
     * Tells if a value holds real information.
     * Missing tags are replaced by "Unknown" when reading a file, so it is considered absent.
     */
    fun isKnown(value: String?): Boolean =
        !value.isNullOrBlank() && !value.equals(UNKNOWN_VALUE, ignoreCase = true)

    /**
     * Lowercases a value and removes its accents and punctuation.
     */
    fun normalize(value: String): String =
        Normalizer
            .normalize(value, Normalizer.Form.NFD)
            .replace(diacriticsRegex, "")
            .lowercase()
            .replace(nonAlphanumericRegex, " ")
            .trim()

    /**
     * Removes the parts of a title that often differ between sources, like "(Deluxe Edition)" or "feat. X".
     */
    private fun stripDecorations(value: String): String =
        value
            .replace(bracketedPartRegex, " ")
            .replace(featuringRegex, "")

    /**
     * Builds a similarity between two values.
     * Returns a value between 0 and 1, 0 if one of the values is blank.
     */
    fun similarity(first: String, second: String): Float {
        val fullSimilarity = normalizedSimilarity(normalize(first), normalize(second))
        val strippedSimilarity = normalizedSimilarity(
            normalize(stripDecorations(first)),
            normalize(stripDecorations(second)),
        )
        return maxOf(fullSimilarity, strippedSimilarity)
    }

    /**
     * Splits a tag holding several artists ("A feat. B", "A & B"...).
     * The original name is kept, as some artists contain a separator in their name.
     */
    fun splitArtists(names: List<String>): List<String> =
        names
            .flatMap { name -> listOf(name) + name.split(artistSeparatorRegex) }
            .filter { isKnown(it) }
            .distinct()

    private fun normalizedSimilarity(first: String, second: String): Float {
        if (first.isEmpty() || second.isEmpty()) return 0f
        if (first == second) return 1f

        val maxLength = maxOf(first.length, second.length)
        return 1f - levenshteinDistance(first, second).toFloat() / maxLength.toFloat()
    }

    private fun levenshteinDistance(first: String, second: String): Int {
        var previousRow = IntArray(second.length + 1) { it }
        var currentRow = IntArray(second.length + 1)

        for (i in 1..first.length) {
            currentRow[0] = i
            for (j in 1..second.length) {
                val substitutionCost = if (first[i - 1] == second[j - 1]) 0 else 1
                currentRow[j] = minOf(
                    previousRow[j] + 1,
                    currentRow[j - 1] + 1,
                    previousRow[j - 1] + substitutionCost,
                )
            }
            val swap = previousRow
            previousRow = currentRow
            currentRow = swap
        }
        return previousRow[second.length]
    }
}

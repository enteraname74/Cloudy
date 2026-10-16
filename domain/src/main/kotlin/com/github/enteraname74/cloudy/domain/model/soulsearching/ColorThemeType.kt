package com.github.enteraname74.cloudy.domain.model.soulsearching

enum class ColorThemeType(val raw: Int) {
    DYNAMIC(0),
    SYSTEM(1),
    PERSONALIZED(2);

    companion object {
        fun fromRawOrDefault(raw: Int): ColorThemeType = entries.find { it.raw == raw } ?: DYNAMIC
    }
}
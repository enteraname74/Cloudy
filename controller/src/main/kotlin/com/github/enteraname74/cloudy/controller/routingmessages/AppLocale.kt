package com.github.enteraname74.cloudy.controller.routingmessages

enum class AppLocale(val value: String) {
    Fr("fr"),
    En("en");

    companion object {
        fun fromValue(value: String?): AppLocale =
            entries.firstOrNull { it.value == value } ?: En
    }
}
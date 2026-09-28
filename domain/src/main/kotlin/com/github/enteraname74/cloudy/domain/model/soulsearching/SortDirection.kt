package com.github.enteraname74.cloudy.domain.model.soulsearching

enum class SortDirection(val value: Int) {
    ASC(0),
    DESC(1);

    companion object {
        fun fromOrDefault(value: Int): SortDirection =
            SortDirection.entries.firstOrNull { it.value == value } ?: ASC
    }
}
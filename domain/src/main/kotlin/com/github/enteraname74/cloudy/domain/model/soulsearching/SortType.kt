package com.github.enteraname74.cloudy.domain.model.soulsearching

enum class SortType(val value: Int) {
    NAME(0),
    ADDED_DATE(1),
    NB_PLAYED(2);

    companion object {
        fun fromOrDefault(value: Int): SortType =
            SortType.entries.firstOrNull { it.value == value } ?: NAME
    }
}
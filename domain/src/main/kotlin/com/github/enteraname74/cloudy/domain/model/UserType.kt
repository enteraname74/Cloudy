package com.github.enteraname74.cloudy.domain.model

enum class UserType(val value: String) {
    User("user"),
    Admin("admin");

    companion object {
        fun fromString(value: String): UserType =
            entries.find { it.value == value } ?: User
    }
}
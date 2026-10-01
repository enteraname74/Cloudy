package com.github.enteraname74.cloudy.domain.model.user

enum class UserType(val value: String) {
    User("user"),
    Admin("admin");

    companion object {
        fun fromString(value: String): UserType =
            entries.find { it.value == value } ?: User
    }
}
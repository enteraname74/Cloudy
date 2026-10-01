package com.github.enteraname74.cloudy.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val backendVersion: Int,
    val dbVersion: Int,
) {
    companion object {
        val CURRENT = Settings(
            backendVersion = 1,
            dbVersion = 1,
        )
    }
}

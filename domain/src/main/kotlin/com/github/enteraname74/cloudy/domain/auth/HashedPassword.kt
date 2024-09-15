package com.github.enteraname74.cloudy.domain.auth

import kotlinx.serialization.Serializable

@Serializable
class HashedPassword(
    val hash: ByteArray,
    val salt: ByteArray,
)

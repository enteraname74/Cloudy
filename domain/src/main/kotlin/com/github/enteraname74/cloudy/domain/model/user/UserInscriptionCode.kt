package com.github.enteraname74.cloudy.domain.model.user

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UserInscriptionCode(
    val ownerId: Uuid,
    val code: Uuid,
)
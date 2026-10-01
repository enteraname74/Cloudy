package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.user.UserInscriptionCode
import kotlin.uuid.Uuid

interface UserInscriptionCodeRepository {
    suspend fun generate(userId: Uuid): UserInscriptionCode

    suspend fun getFromCode(code: Uuid): UserInscriptionCode?

    suspend fun allOfUser(userId: Uuid): List<UserInscriptionCode>

    suspend fun delete(code: Uuid)
}
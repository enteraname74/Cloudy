package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.user.UserInscriptionCode
import com.github.enteraname74.cloudy.domain.repository.UserInscriptionCodeRepository
import com.github.enteraname74.cloudy.repository.datasource.UserInscriptionCodeDataSource
import kotlin.uuid.Uuid

class UserInscriptionCodeRepositoryImpl(
    private val dataSource: UserInscriptionCodeDataSource,
) : UserInscriptionCodeRepository {
    override suspend fun generate(userId: Uuid): UserInscriptionCode =
        dataSource.generate(userId = userId)

    override suspend fun getFromCode(code: Uuid): UserInscriptionCode? =
        dataSource.getFromCode(code = code)

    override suspend fun allOfUser(userId: Uuid): List<UserInscriptionCode> =
        dataSource.allOfUser(userId = userId)

    override suspend fun delete(code: Uuid) {
        dataSource.delete(code = code)
    }
}
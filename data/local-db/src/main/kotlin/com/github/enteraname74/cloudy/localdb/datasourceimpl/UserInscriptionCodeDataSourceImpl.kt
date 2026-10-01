package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.user.UserInscriptionCode
import com.github.enteraname74.cloudy.localdb.table.UserInscriptionCodeEntity
import com.github.enteraname74.cloudy.localdb.table.UserInscriptionCodeTable
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.UserInscriptionCodeDataSource
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import kotlin.uuid.Uuid

class UserInscriptionCodeDataSourceImpl: UserInscriptionCodeDataSource {
    override suspend fun generate(userId: Uuid): UserInscriptionCode =
        workTransaction {

            val code = Uuid.random()
            UserInscriptionCodeTable.insert(
                userId = userId,
                code = code
            )
            UserInscriptionCodeEntity.findById(code)!!.toUserInscriptionCode()
        }

    override suspend fun getFromCode(code: Uuid): UserInscriptionCode? =
        workTransaction {
            UserInscriptionCodeEntity.findById(code)?.toUserInscriptionCode()
        }

    override suspend fun allOfUser(userId: Uuid): List<UserInscriptionCode> =
        workTransaction {
            UserInscriptionCodeEntity
                .find { UserInscriptionCodeTable.userId eq userId }
                .map { it.toUserInscriptionCode() }
        }

    override suspend fun delete(code: Uuid) {
        workTransaction {
            UserInscriptionCodeTable.deleteWhere { UserInscriptionCodeTable.id eq code }
        }
    }
}
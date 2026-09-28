package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.soulsearching.SoulSearchingSettings
import com.github.enteraname74.cloudy.domain.repository.SoulSearchingSettingsRepository
import com.github.enteraname74.cloudy.repository.datasource.SoulSearchingSettingsDataSource
import kotlin.uuid.Uuid

class SoulSearchingSettingsRepositoryImpl(
    private val dataSource: SoulSearchingSettingsDataSource,
) : SoulSearchingSettingsRepository {
    override suspend fun get(userId: Uuid): SoulSearchingSettings? =
        dataSource.get(userId = userId)

    override suspend fun upsert(
        userId: Uuid,
        settings: SoulSearchingSettings,
    ) {
        dataSource.upsert(
            userId = userId,
            settings = settings,
        )
    }
}

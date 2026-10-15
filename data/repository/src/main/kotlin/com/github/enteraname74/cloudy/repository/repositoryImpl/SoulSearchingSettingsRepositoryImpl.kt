package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.soulsearching.SoulSearchingSettings
import com.github.enteraname74.cloudy.domain.repository.SoulSearchingSettingsRepository
import com.github.enteraname74.cloudy.repository.datasource.SoulSearchingSettingsDataSource
import kotlin.uuid.Uuid

class SoulSearchingSettingsRepositoryImpl(
    private val dataSource: SoulSearchingSettingsDataSource,
) : SoulSearchingSettingsRepository {
    override suspend fun get(
        userId: Uuid,
        lastUpdateAt: Long?,
    ): SoulSearchingSettings? =
        dataSource.get(
            userId = userId,
            lastUpdateAt = lastUpdateAt,
        )

    override suspend fun upsert(
        userId: Uuid,
        settings: SoulSearchingSettings,
    ): SoulSearchingSettings =
        dataSource.upsert(
            userId = userId,
            settings = settings,
        )
}

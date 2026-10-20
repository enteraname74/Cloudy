package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.soulsearching.SoulSearchingSettings
import com.github.enteraname74.cloudy.domain.repository.SoulSearchingSettingsRepository
import kotlin.uuid.Uuid

class SoulSearchingSettingsService(
    private val repository: SoulSearchingSettingsRepository,
) {
    suspend fun get(
        userId: Uuid,
        lastUpdateAt: Long?,
    ): SoulSearchingSettings? =
        repository.get(
            userId = userId,
            lastUpdateAt = lastUpdateAt,
        )

    suspend fun upsert(
        userId: Uuid,
        settings: SoulSearchingSettings,
    ): SoulSearchingSettings =
        repository.upsert(
            userId = userId,
            settings = settings,
        )
}

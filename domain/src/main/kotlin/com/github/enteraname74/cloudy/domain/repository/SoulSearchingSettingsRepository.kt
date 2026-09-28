package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.soulsearching.SoulSearchingSettings
import kotlin.uuid.Uuid

interface SoulSearchingSettingsRepository {
    suspend fun get(userId: Uuid): SoulSearchingSettings?

    suspend fun upsert(
        userId: Uuid,
        settings: SoulSearchingSettings,
    )
}

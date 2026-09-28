package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.soulsearching.SoulSearchingSettings
import kotlin.uuid.Uuid

interface SoulSearchingSettingsDataSource {
    suspend fun get(userId: Uuid): SoulSearchingSettings?

    suspend fun upsert(
        userId: Uuid,
        settings: SoulSearchingSettings,
    )
}

package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.soulsearching.SoulSearchingSettings
import com.github.enteraname74.cloudy.localdb.table.SoulSearchingSettingsEntity
import com.github.enteraname74.cloudy.localdb.table.SoulSearchingSettingsTable
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.SoulSearchingSettingsDataSource
import kotlin.uuid.Uuid

class SoulSearchingSettingsDataSourceImpl : SoulSearchingSettingsDataSource {
    override suspend fun get(
        userId: Uuid,
        lastUpdateAt: Long?,
    ): SoulSearchingSettings? =
        workTransaction {
            SoulSearchingSettingsEntity
                .findById(userId)
                ?.toSoulSearchingSettings()
                ?.takeIf { lastUpdateAt == null || it.updatedAtMillis > lastUpdateAt }
        }

    override suspend fun upsert(
        userId: Uuid,
        settings: SoulSearchingSettings,
    ): SoulSearchingSettings =
        workTransaction {
            SoulSearchingSettingsTable.upsert(
                userId = userId,
                settings = settings,
            )
            SoulSearchingSettingsEntity
                .findById(userId)
                ?.toSoulSearchingSettings()
                ?: error("Soul Searching settings upsert did not produce a stored row")
        }
}

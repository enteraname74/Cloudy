package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.soulsearching.SoulSearchingSettings
import com.github.enteraname74.cloudy.localdb.table.SoulSearchingSettingsEntity
import com.github.enteraname74.cloudy.localdb.table.SoulSearchingSettingsTable
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.SoulSearchingSettingsDataSource
import kotlin.uuid.Uuid

class SoulSearchingSettingsDataSourceImpl : SoulSearchingSettingsDataSource {
    override suspend fun get(userId: Uuid): SoulSearchingSettings? =
        workTransaction {
            SoulSearchingSettingsEntity
                .findById(userId)
                ?.toSoulSearchingSettings()
        }

    override suspend fun upsert(
        userId: Uuid,
        settings: SoulSearchingSettings,
    ) {
        workTransaction {
            SoulSearchingSettingsTable.upsert(
                userId = userId,
                settings = settings,
            )
        }
    }
}

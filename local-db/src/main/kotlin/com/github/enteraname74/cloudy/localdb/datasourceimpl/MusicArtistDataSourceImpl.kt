package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable
import com.github.enteraname74.cloudy.localdb.util.dbQuery
import com.github.enteraname74.cloudy.repository.datasource.MusicArtistDataSource
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.upsert

class MusicArtistDataSourceImpl: MusicArtistDataSource {
    override suspend fun upsert(musicArtist: MusicArtist) {
        dbQuery {
            MusicArtistTable.upsert {
                it[id] = musicArtist.id
                it[musicId] = musicArtist.musicId
                it[artistId] = musicArtist.artistId
            }
        }
    }

    override suspend fun delete(musicArtist: MusicArtist) {
        dbQuery {
            MusicArtistTable.deleteWhere {
                id eq musicArtist.id
            }
        }
    }
}
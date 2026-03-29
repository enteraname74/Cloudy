package com.github.enteraname74.cloudy.localdb

import com.github.enteraname74.cloudy.localdb.table.AlbumTable
import com.github.enteraname74.cloudy.localdb.table.ArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicPlaylistTable
import com.github.enteraname74.cloudy.localdb.table.MusicTable
import com.github.enteraname74.cloudy.localdb.table.PlaylistTable
import com.github.enteraname74.cloudy.localdb.table.UserTable
import com.github.enteraname74.cloudy.localdb.table.player.PlayedListMusicTable
import com.github.enteraname74.cloudy.localdb.table.player.PlayedListTable
import com.github.enteraname74.cloudy.localdb.table.player.PlayedListUserTable
import com.github.enteraname74.cloudy.logging.CloudyLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object AppDatabase {
    private val logger = CloudyLogger(this::class)
    private fun initTables() {
        transaction {
            logger.info("Will create missing tables and columns")
            SchemaUtils.create(
                MusicTable,
                ArtistTable,
                AlbumTable,
                UserTable,
                PlaylistTable,
                MusicArtistTable,
                MusicPlaylistTable,
                PlayedListTable,
                PlayedListUserTable,
                PlayedListMusicTable,
            )
        }
    }

    fun connectToDatabase(
        url: String,
        driver: String,
        user: String = "",
        password: String = "",
    ) {
        Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password,
        )
        initTables()
    }
}


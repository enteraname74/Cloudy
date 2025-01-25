package com.github.enteraname74.cloudy.localdb

import com.github.enteraname74.cloudy.localdb.table.AlbumTable
import com.github.enteraname74.cloudy.localdb.table.ArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicTable
import com.github.enteraname74.cloudy.localdb.table.UserTable
import com.github.enteraname74.cloudy.logging.CloudyLogger
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

object AppDatabase {
    private val logger = CloudyLogger(this::class)
    private fun initTables() {
        transaction {
            logger.info("Will create missing tables and columns")
            SchemaUtils.createMissingTablesAndColumns(
                MusicTable,
                ArtistTable,
                AlbumTable,
                UserTable,
                MusicArtistTable,
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


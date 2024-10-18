package com.github.enteraname74.cloudy.localdb

import com.github.enteraname74.cloudy.localdb.table.AlbumTable
import com.github.enteraname74.cloudy.localdb.table.ArtistTable
import com.github.enteraname74.cloudy.localdb.table.MusicTable
import com.github.enteraname74.cloudy.localdb.table.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object AppDatabase {
    private fun initTables() {
        transaction {
            println("DATABASE -- Will create missing tables and columns")
            SchemaUtils.createMissingTablesAndColumns(
                MusicTable,
                ArtistTable,
                AlbumTable,
                UserTable,
            )
        }
    }

    private fun initDatabase(
        dbName: String,
        db: Database?,
    ) {
        transaction(db) {
            println("DATABASE -- Will create database $dbName")
            connection.autoCommit = true
            SchemaUtils.createDatabase(dbName)
            connection.autoCommit = false
        }
    }

    fun connectToDatabase(
        url: String,
        driver: String,
        user: String = "",
        password: String = "",
    ) {
        println("Will connect to db with following info: url = $url, driver = $driver, user = $user, password = $password")
        Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password,
        )
//        initDatabase("api_db_DOS", db)
        initTables()
    }
}


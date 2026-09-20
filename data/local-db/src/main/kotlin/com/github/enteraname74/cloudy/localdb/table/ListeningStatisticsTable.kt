package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.ListeningStatistics
import com.github.enteraname74.cloudy.domain.model.LocalMonthYear
import com.github.enteraname74.cloudy.domain.model.music.MusicId
import com.github.enteraname74.cloudy.domain.util.DateUtils
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import kotlin.time.Duration.Companion.milliseconds

internal object ListeningStatisticsTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = text("id").entityId()
    override val primaryKey: PrimaryKey = PrimaryKey(id)

    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val nbPlayed = integer("nbPlayed")
    val timeListened = long("timeListened").nullable()
    val month = integer("month")
    val year = integer("year")
    val lastUpdateAt = long("lastUpdateAt")
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE).nullable()
    val albumId = reference("albumId", AlbumTable.id, onDelete = ReferenceOption.CASCADE).nullable()
    val artistId = reference("artistId", ArtistTable.id, onDelete = ReferenceOption.CASCADE).nullable()
    val playlistId = reference("playlistId", PlaylistTable.id, onDelete = ReferenceOption.CASCADE).nullable()

    fun upsertAll(statistics: List<ListeningStatistics>) {
        batchUpsert(statistics) { statistic ->
            this[id] = statistic.id
            this[userId] = statistic.userId
            this[nbPlayed] = statistic.nbPlayed
            this[timeListened] = statistic.timeListened?.inWholeMilliseconds
            this[month] = statistic.localMonthYear.month
            this[year] = statistic.localMonthYear.year
            this[lastUpdateAt] = DateUtils.now()
            this[musicId] = statistic.musicId?.raw
            this[albumId] = statistic.albumId
            this[artistId] = statistic.artistId
            this[playlistId] = statistic.playlistId
        }
    }
}

internal class ListeningStatisticsEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, ListeningStatisticsEntity>(ListeningStatisticsTable)

    var userId by ListeningStatisticsTable.userId
    var nbPlayed by ListeningStatisticsTable.nbPlayed
    var timeListened by ListeningStatisticsTable.timeListened
    var month by ListeningStatisticsTable.month
    var lastUpdateAt by ListeningStatisticsTable.lastUpdateAt
    var year by ListeningStatisticsTable.year
    var musicId by ListeningStatisticsTable.musicId
    var albumId by ListeningStatisticsTable.albumId
    var artistId by ListeningStatisticsTable.artistId
    var playlistId by ListeningStatisticsTable.playlistId

    fun toListeningStats(): ListeningStatistics =
        ListeningStatistics(
            id = id.value,
            userId = userId.value,
            nbPlayed = nbPlayed,
            timeListened = timeListened?.milliseconds,
            localMonthYear = LocalMonthYear(
                month = month,
                year = year,
            ),
            musicId = musicId?.value?.let(::MusicId),
            playlistId = playlistId?.value,
            albumId = albumId?.value,
            artistId = artistId?.value,
            lastUpdateAtMillis = lastUpdateAt
        )
}
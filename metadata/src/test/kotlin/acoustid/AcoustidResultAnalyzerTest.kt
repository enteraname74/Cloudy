package acoustid

import com.github.enteraname74.cloudy.metadata.acoustid.matching.TextSimilarity
import com.github.enteraname74.cloudy.metadata.acoustid.model.AcoustidLookupRequestResult
import com.github.enteraname74.cloudy.metadata.acoustid.model.AcoustidResultAnalyzer
import com.github.enteraname74.cloudy.metadata.acoustid.model.AcoustidReleaseGroup
import com.github.enteraname74.cloudy.metadata.model.MusicMetadata
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AcoustidResultAnalyzerTest {
    private val json = Json { ignoreUnknownKeys = true }

    private fun metadata(
        name: String = "Unknown",
        artists: List<String> = listOf("Unknown"),
        album: String = "Unknown",
        albumPosition: Int? = null,
    ) = MusicMetadata(
        name = name,
        artists = artists.map { artist(it) },
        album = MusicMetadata.Album(name = album, artist = artist(artists.first()), nbPlayed = 0, isInQuickAccess = false),
        duration = 1_000L,
        albumPosition = albumPosition,
        nbPlayed = 0,
        isInQuickAccess = false,
    )

    private fun artist(name: String) = MusicMetadata.Artist(name = name, nbPlayed = 0, isInQuickAccess = false)

    private fun analyze(response: String, initialMetadata: MusicMetadata): MusicMetadata =
        AcoustidResultAnalyzer(
            requestResult = json.decodeFromString<AcoustidLookupRequestResult>(response),
            initialMetadata = initialMetadata,
        ).getMusicMetadataFromRequest()

    @Test
    fun `release group without secondary types or type is deserialized`() {
        val result = json.decodeFromString<AcoustidLookupRequestResult>(
            """{"status":"ok","results":[{"id":"a","score":0.9,"recordings":[{"id":"r","releasegroups":[{"id":"g","title":"Album"}]}]}]}"""
        )
        val releaseGroup = result.results.first().recordings.first().releaseGroups!!.first()
        assertEquals(emptyList(), releaseGroup.secondaryTypes)
        assertNull(releaseGroup.type)
    }

    @Test
    fun `error response without results is deserialized`() {
        val result = json.decodeFromString<AcoustidLookupRequestResult>(
            """{"status":"error","error":{"code":4,"message":"invalid API key"}}"""
        )
        assertEquals(false, result.isSuccess)
        assertEquals("invalid API key", result.error?.message)
    }

    @Test
    fun `low fingerprint score is ignored`() {
        val initial = metadata(name = "Song", artists = listOf("Artist"), album = "Album")
        val response = """{"status":"ok","results":[{"id":"a","score":0.4,"recordings":[
            {"title":"Song","artists":[{"name":"Artist"}],"releasegroups":[{"title":"Album","type":"Album"}]}
        ]}]}"""
        assertEquals(initial, analyze(response, initial))
    }

    @Test
    fun `recording matching the tags wins over the first one`() {
        val initial = metadata(name = "Get Lucky", artists = listOf("Daft Punk feat. Pharrell Williams"), album = "Random Access Memories (Deluxe)")
        val response = """{"status":"ok","results":[{"id":"a","score":0.9,"recordings":[
            {"title":"Something Else","artists":[{"name":"Cover Band"}],"releasegroups":[{"title":"Hits","type":"Album"}],"sources":50},
            {"title":"Get Lucky","artists":[{"name":"Daft Punk"},{"name":"Pharrell Williams"}],"releasegroups":[
                {"title":"Now 85","type":"Album","secondarytypes":["Compilation"]},
                {"title":"Random Access Memories","type":"Album"}
            ],"sources":10}
        ]}]}"""
        val result = analyze(response, initial)
        assertEquals("Get Lucky", result.name)
        assertEquals(listOf("Daft Punk", "Pharrell Williams"), result.artists.map { it.name })
        assertEquals("Random Access Memories", result.album.name)
    }

    @Test
    fun `wrong tags fall back on the Acoustid data`() {
        val initial = metadata(name = "Song", artists = listOf("Artist"), album = "Album")
        val response = """{"status":"ok","results":[{"id":"a","score":0.85,"recordings":[
            {"title":"Totally Different","artists":[{"name":"Nobody"}],"releasegroups":[{"title":"Other","type":"Album"}]}
        ]}]}"""
        val result = analyze(response, initial)
        assertEquals("Totally Different", result.name)
        assertEquals("Other", result.album.name)
    }

    @Test
    fun `too low candidates of both passes keep the file metadata`() {
        val initial = metadata(name = "Song", artists = listOf("Artist"), album = "Album")
        val response = """{"status":"ok","results":[{"id":"a","score":0.8,"recordings":[
            {"title":"Totally Different","artists":[{"name":"Nobody"}],"releasegroups":[
                {"title":"Other","type":"Other","secondarytypes":["Live","Compilation"]}
            ]}
        ]}]}"""
        assertEquals(initial, analyze(response, initial))
    }

    @Test
    fun `wrong tags do not pick a release only because its title looks like them`() {
        val initial = metadata(name = "Track 01", artists = listOf("Artist"), album = "Album")
        val response = """{"status":"ok","results":[{"id":"a","score":0.9,"recordings":[
            {"title":"Real Song","artists":[{"name":"Real"}],"releasegroups":[
                {"title":"Album Hits","type":"Album","secondarytypes":["Compilation"]},
                {"title":"Real Record","type":"Album"}
            ]}
        ]}]}"""
        val result = analyze(response, initial)
        assertEquals("Real Song", result.name)
        assertEquals("Real Record", result.album.name)
    }

    @Test
    fun `untagged file uses the most popular studio album`() {
        val initial = metadata()
        val response = """{"status":"ok","results":[{"id":"a","score":0.95,"recordings":[
            {"title":"Song","artists":[{"name":"Artist"}],"releasegroups":[{"title":"Live Album","type":"Album","secondarytypes":["Live"]}],"sources":3},
            {"title":"Song","artists":[{"name":"Artist"}],"releasegroups":[{"title":"Studio Album","type":"Album"}],"sources":2},
            {"title":"Song","artists":[{"name":"Artist"}],"releasegroups":[{"title":"Studio Album","type":"Album"}],"sources":20}
        ]}]}"""
        val result = analyze(response, initial)
        assertEquals("Studio Album", result.album.name)
    }

    @Test
    fun `recordings without useful information are ignored`() {
        val initial = metadata()
        val response = """{"status":"ok","results":[{"id":"a","score":0.95,"recordings":[{"id":"r"}]}]}"""
        assertEquals(initial, analyze(response, initial))
    }

    @Test
    fun `compilation uses the release group artist and keeps the position of the same album`() {
        val initial = metadata(name = "Song", artists = listOf("Artist"), album = "Best Of", albumPosition = 4)
        val response = """{"status":"ok","results":[{"id":"a","score":0.95,"recordings":[
            {"title":"Song","artists":[{"name":"Artist"}],"releasegroups":[
                {"title":"Best Of","type":"Album","secondarytypes":["Compilation"],"artists":[{"name":"Various Artists"}]}
            ]}
        ]}]}"""
        val result = analyze(response, initial)
        assertEquals("Various Artists", result.album.artist.name)
        assertEquals(4, result.albumPosition)
    }

    @Test
    fun `release group id of the picked release is kept for the cover`() {
        val initial = metadata(name = "Song", artists = listOf("Artist"), album = "Album")
        val response = """{"status":"ok","results":[{"id":"a","score":0.95,"recordings":[
            {"title":"Song","artists":[{"name":"Artist"}],"releasegroups":[
                {"id":"compilation-id","title":"Hits","type":"Album","secondarytypes":["Compilation"]},
                {"id":"album-id","title":"Album","type":"Album"}
            ]}
        ]}]}"""
        assertEquals("album-id", analyze(response, initial).releaseGroupId)
    }

    @Test
    fun `stats of the file are kept on the remote metadata`() {
        val initial = metadata(name = "Song", artists = listOf("Artist"), album = "Album").copy(nbPlayed = 12, isInQuickAccess = true)
        val response = """{"status":"ok","results":[{"id":"a","score":0.95,"recordings":[
            {"title":"Song","artists":[{"name":"Artist"}],"releasegroups":[{"title":"Album","type":"Album"}]}
        ]}]}"""
        val result = analyze(response, initial)
        assertEquals(12, result.nbPlayed)
        assertTrue(result.isInQuickAccess)
    }

    @Test
    fun `position is dropped when the album changes`() {
        val initial = metadata(name = "Song", artists = listOf("Artist"), album = "Wrong Album", albumPosition = 4)
        val response = """{"status":"ok","results":[{"id":"a","score":0.98,"recordings":[
            {"title":"Song","artists":[{"name":"Artist"}],"releasegroups":[{"title":"Right Album","type":"Album"}]}
        ]}]}"""
        assertNull(analyze(response, initial).albumPosition)
    }

    @Test
    fun `worst secondary type is used for the preference`() {
        val liveCompilation = AcoustidReleaseGroup(title = "X", type = "Album", secondaryTypes = listOf("Compilation", "Live"))
        val live = AcoustidReleaseGroup(title = "X", type = "Album", secondaryTypes = listOf("Live"))
        assertTrue(liveCompilation.preference() < live.preference())
    }

    @Test
    fun `text similarity ignores case, accents and decorations`() {
        assertEquals(1f, TextSimilarity.similarity("Beyoncé", "beyonce"))
        assertEquals(1f, TextSimilarity.similarity("Album (Deluxe Edition)", "album"))
        assertEquals(1f, TextSimilarity.similarity("Song feat. Someone", "Song"))
        assertEquals(0f, TextSimilarity.similarity("", ""))
    }
}

package com.github.enteraname74.cloudy.controller.routingmessages.impl

import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import java.util.UUID

object FrRoutingMessages: RoutingMessages {
    override val USERNAME_TAKEN: String = "Ce nom d'utilisateur est déjà pris."
    override val WRONG_INFORMATION: String = "Les informations fournis sont incorrectes."
    override val MISSING_USER_INFORMATION: String = "Il manque le nom d'utilisateur ou le mot de passe."
    override val CANNOT_CREATE_USER: String = "Impossible de créer le profil utilisateur."
    override val CANNOT_FIND_USER: String = "Impossible de trouver l'utilisateur."
    override val NOT_AN_ADMIN: String = "L'utilisateur n'est pas un administrateur."
    override val INVALID_INSCRIPTION_CODE: String = "Le code d'inscription fournis est invalide."
    override val MISSING_PERMISSION_FOR_DELETION: String = "L'utilisateur n'a pas les droits pour supprimer ce profil."
    override val USER_DELETED: String = "Le profil utilisateur a été supprimé."

    override val NO_FILE_DATA: String = "Le fichier est vide."
    override val USER_MAX_STORAGE_REACHED: String = "L'utilisateur a atteint se limite de stockage disponible."
    override val FILE_TOO_HEAVY: String = "Le fichier est trop gros."
    override val FILE_NOT_FOUND: String = "Impossible de trouver le fichier de musique."

    override val SONGS_DELETED: String = "Les musiques ont été supprimées."
    override val SONG_NOT_POSSESSED_BY_USER: String = "Cette musique n'est pas possédée par l'utilisateur."

    override fun songNotPossessedByUser(musicId: UUID): String =
        "La musique ayant l'identifiant : $musicId, n'est pas possédée par l'utilisateur."

    override val ARTISTS_DELETED: String = "Les artistes ont été supprimés."
    override val ARTIST_NOT_POSSESSED_BY_USER: String = "Cet artiste n'est pas possédé par l'utilisateur."

    override fun artistNotPossessedByUser(artistId: UUID): String =
        "L'artiste ayant l'identifiant : $artistId, n'est pas possédé par l'utilisateur."

    override val ALBUMS_DELETED: String = "Les albums ont été supprimés."
    override val ALBUM_NOT_POSSESSED_BY_USER: String = "Cet album n'est pas possédé par l'utilisateur."

    override fun albumNotPossessedByUser(albumId: UUID): String =
        "L'album ayant l'identifiant : $albumId, n'est pas possédé par l'utilisateur."

    override val PLAYLIST_NOT_FOUND: String = "Impossible de trouver cette playlist."
    override val PLAYLISTS_DELETED: String = "Les playlists ont été supprimées."
    override val PLAYLIST_NOT_POSSESSED_BY_USER: String = "Cette playlist n'est pas possédée par l'utilisateur."
    override val PLAYLIST_ALREADY_EXISTING: String = "Cette playlist existe déja."

    override fun playlistNotPossessedByUser(playlistId: UUID): String =
        "La playlist ayant l'identifiant : $playlistId, n'est pas possédée par l'utilisateur."

    override val MISSING_TOKEN_INFORMATION: String = "Il manque des informations dans le token fournis."
    override val NOT_A_REFRESH_TOKEN: String = "Le token fournis n'est pas un token de rafraîchissement."

    override val WRONG_ID: String = "L'identifiant fournis est incorrect."
    override val WRONG_BODY_DATA: String = "Les données fournis sont incorrectes."
}
package com.github.enteraname74.cloudy.domain.routingmessages.impl

import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import kotlin.uuid.Uuid

object FrRoutingMessages: RoutingMessages {
    override val USERNAME_TAKEN: String = "Ce nom d'utilisateur est déjà pris."
    override val WRONG_INFORMATION: String = "Les informations fournies sont incorrectes."
    override val INVALID_INFORMATION: String = "Les informations fournies ne sont pas valables."
    override val MISSING_USER_INFORMATION: String = "Il manque le nom d'utilisateur ou le mot de passe."
    override val CANNOT_CREATE_USER: String = "Impossible de créer le profil utilisateur."
    override val CANNOT_FIND_USER: String = "Impossible de trouver l'utilisateur."
    override val NOT_AN_ADMIN: String = "L'utilisateur n'est pas un administrateur."
    override val INVALID_INSCRIPTION_CODE: String = "Le code d'inscription fournis est invalide."
    override val MISSING_PERMISSION_FOR_DELETION: String = "L'utilisateur n'a pas les droits pour supprimer ce profil."
    override val USER_DELETED: String = "Le profil utilisateur a été supprimé."
    override val INSCRIPTION_CODE_NOT_FOUND: String = "Le code d'inscription n'existe pas."

    override val NO_FILE_DATA: String = "Le fichier est vide."
    override val USER_MAX_STORAGE_REACHED: String = "L'utilisateur a atteint se limite de stockage disponible."
    override val FILE_TOO_HEAVY: String = "Le fichier est trop gros."
    override val FILE_NOT_FOUND: String = "Impossible de trouver le fichier de musique."
    override val IMAGE_NOT_FOUND: String = "Impossible de trouver l'image demandée."

    override val SONGS_DELETED: String = "Les musiques ont été supprimées."
    override val GIVEN_FILE_IS_NOT_A_MUSIC_FILE: String = "Le fichier fourni n'est pas une musique."
    override val SONG_NOT_POSSESSED_BY_USER: String = "Cette musique n'est pas possédée par l'utilisateur."
    override val CANNOT_SAVE_SONG: String = "Impossible de sauvegarder la musique."

    override fun cannotUpdateSong(songId: String): String =
        "Impossible de mettre à jour la musique avec l'id : $songId"

    override fun songNotPossessedByUser(musicId: String): String =
        "La musique ayant l'identifiant : $musicId, n'est pas possédée par l'utilisateur."

    override val ARTISTS_DELETED: String = "Les artistes ont été supprimés."
    override val ARTIST_NOT_POSSESSED_BY_USER: String = "Cet artiste n'est pas possédé par l'utilisateur."

    override fun artistNotPossessedByUser(artistId: Uuid): String =
        "L'artiste ayant l'identifiant : $artistId, n'est pas possédé par l'utilisateur."

    override val ALBUMS_DELETED: String = "Les albums ont été supprimés."
    override val ALBUM_NOT_POSSESSED_BY_USER: String = "Cet album n'est pas possédé par l'utilisateur."

    override fun albumNotPossessedByUser(albumId: Uuid): String =
        "L'album ayant l'identifiant : $albumId, n'est pas possédé par l'utilisateur."

    override val PLAYLIST_NOT_FOUND: String = "Impossible de trouver cette playlist."
    override val PLAYLISTS_DELETED: String = "Les playlists ont été supprimées."
    override val PLAYLIST_NOT_POSSESSED_BY_USER: String = "Cette playlist n'est pas possédée par l'utilisateur."
    override val PLAYLIST_ALREADY_EXISTING: String = "Cette playlist existe déja."
    override val CANNOT_SAVE_PLAYLIST: String = "Impossible de sauvegarder la playlist."

    override fun playlistNotPossessedByUser(playlistId: Uuid): String =
        "La playlist ayant l'identifiant : $playlistId, n'est pas possédée par l'utilisateur."

    override val MISSING_TOKEN_INFORMATION: String = "Il manque des informations dans le token fournis."
    override val NOT_A_REFRESH_TOKEN: String = "Le token fournis n'est pas un token de rafraîchissement."

    override val WRONG_ID: String = "L'identifiant fournis est incorrect."
    override val WRONG_BODY_DATA: String = "Les données fournis sont incorrectes."

    override val USER_ALREADY_IN_PLAYED_LIST: String = "L'utilisateur est déjà dans la liste de lecture."
    override val PLAYED_LIST_NOT_FOUND: String = "La liste de lecture n'existe pas."
    override val PLAYED_LIST_NOT_FOUND_OR_NOT_IN_LIST: String = "La liste de lecture n'existe pas ou l'utilisateur n'est pas dans la liste."
    override val NOT_OWNER_OF_PLAYED_LIST: String = "L'utilisateur n'est pas propriétaire de la liste."
    override val NO_PERMISSION_TO_REMOVE_USER_IN_PLAYED_LIST: String = "L'utilisateur ne peut pas supprimer quelqu'un d'autre de la liste."
    override val MISING_DEVICE_ID: String = "L'identifiant d'appareil est absent"

    override fun internalServerError(error: String): String =
        """
            Une erreur interne est survenue.
            Cause : $error
        """.trimIndent()
}
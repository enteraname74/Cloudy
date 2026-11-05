#!/usr/bin/env python3
"""Upload every song of a folder to a Cloudy server, without Soul Searching.

Usage:
    python cloudy_upload.py --server https://cloudy.example.com --user bob "D:\\Music"

The password is asked interactively, or read from the CLOUDY_PASSWORD environment variable.
"""

import argparse
import getpass
import json
import os
import sys
from pathlib import Path

import mutagen
import requests
from mutagen.flac import FLAC
from mutagen.id3 import ID3
from mutagen.mp4 import MP4

# Extensions mapped to MIME types accepted by the server (see CommonFileUtils.isMusicFile).
MUSIC_MIME_TYPES = {
    ".mp3": "audio/mpeg",
    ".m4a": "audio/mp4",
    ".mp4": "audio/mp4",
    ".aac": "audio/aac",
    ".flac": "audio/flac",
    ".ogg": "audio/ogg",
    ".opus": "audio/opus",
    ".wav": "audio/wav",
    ".wma": "audio/x-ms-wma",
    ".aif": "audio/aiff",
    ".aiff": "audio/aiff",
    ".webm": "audio/webm",
    ".amr": "audio/amr",
    ".3gp": "audio/3gpp",
}

# Cover files looked for next to a song when it has no embedded cover.
FOLDER_COVER_NAMES = ["cover", "folder", "front", "album"]
IMAGE_MIME_TYPES = {
    ".jpg": "image/jpeg",
    ".jpeg": "image/jpeg",
    ".png": "image/png",
    ".webp": "image/webp",
}

UNKNOWN = "Unknown"


def log_in(server: str, username: str, password: str) -> str:
    try:
        response = requests.post(
            f"{server}/auth/logIn",
            json={"username": username, "password": password},
            timeout=30,
        )
    except requests.RequestException as error:
        sys.exit(f"Cannot reach the server at {server} ({error})")
    if response.status_code != 200:
        sys.exit(f"Login failed ({response.status_code}): {response.text}")
    return response.json()["tokens"]["accessToken"]


def find_music_files(folder: Path) -> list[Path]:
    return sorted(
        path for path in folder.rglob("*")
        if path.is_file() and path.suffix.lower() in MUSIC_MIME_TYPES
    )


def first_tag(tags: dict, key: str) -> str | None:
    values = tags.get(key)
    if not values:
        return None
    value = str(values[0]).strip()
    return value or None


def parse_track_number(value: str | None) -> int | None:
    """Handles '3' as well as '3/12'."""
    if not value:
        return None
    try:
        return int(value.split("/")[0])
    except ValueError:
        return None


def read_embedded_cover(path: Path) -> tuple[bytes, str] | None:
    try:
        suffix = path.suffix.lower()
        if suffix == ".mp3":
            for frame in ID3(path).getall("APIC"):
                return frame.data, frame.mime or "image/jpeg"
        elif suffix == ".flac":
            for picture in FLAC(path).pictures:
                return picture.data, picture.mime or "image/jpeg"
        elif suffix in (".m4a", ".mp4"):
            for cover in MP4(path).tags.get("covr", []):
                mime = "image/png" if cover.imageformat == cover.FORMAT_PNG else "image/jpeg"
                return bytes(cover), mime
    except Exception:
        pass
    return None


def read_folder_cover(path: Path) -> tuple[bytes, str] | None:
    for file in path.parent.iterdir():
        if file.stem.lower() in FOLDER_COVER_NAMES and file.suffix.lower() in IMAGE_MIME_TYPES:
            return file.read_bytes(), IMAGE_MIME_TYPES[file.suffix.lower()]
    return None


def build_upload_spec(path: Path) -> dict:
    """Builds the MusicUploadSpec expected by the server from the file tags."""
    audio = mutagen.File(path, easy=True)
    tags = audio.tags if audio is not None and audio.tags is not None else {}
    duration_ms = int(audio.info.length * 1000) if audio is not None and audio.info else 0

    name = first_tag(tags, "title") or path.stem
    artist_names = [str(artist).strip() for artist in tags.get("artist", []) if str(artist).strip()]
    if not artist_names:
        artist_names = [UNKNOWN]
    album_artist_name = first_tag(tags, "albumartist") or artist_names[0]
    album_name = first_tag(tags, "album") or path.parent.name or UNKNOWN

    def artist_upload(artist_name: str) -> dict:
        return {"name": artist_name, "nbPlayed": 0, "isInQuickAccess": False}

    return {
        "name": name,
        "albumUpload": {
            "name": album_name,
            "nbPlayed": 0,
            "isInQuickAccess": False,
            "artistUpload": artist_upload(album_artist_name),
        },
        "artists": [artist_upload(artist_name) for artist_name in artist_names],
        "albumPosition": parse_track_number(first_tag(tags, "tracknumber")),
        "duration": duration_ms,
        "nbPlayed": 0,
        "isInQuickAccess": False,
    }


def upload_music(server: str, token: str, path: Path, search_metadata: bool) -> requests.Response:
    spec = build_upload_spec(path)
    cover = read_embedded_cover(path) or read_folder_cover(path)

    with path.open("rb") as music_file:
        # The server takes the first music part as the song and the first image part as its cover.
        files = [
            ("metadata", (None, json.dumps(spec), "application/json")),
            ("music", (path.name, music_file, MUSIC_MIME_TYPES[path.suffix.lower()])),
        ]
        if cover is not None:
            cover_data, cover_mime = cover
            cover_extension = "png" if cover_mime == "image/png" else "jpg"
            files.append(("cover", (f"cover.{cover_extension}", cover_data, cover_mime)))

        return requests.post(
            f"{server}/music/upload",
            params={"searchMetadata": str(search_metadata).lower()},
            headers={"Authorization": f"Bearer {token}"},
            files=files,
            timeout=600,
        )


def main() -> None:
    parser = argparse.ArgumentParser(description="Upload the songs of a folder to a Cloudy server.")
    parser.add_argument("folder", type=Path, help="Folder containing the songs (scanned recursively)")
    parser.add_argument("--server", required=True, help="Cloudy server URL, e.g. https://cloudy.example.com")
    parser.add_argument("--user", required=True, help="Cloudy username")
    parser.add_argument(
        "--no-search-metadata",
        action="store_true",
        help="Only use the file tags, don't let the server look songs up online",
    )
    parser.add_argument("--dry-run", action="store_true", help="Show what would be uploaded, without uploading")
    args = parser.parse_args()

    folder: Path = args.folder.expanduser()
    if not folder.is_dir():
        sys.exit(f"Not a folder: {folder}")

    music_files = find_music_files(folder)
    if not music_files:
        sys.exit(f"No song found in {folder}")
    print(f"Found {len(music_files)} song(s) in {folder}")

    if args.dry_run:
        for path in music_files:
            spec = build_upload_spec(path)
            artists = ", ".join(artist["name"] for artist in spec["artists"])
            print(f"  {path.name}: {spec['name']} - {artists} [{spec['albumUpload']['name']}]")
        return

    server: str = args.server.rstrip("/")
    password = os.environ.get("CLOUDY_PASSWORD") or getpass.getpass(f"Password for {args.user}: ")
    token = log_in(server, args.user, password)

    uploaded, failed = 0, []
    for index, path in enumerate(music_files, start=1):
        prefix = f"[{index}/{len(music_files)}] {path.relative_to(folder)}"
        try:
            response = upload_music(server, token, path, search_metadata=not args.no_search_metadata)
        except requests.RequestException as error:
            print(f"{prefix}: network error ({error})")
            failed.append(path)
            continue

        if response.status_code == 200:
            uploaded += 1
            print(f"{prefix}: uploaded")
        elif response.status_code in (403, 413):
            print(f"{prefix}: storage full, stopping ({response.text})")
            failed.extend(music_files[index - 1:])
            break
        elif response.status_code == 401:
            sys.exit(f"{prefix}: session expired or invalid, please run the script again")
        else:
            print(f"{prefix}: failed ({response.status_code}: {response.text})")
            failed.append(path)

    print(f"\nDone: {uploaded} uploaded, {len(failed)} failed")
    if failed:
        print("Failed songs:")
        for path in failed:
            print(f"  {path}")
        sys.exit(1)


if __name__ == "__main__":
    main()

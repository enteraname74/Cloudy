# Releases notes
## 0.3.0
This release is compatible with Soul Searching releases starting from **v0.17.0**.

### Features
- Add an import folder: songs put in `import/<username>/` on the server are imported automatically for that user
  (useful for drag and drop, or for another service downloading songs to the server). See the **Import folder** section of the README.
  - Enabled by default with the docker setup, set `IMPORT_FOLDER_PATH=` (empty) in your `.env` file to disable it
  - Set `IMPORT_FOLDER_SEARCH_METADATA=true` to search the metadata of imported songs on remote sources
  - A folder is created for each user, and deleted with the user
- Improve the search of music metadata on remote sources (Acoustid) on upload: better matching of results, covers retrieved from the Cover Art Archive, requests rate limited
- Add a Python script to upload every song of a folder to Cloudy, without Soul Searching (`scripts/uploader`)
- Add a Swagger page (`/swagger`), enabled with `SWAGGER=true`

### Docker
- Update your `compose.yaml` file to use the import folder: it adds the `import-init` service (preparing the import folder at each start), the `./import` mount and the `group_add` of the `ktor` service
- If your user on the host is not the first one (`uid=1000`, `gid=1000` with the `id` command), set `PUID` and `PGID` in your `.env` file

## 0.2.0
This release is compatible with Soul Searching releases starting from **v0.17.0**.

### Features
- Add Soul Searching settings support
- Add Soul Searching collection support
- Add Soul Searching Grid cover support

### Fixes
- Fix `nbPlayed`, artist's `name`, `isInQuickAccess` and `albumPosition` fields not correctly updated
- Fix artists not sent back to music metadata on music upload/update

## 0.1.0
This release is compatible with **v0.16.0** of Soul Searching.
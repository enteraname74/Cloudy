# Cloudy

Cloud server for the multiplatform music player application [***Soul Searching***](https://github.com/enteraname74/SoulSearching).

## Features
- save your *Soul Searching* data (songs, playlists, statistics,...) remotely
- support for multiple users (optimized for a group of friends, a family) with inscription codes
- shared played list between multiple users (like Spotify)

> Check the matching Cloudy releases with [Soul Searching](https://github.com/enteraname74/SoulSearching) ones in the [releases notes](releases_notes.md) file in this repository

## Set up the server

> The app works best with docker and docker compose. Be sure to have these installed on your system.

You need to provide a `.env` file with all the environment variable that the server needs.
With this, you can customize the settings of the server.

You will find a `.env.template` file with all the environment variables that the server needs.
You can use this file as a template to create your `.env` file.
> For the database setup, Cloudy supports SQLite and PostgreSQL (see DB_FLAVOR env var). I recommend using PostgreSQL with the docker setup.

You can use the existing `compose.yaml` file of this project.
> For development purpose, the build section of the ktor service may be used.
> Uncomment the image section of the ktor service and comment the build section to use an official release of cloudy, 
> thus skipping the need to clone this repository in your system.
> See [GitHub packages](https://github.com/users/enteraname74/packages/container/package/cloudy) for the latest image.

### HTTPS support
Cloudy can be deployed on a VPS with HTTPS support using [Traefik](https://traefik.io/traefik).
To make it work properly, you will need to add a `dynamic.yml` file in a `traefik` folder, at the root of your setup folder (containing compose file,...).
This will contain some setup for the backend service.
Template of a `dynamic.yml` file:
```
http:
  routers:
    api:
      # Here, replace with your domain name
      rule: "Host(`my.domain.com`)"
      entryPoints:
        - websecure
      service: api-service
      tls:
        certResolver: letsencrypt

  services:
    api-service:
      loadBalancer:
        servers:
          - url: "http://ktor:8080"
```

### Import folder
Cloudy can watch a folder and import every song put in it, without going through the API
(useful for drag and drop, or for another service downloading songs directly to the server).

Set the `IMPORT_FOLDER_PATH` env variable to enable it. With the docker setup, use `/cloudy_import`:
it is mounted from the `import` folder next to your `compose.yaml` file.

- Put songs in `import/<username>/`. A folder is created when a user is created (and at startup for existing users).
- Folders matching no user are deleted with their content, and the folder of a user is deleted when the user is deleted.
- Songs are processed like an upload (fingerprint, artists, album...), using the metadata of the file.
- Every processed file is removed from the import folder, **including files that can't be imported** (unsupported file, storage full...): check the server logs to know why a file was not imported.
- A song is only imported once it hasn't changed for a few seconds, so files still being downloaded are left alone. Files ending with `.part`, `.tmp`, `.crdownload`... and hidden files are ignored.

> **Permissions (docker)**: the server runs as the `cloudy` user of the container, which is not your user on the host.
> - Create the `import` folder yourself and make it writable by everyone, before starting the server:
>   `mkdir -p import && chmod 777 import`. Otherwise, docker creates it as `root` and the server can't use it.
> - User folders created by the server are writable by everyone, so that you (or another service) can put songs in them.
> - Songs must be readable by everyone (the default for most tools). Unreadable songs are kept and logged until their permissions are fixed.

## Development mode
### Launch locally without docker
You can launch the app locally using a premade bash script. Be sure to make the bash script executable on your device:
```shell
chmod +x cloudy.sh
```
Then, launch the project using the script. You will need to pass a path to your env file:
```shell
# Here, the .env file is located at the same place as the script.
./cloudy.sh .env
```

### Launch locally with docker

Build the jar of the ktor backend
```
./gradlew controller:buildFatJar
```

Ensure that the ktor service in `compose.yaml` is built from the `Dockerfile` of the project.

Launch the server with docker compose and ensure that the image is always up to date
```
docker compose build --no-cache --pull ktor && docker compose up
```

## Additional resources
### Send songs to Cloudy without Soul Searching
Some pre-made scripts are available in case you cannot use Soul Searching to send songs to Cloudy, 
or if you don't want to use the API directly.
You will find Python scripts in `scripts` folder. 
The `cloudy_uploader.py` script lets you upload songs from a selected folder to the cloud. Here are recommends steps to use it, 
in a terminal (linux):
```bash
# Move to a python virtual environment setup
python3 -m venv .venv

source .venv/bin/activate

# Install the dependencies
python -m pip install -r requirements.txt

# Launch the uploader. The script will ask you for your cloudy password
python cloudy_upload.py --server https://your-server --user your-username folder-to-upload
```

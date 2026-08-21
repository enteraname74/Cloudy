# Cloudy

Cloud server for the multiplatform music player application [***Soul Searching***](https://github.com/enteraname74/SoulSearching).

## Features
- save your songs and playlists remotely
- support for multiple users (optimized for a group of friends, a family) with inscription codes
- shared played list between multiple users (like Spotify)

## Set up the server

> The app works best with docker and docker compose. Be sure to have these installed on your system.

You need to provide a `.env` file with all the environment variable that the server needs.
With this, you can customize the settings of the server.

You will find a `.env.template` file with all the environment variables that the server needs.
You can use this file as a template to create your `.env` file.
> For the database setup, Cloudy supports SQLite and PostgreSQL. I recommend using PostgreSQL with the docker setup.

You can use the existing `compose.yaml` file of this project.
> For development purpose, the build section of the ktor service may be used.
> Uncomment the image section of the ktor service and comment the build section to use an official release of cloudy, 
> thus skipping the need to clone this repository in your system.
> See [GitHub packages](https://github.com/users/enteraname74/packages/container/package/cloudy) for the latest image.

Cloudy can be deployed on a VPS with HTTPS support using [Traefik](https://traefik.io/traefik).
To make it work properly, you will need to add a `dynamic.yml` file in a `traefik` folder. This will contain some setup for the backend service.
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

## Development mode
### Launch locally without docker
You can launch the app locally using a premade bash script. Be sure to make the bash script executable on your device:
```shell
chmod +x launch_backend.sh
```
Then, launch the project using the script. You will need to pass a path to your env file:
```shell
# Here, the .env file is located at the same place as the script.
./launch_backend.sh .env
```

### Launch locally with docker

Build the jar of the ktor backend
```
./gradlew controller:buildFatJar
```

Ensure that the ktor service in `compose.yaml` is built from the `Dockerfile` of the project.

Launch the server with docker compose and ensure that the image is always up to date
```
docker compose build --no-cache --pull ktor && docker compose up -d
```

# Cloudy

Cloud server for the multiplatform music player application [***Soul Searching***](https://github.com/enteraname74/SoulSearching).

## Set up the server

You need to provide a `.env` file with all the environment variable that the server needs.
With this, you can customize the settings of the server.

You will find a `.env.template` file with all the environment variables that the server needs.
You can use this file as a template to create your `.env` file.

## Launch the server
### With Docker

Build the jar of the ktor backend
```
./gradlew controller:buildFatJar
```

Launch the server with docker compose and ensure that the image is always up to date
```
docker compose build --no-cache --pull ktor && docker compose up -d
```

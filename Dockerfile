FROM amazoncorretto:21-alpine-jdk

RUN apk add --no-cache chromaprint

RUN addgroup -S cloudy && adduser -S cloudy -G cloudy

RUN mkdir -p /cloudy_data /cloudy_import \
 && chown -R cloudy:cloudy /cloudy_data /cloudy_import

WORKDIR /app

ARG JAR_FILE=./controller/build/libs/*.jar
COPY --chown=cloudy:cloudy ${JAR_FILE} application.jar

USER cloudy
# Files and folders created by the server are writable by their group (needed to share the import folder).
ENTRYPOINT ["sh", "-c", "umask 0002 && exec java -jar application.jar"]
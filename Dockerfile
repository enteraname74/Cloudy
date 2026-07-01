# Use Amazon Corretto 21 with Alpine as the base image
FROM amazoncorretto:21-alpine-jdk

# Install necessary packages (ffmpeg, chromaprint and curl)
RUN apk update && \
    apk add --no-cache \
    ffmpeg \
    chromaprint \
    curl \
    && rm -rf /var/cache/apk/*


RUN curl -L \
      https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_musllinux \
      -o /usr/local/bin/yt-dlp \
 && chmod +x /usr/local/bin/yt-dlp \
 && yt-dlp --version


# Copy the fat jar from the build output into the container
RUN addgroup -S cloudy && adduser -S cloudy -G cloudy

ARG JAR_FILE=./controller/build/libs/*.jar
COPY --chown=cloudy:cloudy ${JAR_FILE} application.jar

USER cloudy
ENTRYPOINT ["java", "-jar", "application.jar"]
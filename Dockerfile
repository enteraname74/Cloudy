FROM amazoncorretto:21-alpine-jdk

RUN apk add --no-cache ffmpeg chromaprint nodejs curl

RUN curl -L \
      https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_musllinux \
      -o /usr/local/bin/yt-dlp \
 && chmod +x /usr/local/bin/yt-dlp \
 && yt-dlp --version \
 && apk del curl

RUN addgroup -S cloudy && adduser -S cloudy -G cloudy

RUN mkdir -p /cloudy_data \
 && chown -R cloudy:cloudy /cloudy_data

WORKDIR /app

ARG JAR_FILE=./controller/build/libs/*.jar
COPY --chown=cloudy:cloudy ${JAR_FILE} application.jar

USER cloudy
ENTRYPOINT ["java", "-jar", "application.jar"]
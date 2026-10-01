FROM amazoncorretto:21-alpine-jdk

RUN apk add --no-cache chromaprint

RUN addgroup -S cloudy && adduser -S cloudy -G cloudy

RUN mkdir -p /cloudy_data \
 && chown -R cloudy:cloudy /cloudy_data

WORKDIR /app

ARG JAR_FILE=./controller/build/libs/*.jar
COPY --chown=cloudy:cloudy ${JAR_FILE} application.jar

USER cloudy
ENTRYPOINT ["java", "-jar", "application.jar"]
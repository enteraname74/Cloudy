# Use Amazon Corretto 17 with Alpine as the base image
FROM amazoncorretto:17-alpine-jdk

# Install necessary packages (ffmpeg and chromaprint)
RUN apk update && \
    apk add --no-cache \
    ffmpeg \
    chromaprint \
    && rm -rf /var/cache/apk/*

# Create a volume for music files
VOLUME /app/songs

# Copy the fat jar from the build output into the container
ARG JAR_FILE=./controller/build/libs/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
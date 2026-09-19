# Build the Micronaut fat jar and ship it on a slim JRE.
#
# The image is built in two stages so no JDK/Gradle is needed on the host:
#   docker build -t squidcode-backend services/backend-service/application
#
# `-Dorg.gradle.jvmargs` keeps the Gradle JVM small on memory constrained
# builders (the CI runner and the 2 vCPU VPS both choke on the 4G default of
# gradle.properties).
FROM eclipse-temurin:21-jdk AS build

WORKDIR /application
COPY . .
RUN ./gradlew --no-daemon -Dorg.gradle.jvmargs=-Xmx1536m shadowJar

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=build /application/build/libs/*-all.jar /app/application.jar

ENV PORT=8000
EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/app/application.jar"]

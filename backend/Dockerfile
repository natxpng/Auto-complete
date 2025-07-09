# Etapa 1: build
FROM gradle:8.4.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Etapa 2: runtime
FROM openjdk:17
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

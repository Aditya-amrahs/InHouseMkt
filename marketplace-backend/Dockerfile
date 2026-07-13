# Use a pre-installed Maven image to build your app
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy your configuration and source code
COPY pom.xml .
COPY src ./src

# Build using the globally installed 'mvn' command instead of './mvnw'
RUN mvn clean package -DskipTests

# Standard lightweight runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
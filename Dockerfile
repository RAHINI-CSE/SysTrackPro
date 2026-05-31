# --- Stage 1: Compile and Package the Java Application ---
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app

# Copy pom.xml and source code into the build container
COPY pom.xml .
COPY src ./src

# Compile and package the application cleanly
RUN mvn clean package -DskipTests

# --- Stage 2: Create the lightweight execution image ---
FROM eclipse-temurin:23-jdk-jammy
WORKDIR /app

# Copy the generated JAR file from the build stage container
COPY --from=build /app/target/SysTrackPro-0.0.1-SNAPSHOT.jar systrackpro-app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "systrackpro-app.jar"]
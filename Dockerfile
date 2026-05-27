FROM eclipse-temurin:23-jdk-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the Maven target directory into the container
COPY target/SysTrackPro-0.0.1-SNAPSHOT.jar systrackpro-app.jar

# Expose the internal port your Spring Boot application listens on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "systrackpro-app.jar"]
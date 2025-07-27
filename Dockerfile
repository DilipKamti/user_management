# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine
VOLUME /tmp
# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file into the container
COPY target/user-management*.jar app.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
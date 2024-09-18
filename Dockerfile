# Stage 1: Build the application using Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /home/app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the project files into the container
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Use a lightweight image for running the app
FROM eclipse-temurin:21-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /home/app/target/*.jar /app/qlock-in.jar

# Expose the port your Spring Boot app will run on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "/app/qlock-in.jar"]
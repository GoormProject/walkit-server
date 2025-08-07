FROM eclipse-temurin:17-jdk-alpine
COPY ./build/libs/*SNAPSHOT.jar project.jar
COPY src/main/resources/application*.yml /app/
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "-Dspring.config.location=file:/app/", "/project.jar"]

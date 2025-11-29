FROM eclipse-temurin:21-jdk-alpine AS build
COPY /target/*.jar market-app.jar
RUN mkdir -p /app/images
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "market-app.jar"]
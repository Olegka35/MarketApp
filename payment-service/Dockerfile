FROM eclipse-temurin:21-jdk-alpine AS build
COPY /target/*.jar payment-service.jar
RUN mkdir -p /app/images
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "payment-service.jar"]
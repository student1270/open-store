
FROM eclipse-temurin:21-jdk-alpine


WORKDIR /app


COPY target/open-store-0.0.1-SNAPSHOT.jar app.jar


RUN mkdir -p images


EXPOSE 8083


ENTRYPOINT ["java", "-jar", "app.jar"]

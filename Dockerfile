# 1. Java 21 uchun official Eclipse Temurin Alpine image
FROM eclipse-temurin:21-jdk-alpine

# 2. Konteynerdagi ishchi katalogni belgilaymiz
WORKDIR /app

# 3. Loyihadagi JAR faylni konteynerga nusxalaymiz
COPY target/open-store-0.0.1-SNAPSHOT.jar app.jar

# 4. Rasmlar uchun katalog (mahsulot rasmlari shu yerda bo‘ladi)
RUN mkdir -p images

# 5. Ilova Spring Boot orqali ishlaydi, port 8083 (application.yml ga mos)
EXPOSE 8083

# 6. JAR faylni ishga tushiramiz
ENTRYPOINT ["java", "-jar", "app.jar"]

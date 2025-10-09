# Stage 1: Build
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/holify-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/serviceAccountKey.json /app/serviceAccountKey.json
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/your_db_name
ENV SPRING_DATASOURCE_USERNAME=your_db_user
ENV SPRING_DATASOURCE_PASSWORD=your_db_password
ENV SENDGRID_API_KEY=SG.your-sendgrid-api-key
ENV APP_EMAIL_FROM=otp@yourdomain.com
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
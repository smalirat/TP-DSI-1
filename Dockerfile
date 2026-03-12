# Etapa 1: build
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: runtime
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /app/target/metamapa-web-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

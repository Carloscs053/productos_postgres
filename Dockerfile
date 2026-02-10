# Compilación (build)
FROM maven:3.9.5-eclipse-temurin-21 AS build
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Ejecución
FROM eclipse-temurin:21-jdk-jammy
COPY --from=build /target/*.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]
# Etapa 1: build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos el proto compartido donde el pom.xml lo espera
COPY proto/ ./proto/

# Copiamos el proyecto
COPY presence-service/pom.xml ./presence-service/
WORKDIR /app/presence-service
RUN mvn dependency:go-offline -B
COPY presence-service/src ./src
RUN mvn clean package -DskipTests -B

# Etapa 2: runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build /app/presence-service/target/*.jar app.jar
EXPOSE 8081
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
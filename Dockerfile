# ===== Etapa 1: build da aplicação com Maven =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copia primeiro o pom.xml para aproveitar o cache de dependências
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copia o código-fonte e gera o .jar
COPY src ./src
RUN mvn -B clean package -DskipTests

# ===== Etapa 2: imagem final, somente com o runtime =====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

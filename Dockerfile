# Etapa de construcción
FROM maven:3.9.8-eclipse-temurin-17-alpine AS builder

# Establecemos directorio de trabajo
WORKDIR /app

# Copiamos archivos de proyecto
COPY pom.xml .
COPY src ./src

# Descargamos dependencias y construir el proyecto
RUN mvn clean package -DskipTests

# Etapa de ejecución usamos la version de java correspondiente
FROM openjdk:17-jdk-slim

# Metadatos
LABEL maintainer="tu-equipo@grupobios.cl"
LABEL version="1.0"
LABEL description="API Ficha Técnica Pacientes - GrupoBios"

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=America/Santiago
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Creamos un usuario no-root para seguridad
RUN groupadd -r spring && useradd -r -g spring spring

# Directorio de trabajo
WORKDIR /app

# Copiamos todo el JAR desde la etapa de construcción
COPY --from=builder /app/target/*.jar app.jar

# Cambiamos los permisos
RUN chown -R spring:spring /app
USER spring

# Exponer puerto
EXPOSE 8080

# Salud check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando de ejecución
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
# Multi-stage build para optimizar el tamaño de la imagen

# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copiar archivos de Maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Copiar archivos de configuración de calidad de código
COPY checkstyle-suppressions.xml ./
COPY spotbugs-exclude.xml ./

# Copiar código fuente
COPY src ./src

# Dar permisos de ejecución al wrapper de Maven
RUN chmod +x ./mvnw

# Construir la aplicación (sin tests para acelerar)
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Crear directorio para configuración
RUN mkdir -p /app/config

# Copiar el JAR desde el stage de build
COPY --from=builder /app/target/*.jar app.jar

# Copiar archivo de configuración Docker y darle permisos
COPY application-docker.properties /app/config/application-docker.properties
RUN chmod 644 /app/config/application-docker.properties

# Copiar entrypoint script y darle permisos de ejecución
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Cambiar permisos de los archivos al usuario spring
RUN chown -R spring:spring /app

# Cambiar a usuario no-root
USER spring:spring

# Exponer el puerto de la aplicación
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Configurar JVM para contenedores
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Ejecutar la aplicación usando el entrypoint script
ENTRYPOINT ["/app/entrypoint.sh"]

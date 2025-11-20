#!/bin/sh


# Ejecutar Spring Boot con configuración personalizada
exec java $JAVA_OPTS -jar /app/app.jar --spring.config.location=file:/app/config/application-docker.properties
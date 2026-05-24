# =========================
# ETAPA 1 - BUILD
# =========================
FROM openjdk:17 AS build

# Instalar Ant
RUN apt-get update && apt-get install -y ant

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivos del proyecto
COPY . .

# Compilar proyecto con Ant
RUN ant clean jar

# =========================
# ETAPA 2 - RUNTIME
# =========================
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/dist/*.jar app.jar

# Puerto de ejecución
EXPOSE 8080

# Ejecutar aplicación
CMD ["java", "-jar", "app.jar"]

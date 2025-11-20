# 🚀 Guía de Inicio Rápido

## ⚡ Inicio en 3 Pasos

### 1️⃣ Levantar Todo con Docker

```bash
docker-compose up --build -d
```

### 2️⃣ Verificar que Esté Corriendo

```bash
# Ver estado
docker-compose ps

# Ver logs
docker-compose logs -f payment-orders-app
```

### 3️⃣ Probar la API

Abre en tu navegador: **http://localhost:8080/swagger-ui.html**

O usa curl:
```bash
curl http://localhost:8080/actuator/health
```

---

## 📱 Servicios Disponibles

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **API REST** | http://localhost:8080 | - |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | - |
| **Health Check** | http://localhost:8080/actuator/health | - |
| **PgAdmin** | http://localhost:5050 | Email: `admin@paymentorders.com`<br>Pass: `admin` |
| **PostgreSQL** | localhost:5432 | User: `paymentuser`<br>Pass: `paymentpass`<br>DB: `paymentorders` |

---

## 🧪 Probar con Postman

1. **Importar colección**: `ai/payment-orders-api.postman_collection.json`
2. **Ejecutar requests en orden**:
   - `0. Health Check` → Verifica que la app esté corriendo
   - `1. Initiate Payment Order - USD` → Crea una orden (guarda ID automáticamente)
   - `3. Retrieve Payment Order` → Consulta la orden completa
   - `4. Retrieve Payment Order Status` → Consulta solo el estado
   - `5. Retrieve Non-Existent Order (404)` → Prueba manejo de errores
   - `6. Invalid Payment Order (400)` → Prueba validaciones

---

## 🛑 Detener Todo

```bash
# Detener servicios
docker-compose down

# Detener y limpiar base de datos
docker-compose down -v
```

---

## 🔧 Desarrollo Local (sin Docker para la app)

Si prefieres ejecutar la aplicación desde IntelliJ IDEA:

```bash
# 1. Solo levantar PostgreSQL
docker-compose up -d postgres

# 2. Ejecutar desde IntelliJ o con Maven
mvn spring-boot:run
```

---

## 📊 Comandos Útiles

```bash
# Ver logs en tiempo real
docker-compose logs -f payment-orders-app

# Reiniciar la aplicación
docker-compose restart payment-orders-app

# Reconstruir después de cambios en el código
docker-compose up --build -d payment-orders-app

# Conectar a PostgreSQL
docker exec -it payment-orders-db psql -U paymentuser -d paymentorders

# Ver todas las órdenes de pago
docker exec -it payment-orders-db psql -U paymentuser -d paymentorders -c "SELECT * FROM payment_orders;"
```

---

## ❓ Solución de Problemas

### La aplicación no inicia
```bash
# Ver logs detallados
docker-compose logs payment-orders-app

# Verificar que PostgreSQL esté healthy
docker-compose ps
```

### Puerto 8080 ocupado
```bash
# Cambiar puerto en docker-compose.yml
ports:
  - "8081:8080"  # Usar 8081 en lugar de 8080
```

### Base de datos corrupta
```bash
# Limpiar y reiniciar
docker-compose down -v
docker-compose up --build -d
```

### Reconstruir desde cero
```bash
# Paso 1: Detener y limpiar contenedores
docker-compose down -v
```

```bash
# Paso 2: Limpiar imágenes y caché (requiere confirmación Y/N)
docker system prune -a
```

```bash
# Paso 3: Volver a construir
docker-compose up --build -d
```

---

## 📖 Más Información

- **README completo**: [README.md](../README.md)
- **Documentación BIAN**: [ANALISIS_MIGRACION.md](ANALISIS_MIGRACION.md)
- **Colección Postman**: [../ai/payment-orders-api.postman_collection.json](../ai/payment-orders-api.postman_collection.json)
- **Reglas de Desarrollo**: [../ai/windsurf-rules.md](../ai/windsurf-rules.md)

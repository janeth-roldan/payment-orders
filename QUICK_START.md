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

1. **Importar colección**: `ai/postman_collection_bian.json`
2. **Ejecutar requests**:
   - `1. Initiate Payment Order` → Crea una orden
   - `2. Retrieve Payment Order` → Consulta la orden
   - `3. Retrieve Payment Order Status` → Consulta el estado

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
# Limpiar todo
docker-compose down -v
docker system prune -a

# Volver a construir
docker-compose up --build -d
```

---

## 📖 Más Información

- **README completo**: [README.md](README.md)
- **Documentación BIAN**: [ai/analisis_migracion.md](ai/analisis_migracion.md)
- **Colección Postman**: [ai/postman_collection_bian.json](ai/postman_collection_bian.json)

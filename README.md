# Payment Orders API - BIAN 12.0

API REST para gestión de órdenes de pago alineada con BIAN Service Landscape 12.0.

## 🏗️ Arquitectura

- **Patrón**: Hexagonal (Ports & Adapters)
- **Framework**: Spring Boot 3.5.7 + WebFlux (Reactive)
- **Base de Datos**: PostgreSQL con R2DBC
- **Especificación**: OpenAPI 3.0 (Contract-First)
- **Estándar**: BIAN 12.0 - Payment Initiation SD / PaymentOrder BQ

## 📋 Requisitos Previos

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Postman (opcional, para pruebas)

## 🚀 Inicio Rápido

### Opción 1: Todo con Docker Compose (Recomendado)

```bash
# Construir y levantar todos los servicios
docker-compose up --build -d

# Ver logs
docker-compose logs -f payment-orders-app
```

Esto iniciará:
- **PostgreSQL 16**: Puerto 5432
- **Spring Boot API**: Puerto 8080
- **PgAdmin 4**: Puerto 5050 (opcional)

### Opción 2: Solo PostgreSQL + Ejecutar App Localmente

```bash
# 1. Levantar solo PostgreSQL
docker-compose up -d postgres

# 2. Ejecutar aplicación
mvn spring-boot:run
```

### Opción 3: Script de Inicio Rápido (Windows)

```bash
# Ejecutar start.bat y elegir opción
start.bat

# Opción 1: Docker Compose completo
# Opción 2: Solo PostgreSQL + Maven
```

### Verificar Instalación

```bash
# Health check de la API
curl http://localhost:8080/actuator/health

# O abrir en navegador
http://localhost:8080/swagger-ui.html
```

## 📚 Documentación de la API

Una vez que la aplicación esté corriendo:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **Colección Postman**: `ai/postman_collection_bian.json`

## 🔌 Endpoints Disponibles

### 1. Iniciar Orden de Pago
```http
POST /payment-initiation/payment-orders
Content-Type: application/json

{
  "paymentOrderProcedure": {
    "paymentTransactionInitiatorReference": "EXT-2025-001",
    "payer": {
      "payerReference": "Juan Pérez",
      "payerBankReference": "BANK001",
      "payerProductInstanceReference": "EC1234567890123456789012"
    },
    "payee": {
      "payeeReference": "María López",
      "payeeBankReference": "BANK002",
      "payeeProductInstanceReference": "EC9876543210987654321098"
    },
    "paymentDetails": {
      "amount": 150.75,
      "currency": "USD",
      "paymentMechanismType": "CreditTransfer"
    },
    "dateInformation": {
      "dateType": "RequestedExecutionDate",
      "date": "2025-12-31"
    },
    "remittanceInformation": "Factura 001-123"
  }
}
```

### 2. Consultar Orden de Pago
```http
GET /payment-initiation/payment-orders/{id}
```

### 3. Consultar Estado de Orden
```http
GET /payment-initiation/payment-orders/{id}/status
```

## 🧪 Pruebas con Postman

1. Importar la colección: `ai/postman_collection_bian.json`
2. Ejecutar los requests en orden:
   - **1. Initiate Payment Order** (guarda el ID automáticamente)
   - **2. Retrieve Payment Order** (usa el ID guardado)
   - **3. Retrieve Payment Order Status** (usa el ID guardado)

## 🗄️ Base de Datos

### Conectar a PostgreSQL

```bash
# Desde línea de comandos
psql -h localhost -p 5432 -U paymentuser -d paymentorders

# Desde PgAdmin
# URL: http://localhost:5050
# Email: admin@paymentorders.com
# Password: admin
```

### Esquema de Base de Datos

La tabla `payment_orders` se crea automáticamente al iniciar la aplicación:

```sql
CREATE TABLE payment_orders (
    id UUID PRIMARY KEY,
    payment_transaction_initiator_reference VARCHAR(255) NOT NULL,
    payer_reference VARCHAR(255),
    payer_bank_reference VARCHAR(255),
    payer_product_instance_reference VARCHAR(255),
    payee_reference VARCHAR(255),
    payee_bank_reference VARCHAR(255),
    payee_product_instance_reference VARCHAR(255),
    amount DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_mechanism_type VARCHAR(50),
    date_type VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    remittance_information TEXT,
    status VARCHAR(50) NOT NULL,
    created_date_time TIMESTAMP NOT NULL,
    last_update_date_time TIMESTAMP NOT NULL
);
```

## 🛠️ Comandos Docker Útiles

```bash
# Iniciar todos los servicios
docker-compose up -d

# Iniciar con rebuild
docker-compose up --build -d

# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f payment-orders-app
docker-compose logs -f postgres

# Ver estado de los servicios
docker-compose ps

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes (limpia la BD)
docker-compose down -v

# Reiniciar un servicio específico
docker-compose restart payment-orders-app
docker-compose restart postgres

# Reconstruir solo la aplicación
docker-compose build payment-orders-app
docker-compose up -d payment-orders-app

# Ejecutar comandos dentro del contenedor
docker exec -it payment-orders-app sh
docker exec -it payment-orders-db psql -U paymentuser -d paymentorders
```

## 📊 Calidad de Código

### Ejecutar Análisis Completo
```bash
mvn verify
```

Esto ejecuta:
- ✅ Checkstyle (Google Java Style)
- ✅ SpotBugs (análisis estático)
- ✅ JaCoCo (cobertura de código)
- ✅ Tests unitarios e integración

### Ver Reportes
- **JaCoCo**: `target/site/jacoco/index.html`
- **SpotBugs**: `target/spotbugsXml.xml`
- **Checkstyle**: Consola de Maven

## 🏛️ Estructura del Proyecto

```
payment-orders/
├── src/main/java/com/banking/paymentorders/
│   ├── domain/                    # Capa de Dominio
│   │   ├── model/                 # Entidades y Value Objects
│   │   ├── port/in/               # Puertos de entrada (Use Cases)
│   │   ├── port/out/              # Puertos de salida (Repositories)
│   │   └── exception/             # Excepciones de dominio
│   ├── application/               # Capa de Aplicación
│   │   └── service/               # Servicios (implementan Use Cases)
│   ├── adapter/                   # Capa de Adaptadores
│   │   ├── in/rest/               # Adaptador REST (Controller + Mapper)
│   │   └── out/persistence/       # Adaptador Persistencia (R2DBC)
│   └── config/                    # Configuración
├── src/main/resources/
│   ├── api/openapi.yaml           # Especificación OpenAPI
│   ├── schema.sql                 # Esquema de base de datos
│   └── application.properties     # Configuración de la aplicación
├── ai/                            # Documentación y recursos
│   ├── analisis_migracion.md      # Análisis de migración BIAN
│   └── postman_collection_bian.json # Colección de Postman
├── docker-compose.yml             # Docker Compose
├── Dockerfile                     # Dockerfile multi-stage
└── pom.xml                        # Configuración Maven
```

## 🔐 Seguridad

- Las credenciales en `docker-compose.yml` son para desarrollo local
- En producción, usar secrets de Docker o variables de entorno seguras
- La aplicación usa Spring Security (si está configurado)

## 📝 Variables de Entorno

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `DB_URL` | URL de conexión R2DBC | `r2dbc:postgresql://localhost:5432/paymentorders` |
| `DB_USERNAME` | Usuario de base de datos | `paymentuser` |
| `DB_PASSWORD` | Contraseña de base de datos | `paymentpass` |
| `SERVER_PORT` | Puerto de la aplicación | `8080` |

## 🤝 Contribución

Este proyecto sigue:
- Google Java Style Guide
- Conventional Commits
- BIAN 12.0 Standards

## 📄 Licencia

[Especificar licencia]

## 👥 Autores

- Desarrollado con asistencia de IA
- Documentación en `ai/prompts.md` y `ai/decisiones.md`

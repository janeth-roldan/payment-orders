# Análisis de Migración SOAP a REST - Payment Orders

## 📋 Información del Proyecto

**Proyecto**: Migración de Servicio SOAP de Órdenes de Pago a REST API  
**Alineación**: BIAN Payment Initiation Service Domain  
**Fecha de Análisis**: Noviembre 2025  
**Arquitectura Objetivo**: Hexagonal (Puertos y Adaptadores)  
**Enfoque**: Contract-First con OpenAPI 3.0

---

## 🔍 Análisis del WSDL Legado

### Servicio SOAP Analizado
- **Nombre**: PaymentOrderService
- **Namespace**: http://legacy.bank/payments
- **Ubicación**: http://soap-mock:8081/legacy/payments

### Operaciones Identificadas

#### 1. SubmitPaymentOrder
**Propósito**: Enviar una nueva orden de pago al sistema

**Request (SubmitPaymentOrderRequest)**:
```xml
<SubmitPaymentOrderRequest>
  <externalId>string</externalId>           <!-- ID externo del cliente -->
  <debtorIban>string</debtorIban>           <!-- IBAN del deudor -->
  <creditorIban>string</creditorIban>       <!-- IBAN del acreedor -->
  <amount>decimal</amount>                  <!-- Monto del pago -->
  <currency>string</currency>               <!-- Moneda (USD, EUR, etc.) -->
  <remittanceInfo>string</remittanceInfo>   <!-- Info adicional (opcional) -->
  <requestedExecutionDate>date</requestedExecutionDate> <!-- Fecha de ejecución -->
</SubmitPaymentOrderRequest>
```

**Response (SubmitPaymentOrderResponse)**:
```xml
<SubmitPaymentOrderResponse>
  <paymentOrderId>string</paymentOrderId>   <!-- ID interno generado -->
  <status>string</status>                   <!-- Estado: ACCEPTED, REJECTED -->
</SubmitPaymentOrderResponse>
```

#### 2. GetPaymentOrderStatus
**Propósito**: Consultar el estado actual de una orden de pago

**Request (GetPaymentOrderStatusRequest)**:
```xml
<GetPaymentOrderStatusRequest>
  <paymentOrderId>string</paymentOrderId>   <!-- ID de la orden -->
</GetPaymentOrderStatusRequest>
```

**Response (GetPaymentOrderStatusResponse)**:
```xml
<GetPaymentOrderStatusResponse>
  <paymentOrderId>string</paymentOrderId>   <!-- ID de la orden -->
  <status>string</status>                   <!-- Estado actual -->
  <lastUpdate>dateTime</lastUpdate>         <!-- Última actualización -->
</GetPaymentOrderStatusResponse>
```

### Estados Identificados en SOAP
- **ACCEPTED**: Orden aceptada por el sistema
- **SETTLED**: Orden completada/liquidada
- **REJECTED**: Orden rechazada (implícito)
- **PENDING**: En proceso (implícito)

---

## 🎯 Mapeo a BIAN Payment Initiation

### Service Domain BIAN
- **SD**: Payment Initiation
- **BQ (Behavior Qualifier)**: PaymentOrder
- **Versión BIAN**: Alineado con estándar BIAN 12.0+

### Recursos REST Propuestos

#### Estructura de URLs
```
Base Path: /payment-initiation/payment-orders
```

#### Endpoints REST

| Método | Endpoint | Operación SOAP Origen | Descripción |
|--------|----------|----------------------|-------------|
| POST | `/payment-initiation/payment-orders` | SubmitPaymentOrder | Iniciar nueva orden de pago |
| GET | `/payment-initiation/payment-orders/{id}` | (nueva) | Recuperar orden de pago completa |
| GET | `/payment-initiation/payment-orders/{id}/status` | GetPaymentOrderStatus | Recuperar solo el estado |

---

## 📊 Mapeo Detallado de Campos

### POST /payment-initiation/payment-orders (Initiate)

#### Request Body Mapping

| Campo SOAP | Campo REST | Tipo | Obligatorio | Notas |
|------------|------------|------|-------------|-------|
| externalId | externalReference | string | Sí | ID de referencia del cliente |
| debtorIban | debtorAccount.iban | string | Sí | Objeto anidado para cuenta deudora |
| - | debtorAccount.name | string | No | Nombre del deudor (nuevo campo) |
| creditorIban | creditorAccount.iban | string | Sí | Objeto anidado para cuenta acreedora |
| - | creditorAccount.name | string | No | Nombre del acreedor (nuevo campo) |
| amount | instructedAmount.amount | number | Sí | Objeto anidado para monto |
| currency | instructedAmount.currency | string | Sí | Código ISO 4217 (USD, EUR) |
| remittanceInfo | remittanceInformation | string | No | Información de remesa |
| requestedExecutionDate | requestedExecutionDate | date | Sí | Formato ISO 8601 (YYYY-MM-DD) |

**Ejemplo Request REST**:
```json
{
  "externalReference": "EXT-123",
  "debtorAccount": {
    "iban": "EC12DEBTOR",
    "name": "Juan Pérez"
  },
  "creditorAccount": {
    "iban": "EC98CREDITOR",
    "name": "María López"
  },
  "instructedAmount": {
    "amount": 150.75,
    "currency": "USD"
  },
  "remittanceInformation": "Factura 001-123",
  "requestedExecutionDate": "2025-10-31"
}
```

#### Response Body Mapping

| Campo SOAP | Campo REST | Tipo | Notas |
|------------|------------|------|-------|
| paymentOrderId | paymentOrderId | string | UUID o ID de negocio |
| status | status | string | Enum: INITIATED, ACCEPTED, REJECTED |
| - | createdDateTime | datetime | Timestamp de creación (nuevo) |
| - | _links | object | HATEOAS links (opcional) |

**Ejemplo Response REST**:
```json
{
  "paymentOrderId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "INITIATED",
  "createdDateTime": "2025-10-30T14:30:00Z",
  "_links": {
    "self": {
      "href": "/payment-initiation/payment-orders/550e8400-e29b-41d4-a716-446655440000"
    },
    "status": {
      "href": "/payment-initiation/payment-orders/550e8400-e29b-41d4-a716-446655440000/status"
    }
  }
}
```

### GET /payment-initiation/payment-orders/{id} (Retrieve)

**Operación Nueva** - No existe en SOAP, se agrega para completitud REST

#### Response Body

```json
{
  "paymentOrderId": "550e8400-e29b-41d4-a716-446655440000",
  "externalReference": "EXT-123",
  "debtorAccount": {
    "iban": "EC12DEBTOR",
    "name": "Juan Pérez"
  },
  "creditorAccount": {
    "iban": "EC98CREDITOR",
    "name": "María López"
  },
  "instructedAmount": {
    "amount": 150.75,
    "currency": "USD"
  },
  "remittanceInformation": "Factura 001-123",
  "requestedExecutionDate": "2025-10-31",
  "status": "COMPLETED",
  "createdDateTime": "2025-10-30T14:30:00Z",
  "lastUpdateDateTime": "2025-10-30T16:25:30Z"
}
```

### GET /payment-initiation/payment-orders/{id}/status (Retrieve Status)

#### Response Body Mapping

| Campo SOAP | Campo REST | Tipo | Notas |
|------------|------------|------|-------|
| paymentOrderId | paymentOrderId | string | ID de la orden |
| status | status | string | Estado actual |
| lastUpdate | lastUpdateDateTime | datetime | ISO 8601 con timezone |

**Ejemplo Response REST**:
```json
{
  "paymentOrderId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "lastUpdateDateTime": "2025-10-30T16:25:30Z"
}
```

---

## 🔄 Mapeo de Estados

### Estados SOAP → REST

| Estado SOAP | Estado REST | Descripción |
|-------------|-------------|-------------|
| ACCEPTED | INITIATED | Orden iniciada y aceptada |
| ACCEPTED | ACCEPTED | Orden validada y en proceso |
| SETTLED | COMPLETED | Orden completada exitosamente |
| - | PENDING | Orden pendiente de validación |
| - | REJECTED | Orden rechazada |
| - | FAILED | Orden fallida por error técnico |
| - | CANCELLED | Orden cancelada por usuario |

### Máquina de Estados Propuesta

```
INITIATED → ACCEPTED → COMPLETED
    ↓           ↓
REJECTED    FAILED
    ↓           ↓
CANCELLED   CANCELLED
```

---

## 🏗️ Arquitectura Hexagonal Propuesta

### Estructura de Capas

```
┌─────────────────────────────────────────────────────────────┐
│                    ADAPTER IN (REST)                        │
│  PaymentOrderController (implementa OpenAPI generado)      │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                   APPLICATION LAYER                         │
│  - InitiatePaymentOrderUseCase                             │
│  - RetrievePaymentOrderUseCase                             │
│  - RetrievePaymentOrderStatusUseCase                       │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                     DOMAIN LAYER                            │
│  - PaymentOrder (entity)                                    │
│  - Account (value object)                                   │
│  - Money (value object)                                     │
│  - PaymentOrderStatus (enum)                               │
│  - PaymentOrderPort (output port interface)               │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                  ADAPTER OUT (PERSISTENCE)                  │
│  - PaymentOrderRepositoryAdapter                           │
│  - PaymentOrderJpaRepository                               │
│  - PaymentOrderEntity (JPA)                                │
└─────────────────────────────────────────────────────────────┘
```

### Paquetes Java

```
com.bank.paymentorders/
├── domain/
│   ├── model/
│   │   ├── PaymentOrder.java
│   │   ├── Account.java
│   │   ├── Money.java
│   │   └── PaymentOrderStatus.java
│   ├── port/
│   │   ├── in/
│   │   │   ├── InitiatePaymentOrderUseCase.java
│   │   │   ├── RetrievePaymentOrderUseCase.java
│   │   │   └── RetrievePaymentOrderStatusUseCase.java
│   │   └── out/
│   │       └── PaymentOrderPort.java
│   └── exception/
│       ├── PaymentOrderNotFoundException.java
│       └── InvalidPaymentOrderException.java
├── application/
│   └── service/
│       ├── InitiatePaymentOrderService.java
│       ├── RetrievePaymentOrderService.java
│       └── RetrievePaymentOrderStatusService.java
├── adapter/
│   ├── in/
│   │   └── rest/
│   │       ├── PaymentOrderController.java
│   │       └── mapper/
│   │           └── PaymentOrderRestMapper.java
│   └── out/
│       └── persistence/
│           ├── PaymentOrderRepositoryAdapter.java
│           ├── PaymentOrderJpaRepository.java
│           ├── entity/
│           │   └── PaymentOrderEntity.java
│           └── mapper/
│               └── PaymentOrderPersistenceMapper.java
└── config/
    ├── OpenApiConfig.java
    └── BeanConfig.java
```

---

## 📝 Contrato OpenAPI - Estructura

### Información General

```yaml
openapi: 3.0.3
info:
  title: Payment Initiation API - PaymentOrder
  description: API REST para gestión de órdenes de pago alineada con BIAN
  version: 1.0.0
  contact:
    name: Equipo de Desarrollo
    email: dev@bank.com

servers:
  - url: http://localhost:8080
    description: Servidor de desarrollo local
  - url: https://api.bank.com
    description: Servidor de producción
```

### Schemas Principales

1. **InitiatePaymentOrderRequest**
2. **InitiatePaymentOrderResponse**
3. **PaymentOrderResponse**
4. **PaymentOrderStatusResponse**
5. **Account**
6. **Money**
7. **ErrorResponse** (RFC 7807)

### Validaciones Propuestas

| Campo | Validación |
|-------|------------|
| externalReference | required, maxLength: 50 |
| debtorAccount.iban | required, pattern: IBAN regex |
| creditorAccount.iban | required, pattern: IBAN regex |
| instructedAmount.amount | required, minimum: 0.01, maximum: 999999999.99 |
| instructedAmount.currency | required, pattern: ^[A-Z]{3}$ |
| requestedExecutionDate | required, format: date, futureOrToday |
| remittanceInformation | optional, maxLength: 140 |

---

## 🧪 Estrategia de Testing

### Tests Unitarios (Capa Domain)

**Objetivo**: ≥80% cobertura

- `PaymentOrderTest.java` - Validar entidad y reglas de negocio
- `AccountTest.java` - Validar value object
- `MoneyTest.java` - Validar value object y operaciones
- `PaymentOrderStatusTest.java` - Validar transiciones de estado

### Tests de Integración (E2E)

**Herramienta**: RestAssured o WebTestClient

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PaymentOrderIntegrationTest {
    
    @Test
    void shouldInitiatePaymentOrder() {
        // POST /payment-initiation/payment-orders
        // Verificar status 201 Created
        // Verificar response body
        // Verificar Location header
    }
    
    @Test
    void shouldRetrievePaymentOrder() {
        // GET /payment-initiation/payment-orders/{id}
        // Verificar status 200 OK
        // Verificar datos completos
    }
    
    @Test
    void shouldRetrievePaymentOrderStatus() {
        // GET /payment-initiation/payment-orders/{id}/status
        // Verificar status 200 OK
        // Verificar estado actual
    }
    
    @Test
    void shouldReturn404WhenPaymentOrderNotFound() {
        // GET con ID inexistente
        // Verificar status 404 Not Found
        // Verificar RFC 7807 error response
    }
}
```

### Tests de Contrato (Contract Testing)

- Validar que controladores implementan interfaces generadas
- Validar schemas OpenAPI con ejemplos
- Validar validaciones de Bean Validation

---

## 🐳 Estrategia de Contenerización

### Dockerfile Multi-Stage

```dockerfile
# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY ../pom.xml .
COPY ../src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  payment-orders-service:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/paymentorders
    depends_on:
      - postgres
    networks:
      - payment-network

  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=paymentorders
      - POSTGRES_USER=admin
      - POSTGRES_PASSWORD=secret
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - payment-network

volumes:
  postgres-data:

networks:
  payment-network:
    driver: bridge
```

---

## 📦 Dependencias Maven Principales

```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- JPA / Persistence (opcional) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    
    <!-- OpenAPI Generator -->
    <dependency>
        <groupId>org.openapitools</groupId>
        <artifactId>jackson-databind-nullable</artifactId>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## ✅ Checklist de Implementación

### Fase 1: Análisis y Diseño ✓
- [x] Analizar WSDL
- [x] Mapear operaciones SOAP → REST
- [x] Definir estructura OpenAPI
- [x] Diseñar arquitectura hexagonal
- [x] Documentar decisiones

### Fase 2: Setup del Proyecto
- [ ] Crear proyecto Spring Boot 3.x con Java 17
- [ ] Configurar pom.xml con plugins necesarios
- [ ] Crear estructura de paquetes hexagonal
- [ ] Configurar openapi-generator-maven-plugin
- [ ] Crear openapi.yaml inicial

### Fase 3: Contract-First
- [ ] Completar especificación OpenAPI
- [ ] Generar interfaces y DTOs con Maven
- [ ] Verificar generación sin errores
- [ ] Crear mappers REST

### Fase 4: Implementación Domain
- [ ] Crear entidad PaymentOrder
- [ ] Crear value objects (Account, Money)
- [ ] Crear enum PaymentOrderStatus
- [ ] Definir interfaces de puertos
- [ ] Implementar reglas de negocio

### Fase 5: Implementación Application
- [ ] Implementar InitiatePaymentOrderUseCase
- [ ] Implementar RetrievePaymentOrderUseCase
- [ ] Implementar RetrievePaymentOrderStatusUseCase
- [ ] Crear servicios de aplicación

### Fase 6: Implementación Adapters
- [ ] Implementar PaymentOrderController
- [ ] Implementar PaymentOrderRepositoryAdapter
- [ ] Configurar JPA entities (si se usa persistencia)
- [ ] Crear mappers de persistencia

### Fase 7: Testing
- [ ] Escribir tests unitarios de dominio
- [ ] Escribir tests de servicios
- [ ] Escribir tests de integración E2E
- [ ] Verificar cobertura ≥80% con JaCoCo
- [ ] Ejecutar mvn verify sin errores

### Fase 8: Calidad
- [ ] Configurar Checkstyle
- [ ] Configurar SpotBugs
- [ ] Corregir todas las violaciones
- [ ] Ejecutar mvn verify exitosamente

### Fase 9: Docker
- [ ] Crear Dockerfile multi-stage
- [ ] Crear docker-compose.yml
- [ ] Probar build de imagen
- [ ] Probar ejecución con docker-compose

### Fase 10: Documentación
- [ ] Completar README.md
- [ ] Documentar prompts de IA en ai/prompts.md
- [ ] Guardar generaciones de IA en ai/generations/
- [ ] Documentar decisiones en ai/decisions.md
- [ ] Probar colección Postman

---

## 🤖 Uso de IA - Plan de Documentación

### Prompts a Documentar

1. **Análisis de WSDL**
   - Prompt: "Analiza este WSDL y extrae operaciones, campos y tipos de datos"
   - Output: Estructura de operaciones y campos

2. **Generación de OpenAPI**
   - Prompt: "Genera especificación OpenAPI 3.0 para estos endpoints REST alineados con BIAN"
   - Output: Borrador de openapi.yaml

3. **Esqueleto Hexagonal**
   - Prompt: "Genera estructura de paquetes para arquitectura hexagonal en Spring Boot"
   - Output: Estructura de clases y paquetes

4. **Generación de Tests**
   - Prompt: "Genera tests unitarios para esta entidad de dominio"
   - Output: Clases de test con JUnit 5

### Correcciones Manuales Esperadas

- Ajustar validaciones de OpenAPI
- Corregir nombres de campos para alineación BIAN
- Implementar lógica de negocio específica
- Ajustar mappers entre capas
- Configurar propiedades de Spring Boot

---

## 📊 Métricas de Calidad Objetivo

| Métrica | Objetivo | Herramienta |
|---------|----------|-------------|
| Cobertura de Código | ≥80% | JaCoCo |
| Violaciones Checkstyle | 0 | maven-checkstyle-plugin |
| Bugs SpotBugs | 0 | spotbugs-maven-plugin |
| Tests Unitarios | ≥50 tests | JUnit 5 |
| Tests Integración | ≥10 tests | RestAssured |
| Tiempo de Build | <3 min | Maven |
| Tamaño Imagen Docker | <200 MB | Docker |

---

## 🚀 Próximos Pasos

1. **Iniciar Fase 2**: Crear proyecto Spring Boot con estructura base
2. **Crear openapi.yaml**: Especificación completa de API REST
3. **Configurar Maven**: Plugins de generación y calidad
4. **Implementar Domain**: Entidades y lógica de negocio
5. **Documentar IA**: Registrar todos los prompts usados

---

## 📚 Referencias

- **BIAN Payment Initiation SD**: https://bian.org/servicelandscape-12-0-0/payment-initiation/
- **OpenAPI 3.0 Spec**: https://swagger.io/specification/
- **Hexagonal Architecture**: https://alistair.cockburn.us/hexagonal-architecture/
- **Spring Boot 3 Docs**: https://docs.spring.io/spring-boot/docs/current/reference/html/
- **RFC 7807 Problem Details**: https://tools.ietf.org/html/rfc7807

---

**Documento generado**: Noviembre 2025  
**Versión**: 1.0  
**Estado**: Análisis Completo - Listo para Implementación

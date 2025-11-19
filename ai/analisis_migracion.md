# Análisis de Migración SOAP a REST - Payment Orders

**Proyecto**: Migración de Servicio SOAP de Órdenes de Pago a REST API  
**Alineación**: BIAN Payment Initiation Service Domain 12.0  
**Fecha**: Noviembre 2025  
**Arquitectura**: Hexagonal (Puertos y Adaptadores)  
**Enfoque**: Contract-First con OpenAPI 3.0

---

## 📑 Tabla de Contenidos

1. [Análisis del WSDL Legado](#-análisis-del-wsdl-legado)
2. [Mapeo a BIAN Payment Initiation](#-mapeo-a-bian-payment-initiation)
3. [Modelo de Datos BIAN 12.0](#-modelo-de-datos-bian-120)
4. [Mapeo Detallado de Campos](#-mapeo-detallado-de-campos)
5. [Mapeo de Estados](#-mapeo-de-estados)
6. [Arquitectura Hexagonal](#-arquitectura-hexagonal)
7. [Estrategia de Testing](#-estrategia-de-testing)
8. [Referencias BIAN](#-referencias-bian)

---

## 🔍 Análisis del WSDL Legado

### Servicio SOAP
- **Nombre**: PaymentOrderService
- **Namespace**: `http://legacy.bank/payments`
- **Ubicación**: `http://soap-mock:8081/legacy/payments`

### Operaciones SOAP

| Operación | Propósito | Request | Response |
|-----------|-----------|---------|----------|
| **SubmitPaymentOrder** | Enviar nueva orden de pago | externalId, debtorIban, creditorIban, amount, currency, remittanceInfo, requestedExecutionDate | paymentOrderId, status |
| **GetPaymentOrderStatus** | Consultar estado de orden | paymentOrderId | paymentOrderId, status, lastUpdate |

### Estados SOAP
- **ACCEPTED**: Orden aceptada
- **SETTLED**: Orden completada/liquidada
- **REJECTED**: Orden rechazada (implícito)
- **PENDING**: En proceso (implícito)

---

## 🎯 Mapeo a BIAN Payment Initiation

### Service Domain BIAN
- **SD**: Payment Initiation
- **BQ**: PaymentOrder
- **Versión**: 12.0
- **BOM Diagram**: https://bian.org/servicelandscape-12-0-0/views/view_28713.html

> **IMPORTANTE**: El Control Record oficial se denomina `PaymentOrderProcedure` según BIAN Service Landscape 12.0

### Endpoints REST Propuestos

| Método | Endpoint | Operación SOAP | Descripción |
|--------|----------|----------------|-------------|
| **POST** | `/payment-initiation/payment-orders` | SubmitPaymentOrder | Iniciar orden de pago |
| **GET** | `/payment-initiation/payment-orders/{id}` | (nueva) | Recuperar orden completa |
| **GET** | `/payment-initiation/payment-orders/{id}/status` | GetPaymentOrderStatus | Recuperar estado |

---

## 📐 Modelo de Datos BIAN 12.0

### PaymentOrderProcedure (Control Record)

```
PaymentInitiation (Service Domain)
    └── PaymentOrderProcedure (Control Record)
        ├── PaymentOrderProcedureInstanceReference: string (ID único)
        ├── PaymentOrderProcedureInstanceStatus: enum
        ├── PaymentTransactionInitiatorReference: string
        │
        ├── Payer (Debtor):
        │   ├── PayerReference: string
        │   ├── PayerBankReference: string
        │   └── PayerProductInstanceReference: string (IBAN)
        │
        ├── Payee (Creditor):
        │   ├── PayeeReference: string
        │   ├── PayeeBankReference: string
        │   └── PayeeProductInstanceReference: string (IBAN)
        │
        ├── Payment Details:
        │   ├── Amount: decimal
        │   ├── Currency: string (ISO 4217)
        │   ├── PaymentMechanismType: string
        │   └── PaymentOrderProcedureInstanceRecord: object
        │
        └── Date Information:
            ├── DateType: string
            └── Date: date (ISO 8601)
```

### Estados BIAN (Enum)
- `Initiated` - Orden iniciada
- `Pending` - Pendiente de validación
- `Accepted` - Aceptada para procesamiento
- `InProgress` - En proceso de ejecución
- `Completed` - Completada exitosamente
- `Settled` - Liquidada
- `Rejected` - Rechazada
- `Failed` - Fallida
- `Cancelled` - Cancelada

---

## 📊 Mapeo Detallado de Campos

> **Referencia BIAN**: Todos los campos están mapeados según el BOM Diagram oficial de BIAN 12.0  
> **Control Record**: `PaymentOrderProcedure`  
> **Enlace**: https://bian.org/servicelandscape-12-0-0/views/view_28713.html

### Resumen de Mapeo por Endpoint

| Endpoint | Método | Campos BIAN Principales | Propósito |
|----------|--------|------------------------|-----------|
| `/payment-initiation/payment-orders` | POST | PaymentTransactionInitiatorReference, PayerProductInstanceReference, PayeeProductInstanceReference, Amount, Currency, Date | Iniciar orden de pago |
| `/payment-initiation/payment-orders/{id}` | GET | PaymentOrderProcedureInstanceReference + todos los campos del POST | Recuperar orden completa |
| `/payment-initiation/payment-orders/{id}/status` | GET | PaymentOrderProcedureInstanceReference, PaymentOrderProcedureInstanceStatus | Consultar solo el estado |

### POST /payment-initiation/payment-orders (Initiate)

#### Request - Tabla de Mapeo SOAP → REST → BIAN

| Campo SOAP | Campo REST | Campo BIAN 12.0 (BOM) | Ruta Completa BIAN | Tipo | Obligatorio |
|------------|------------|----------------------|-------------------|------|-------------|
| externalId | externalReference | PaymentTransactionInitiatorReference | PaymentOrderProcedure.PaymentTransactionInitiatorReference | string | Sí |
| debtorIban | debtorAccount.iban | PayerProductInstanceReference | PaymentOrderProcedure.PayerProductInstanceReference | string (IBAN) | Sí |
| - | debtorAccount.name | PayerReference | PaymentOrderProcedure.PayerReference | string | No |
| - | - | PayerBankReference | PaymentOrderProcedure.PayerBankReference | string | No |
| creditorIban | creditorAccount.iban | PayeeProductInstanceReference | PaymentOrderProcedure.PayeeProductInstanceReference | string (IBAN) | Sí |
| - | creditorAccount.name | PayeeReference | PaymentOrderProcedure.PayeeReference | string | No |
| - | - | PayeeBankReference | PaymentOrderProcedure.PayeeBankReference | string | No |
| amount | instructedAmount.amount | Amount | PaymentOrderProcedure.Amount | decimal | Sí |
| currency | instructedAmount.currency | Currency | PaymentOrderProcedure.Currency | string (ISO 4217) | Sí |
| remittanceInfo | remittanceInformation | PaymentMechanismType | PaymentOrderProcedure.PaymentMechanismType | string | No |
| requestedExecutionDate | requestedExecutionDate | Date | PaymentOrderProcedure.Date (cuando DateType="RequestedExecutionDate") | date (ISO 8601) | Sí |

#### Response - Tabla de Mapeo REST → BIAN

| Campo REST | Campo BIAN 12.0 (BOM) | Ruta Completa BIAN | Tipo | Descripción |
|------------|----------------------|-------------------|------|-------------|
| paymentOrderId | PaymentOrderProcedureInstanceReference | PaymentOrderProcedure.PaymentOrderProcedureInstanceReference | string (UUID) | ID único de la orden |
| status | PaymentOrderProcedureInstanceStatus | PaymentOrderProcedure.PaymentOrderProcedureInstanceStatus | enum | Estado actual |
| createdDateTime | - | - | datetime (ISO 8601) | Timestamp de creación (campo REST adicional) |
| _links.self | - | - | object | HATEOAS link (campo REST adicional) |
| _links.status | - | - | object | HATEOAS link (campo REST adicional) |

### GET /payment-initiation/payment-orders/{id} (Retrieve)

#### Response - Tabla de Mapeo REST → BIAN

| Campo REST | Campo BIAN 12.0 (BOM) | Ruta Completa BIAN | Tipo | Descripción |
|------------|----------------------|-------------------|------|-------------|
| paymentOrderId | PaymentOrderProcedureInstanceReference | PaymentOrderProcedure.PaymentOrderProcedureInstanceReference | string (UUID) | ID único de la orden |
| externalReference | PaymentTransactionInitiatorReference | PaymentOrderProcedure.PaymentTransactionInitiatorReference | string | Referencia externa |
| debtorAccount.iban | PayerProductInstanceReference | PaymentOrderProcedure.PayerProductInstanceReference | string (IBAN) | IBAN del pagador |
| debtorAccount.name | PayerReference | PaymentOrderProcedure.PayerReference | string | Nombre del pagador |
| creditorAccount.iban | PayeeProductInstanceReference | PaymentOrderProcedure.PayeeProductInstanceReference | string (IBAN) | IBAN del beneficiario |
| creditorAccount.name | PayeeReference | PaymentOrderProcedure.PayeeReference | string | Nombre del beneficiario |
| instructedAmount.amount | Amount | PaymentOrderProcedure.Amount | decimal | Monto de la transacción |
| instructedAmount.currency | Currency | PaymentOrderProcedure.Currency | string (ISO 4217) | Código de moneda |
| remittanceInformation | PaymentMechanismType | PaymentOrderProcedure.PaymentMechanismType | string | Información de remesa |
| requestedExecutionDate | Date | PaymentOrderProcedure.Date | date (ISO 8601) | Fecha de ejecución solicitada |
| status | PaymentOrderProcedureInstanceStatus | PaymentOrderProcedure.PaymentOrderProcedureInstanceStatus | enum | Estado actual |
| createdDateTime | - | - | datetime (ISO 8601) | Timestamp de creación (campo REST adicional) |
| lastUpdateDateTime | - | - | datetime (ISO 8601) | Última actualización (campo REST adicional) |

### GET /payment-initiation/payment-orders/{id}/status (Retrieve Status)

#### Response - Tabla de Mapeo REST → BIAN

| Campo REST | Campo BIAN 12.0 (BOM) | Ruta Completa BIAN | Tipo | Descripción |
|------------|----------------------|-------------------|------|-------------|
| paymentOrderId | PaymentOrderProcedureInstanceReference | PaymentOrderProcedure.PaymentOrderProcedureInstanceReference | string (UUID) | ID único de la orden |
| status | PaymentOrderProcedureInstanceStatus | PaymentOrderProcedure.PaymentOrderProcedureInstanceStatus | enum | Estado actual |
| lastUpdateDateTime | - | - | datetime (ISO 8601) | Última actualización (campo REST adicional) |

### Ejemplo Request REST (POST)

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

### Ejemplo Response REST (POST)

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

### Ejemplo Response REST (GET /{id})

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

### Ejemplo Response REST (GET /{id}/status)

```json
{
  "paymentOrderId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "lastUpdateDateTime": "2025-10-30T16:25:30Z"
}
```

### Notas Importantes sobre el Mapeo BIAN

> **Campos BIAN Adicionales Disponibles** (no mapeados en versión inicial):
> - `PaymentOrderProcedure.PayerBankReference` - Referencia del banco del pagador
> - `PaymentOrderProcedure.PayeeBankReference` - Referencia del banco del beneficiario
> - `PaymentOrderProcedure.DateType` - Tipo de fecha (siempre "RequestedExecutionDate" en v1)
> - `PaymentOrderProcedure.PaymentOrderProcedureInstanceRecord` - Registro completo de la orden
> - `PaymentOrderProcedure.DocumentDirectoryEntryInstanceReference` - Referencia a documentos
>
> **Campos REST Adicionales** (no están en BIAN):
> - `createdDateTime` - Timestamp de creación (campo técnico REST)
> - `lastUpdateDateTime` - Timestamp de última actualización (campo técnico REST)
> - `_links` - HATEOAS links (patrón REST)

### Validaciones OpenAPI

| Campo REST | Campo BIAN | Validación | Descripción |
|------------|------------|------------|-------------|
| externalReference | PaymentTransactionInitiatorReference | required, maxLength: 50, pattern: `^[A-Z0-9-]+$` | Alfanumérico con guiones |
| debtorAccount.iban | PayerProductInstanceReference | required, pattern: `^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$` | Formato IBAN válido |
| debtorAccount.name | PayerReference | optional, maxLength: 100 | Nombre del pagador |
| creditorAccount.iban | PayeeProductInstanceReference | required, pattern: `^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$` | Formato IBAN válido |
| creditorAccount.name | PayeeReference | optional, maxLength: 100 | Nombre del beneficiario |
| instructedAmount.amount | Amount | required, minimum: 0.01, maximum: 999999999.99, multipleOf: 0.01 | Monto con 2 decimales |
| instructedAmount.currency | Currency | required, pattern: `^[A-Z]{3}$`, enum: [USD, EUR, etc.] | Código ISO 4217 |
| remittanceInformation | PaymentMechanismType | optional, maxLength: 140 | Información de remesa |
| requestedExecutionDate | Date | required, format: date, pattern: `^\d{4}-\d{2}-\d{2}$` | Formato ISO 8601 (YYYY-MM-DD) |

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

### Máquina de Estados

```
INITIATED → PENDING → ACCEPTED → IN_PROGRESS → COMPLETED
    ↓          ↓          ↓            ↓
REJECTED   REJECTED   FAILED       FAILED
    ↓          ↓          ↓            ↓
CANCELLED  CANCELLED  CANCELLED    CANCELLED
```

---

## 🏗️ Arquitectura Hexagonal

### Estructura de Paquetes

```
com.banking.paymentorders/
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
│           ├── PaymentOrderR2dbcRepository.java
│           ├── entity/
│           │   └── PaymentOrderEntity.java
│           └── mapper/
│               └── PaymentOrderPersistenceMapper.java
└── config/
    ├── OpenApiConfig.java
    └── BeanConfig.java
```

### Diagrama de Capas

```
┌─────────────────────────────────────────────────────────────┐
│              ADAPTER IN (REST Controller)                   │
│  - Implementa interfaces generadas por OpenAPI             │
│  - Mapea DTOs REST ↔ Domain Models                         │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                  APPLICATION LAYER                          │
│  - Orquesta casos de uso                                   │
│  - Coordina puertos de entrada/salida                      │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                    DOMAIN LAYER                             │
│  - Lógica de negocio pura (sin dependencias externas)      │
│  - Entidades, Value Objects, Enums                         │
│  - Interfaces de puertos (in/out)                          │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│             ADAPTER OUT (Persistence)                       │
│  - Implementa puertos de salida                            │
│  - Mapea Domain Models ↔ Entities JPA/R2DBC                │
└─────────────────────────────────────────────────────────────┘
```

---

## 🧪 Estrategia de Testing

### Tests Unitarios (Domain Layer)
**Objetivo**: ≥80% cobertura

- `PaymentOrderTest` - Validar entidad y reglas de negocio
- `AccountTest` - Validar value object
- `MoneyTest` - Validar value object y operaciones
- `PaymentOrderStatusTest` - Validar transiciones de estado

### Tests de Integración (E2E)
**Herramienta**: WebTestClient (Spring WebFlux)

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PaymentOrderIntegrationTest {
    
    @Test
    void shouldInitiatePaymentOrder() {
        // POST /payment-initiation/payment-orders
        // Verificar: 201 Created, response body, Location header
    }
    
    @Test
    void shouldRetrievePaymentOrder() {
        // GET /payment-initiation/payment-orders/{id}
        // Verificar: 200 OK, datos completos
    }
    
    @Test
    void shouldRetrievePaymentOrderStatus() {
        // GET /payment-initiation/payment-orders/{id}/status
        // Verificar: 200 OK, estado actual
    }
    
    @Test
    void shouldReturn404WhenPaymentOrderNotFound() {
        // GET con ID inexistente
        // Verificar: 404 Not Found, RFC 7807 error response
    }
}
```

### Métricas de Calidad

| Métrica | Objetivo | Herramienta |
|---------|----------|-------------|
| Cobertura de Código | ≥80% | JaCoCo |
| Violaciones Checkstyle | 0 | maven-checkstyle-plugin |
| Bugs SpotBugs | 0 | spotbugs-maven-plugin |
| Tests Unitarios | ≥50 tests | JUnit 5 |
| Tests Integración | ≥10 tests | WebTestClient |

---

## 📚 Referencias BIAN

### Documentación Oficial BIAN 12.0

**Service Domain - Payment Initiation**:
- **BOM Diagram Payment Order**: https://bian.org/servicelandscape-12-0-0/views/view_28713.html
- **Service Landscape**: https://bian.org/servicelandscape-12-0-0/payment-initiation/
- **Semantic API**: https://bian.org/semantic-apis/payment-initiation/

**Estándares y Patrones**:
- **BIAN Architecture Framework**: https://bian.org/deliverables/bian-architecture-framework/
- **Control Record Patterns**: https://bian.org/deliverables/bian-standards/control-record-patterns/
- **Service Domain Patterns**: https://bian.org/deliverables/bian-standards/service-domain-patterns/
- **BIAN Data Dictionary**: https://bian.org/deliverables/bian-standards/data-dictionary/

**ISO 20022**:
- **Payment Messages**: https://www.iso20022.org/payments_messages.page
- **Credit Transfer**: https://www.iso20022.org/iso-20022-message-definitions?business-domain=1

**Recursos Adicionales**:
- **BIAN Metamodel**: https://bian.org/deliverables/bian-metamodel/
- **BIAN Glossary**: https://bian.org/deliverables/bian-glossary/
- **OpenAPI 3.0 Spec**: https://swagger.io/specification/
- **Hexagonal Architecture**: https://alistair.cockburn.us/hexagonal-architecture/
- **Spring Boot 3 Docs**: https://docs.spring.io/spring-boot/docs/current/reference/html/
- **RFC 7807 Problem Details**: https://tools.ietf.org/html/rfc7807

---

**Documento**: Análisis de Migración SOAP a REST  
**Versión**: 2.0  
**Fecha**: Noviembre 2025  
**Estado**: ✅ Completo - Listo para Implementación

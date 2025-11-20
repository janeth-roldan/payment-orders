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
3. [Estructura de Entidades BIAN](#-estructura-de-entidades-bian)
4. [Mapeo Detallado de Campos](#-mapeo-detallado-de-campos)
5. [Ejemplos Request/Response](#-ejemplos-requestresponse)
6. [Validaciones OpenAPI](#-validaciones-openapi)
7. [Mapeo de Estados](#-mapeo-de-estados)
8. [Arquitectura Hexagonal](#-arquitectura-hexagonal)
9. [Referencias BIAN](#-referencias-bian)

---

## 🔍 Análisis del WSDL Legado

### Servicio SOAP
- **Nombre**: PaymentOrderService
- **Namespace**: `http://legacy.bank/payments`
- **Ubicación**: `http://soap-mock:8081/legacy/payments`

### Operaciones SOAP

| Operación | Propósito | Campos Request | Campos Response |
|-----------|-----------|----------------|-----------------|
| **SubmitPaymentOrder** | Enviar nueva orden de pago | externalId, debtorIban, creditorIban, amount, currency, remittanceInfo, requestedExecutionDate | paymentOrderId, status |
| **GetPaymentOrderStatus** | Consultar estado de orden | paymentOrderId | paymentOrderId, status, lastUpdate |

### Estados SOAP Identificados
- **ACCEPTED**: Orden aceptada por el sistema
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

> **IMPORTANTE**: El Control Record oficial se denomina `PaymentOrderProcedure` según BIAN Service Landscape 12.0.  
> El OpenAPI usa nomenclatura BIAN con entidades anidadas que representen el modelo completo.

### Endpoints REST Propuestos

| Método | Endpoint | Operación SOAP | Descripción |
|--------|----------|----------------|-------------|
| **POST** | `/payment-initiation/payment-orders` | SubmitPaymentOrder | Iniciar orden de pago |
| **GET** | `/payment-initiation/payment-orders/{id}` | (nueva) | Recuperar orden completa |
| **GET** | `/payment-initiation/payment-orders/{id}/status` | GetPaymentOrderStatus | Recuperar estado |

---

## 📐 Estructura de Entidades BIAN

### PaymentOrderProcedure (Control Record Principal)

El Control Record principal que contiene toda la información de la orden de pago, organizado en entidades anidadas según el modelo BIAN 12.0.

```
PaymentOrderProcedure
├── paymentTransactionInitiatorReference (string)
├── payer (Payer)
│   ├── payerReference (string)
│   ├── payerBankReference (string)
│   └── payerProductInstanceReference (string)
├── payee (Payee)
│   ├── payeeReference (string)
│   ├── payeeBankReference (string)
│   └── payeeProductInstanceReference (string)
├── paymentDetails (PaymentDetails)
│   ├── amount (decimal)
│   ├── currency (string)
│   └── paymentMechanismType (string)
├── dateInformation (DateInformation)
│   ├── dateType (string)
│   └── date (date)
└── remittanceInformation (string)
```

### Entidades BIAN Definidas

#### 1. Payer (Información del Pagador/Deudor)
- **payerReference**: Nombre o referencia del pagador
- **payerBankReference**: Referencia del banco del pagador (opcional)
- **payerProductInstanceReference**: IBAN o número de cuenta del pagador

#### 2. Payee (Información del Beneficiario/Acreedor)
- **payeeReference**: Nombre o referencia del beneficiario
- **payeeBankReference**: Referencia del banco del beneficiario (opcional)
- **payeeProductInstanceReference**: IBAN o número de cuenta del beneficiario

#### 3. PaymentDetails (Detalles del Pago)
- **amount**: Monto de la transacción (decimal)
- **currency**: Código de moneda ISO 4217 (USD, EUR, etc.)
- **paymentMechanismType**: Tipo de mecanismo de pago (CreditTransfer, DirectDebit, etc.)

#### 4. DateInformation (Información de Fechas)
- **dateType**: Tipo de fecha (ej: "RequestedExecutionDate", "ValueDate")
- **date**: Valor de la fecha en formato ISO 8601 (YYYY-MM-DD)

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
> **Enlace**: https://bian.org/servicelandscape-12-0-0/views/view_28713.html

### POST /payment-initiation/payment-orders (Initiate)

#### Request - Tabla de Mapeo SOAP → BIAN con Entidades

| Campo SOAP | Entidad BIAN | Campo BIAN | Ruta Completa OpenAPI | Tipo | Obligatorio |
|------------|--------------|------------|----------------------|------|-------------|
| externalId | PaymentOrderProcedure | paymentTransactionInitiatorReference | paymentOrderProcedure.paymentTransactionInitiatorReference | string | Sí |
| debtorIban | Payer | payerProductInstanceReference | paymentOrderProcedure.payer.payerProductInstanceReference | string (IBAN) | Sí |
| - | Payer | payerReference | paymentOrderProcedure.payer.payerReference | string | No |
| - | Payer | payerBankReference | paymentOrderProcedure.payer.payerBankReference | string | No |
| creditorIban | Payee | payeeProductInstanceReference | paymentOrderProcedure.payee.payeeProductInstanceReference | string (IBAN) | Sí |
| - | Payee | payeeReference | paymentOrderProcedure.payee.payeeReference | string | No |
| - | Payee | payeeBankReference | paymentOrderProcedure.payee.payeeBankReference | string | No |
| amount | PaymentDetails | amount | paymentOrderProcedure.paymentDetails.amount | decimal | Sí |
| currency | PaymentDetails | currency | paymentOrderProcedure.paymentDetails.currency | string (ISO 4217) | Sí |
| remittanceInfo | PaymentDetails | paymentMechanismType | paymentOrderProcedure.paymentDetails.paymentMechanismType | string | No |
| requestedExecutionDate | DateInformation | date | paymentOrderProcedure.dateInformation.date | date (ISO 8601) | Sí |
| - | DateInformation | dateType | paymentOrderProcedure.dateInformation.dateType | string | Sí (valor fijo) |
| - | PaymentOrderProcedure | remittanceInformation | paymentOrderProcedure.remittanceInformation | string | No |

#### Response - Tabla de Mapeo BIAN

| Campo REST | Entidad BIAN | Campo BIAN | Ruta Completa OpenAPI | Tipo | Descripción |
|------------|--------------|------------|----------------------|------|-------------|
| paymentOrderProcedureInstanceReference | PaymentOrderProcedure | paymentOrderProcedureInstanceReference | paymentOrderProcedure.paymentOrderProcedureInstanceReference | string (UUID) | ID único de la orden |
| paymentOrderProcedureInstanceStatus | PaymentOrderProcedure | paymentOrderProcedureInstanceStatus | paymentOrderProcedure.paymentOrderProcedureInstanceStatus | enum | Estado actual |
| createdDateTime | _metadata | createdDateTime | _metadata.createdDateTime | datetime (ISO 8601) | Timestamp de creación |
| self | _links | self.href | _links.self.href | string (URI) | Link HATEOAS a la orden |

### GET /payment-initiation/payment-orders/{id} (Retrieve)

#### Response - Tabla de Mapeo BIAN Completo

| Campo REST | Entidad BIAN | Ruta Completa OpenAPI | Tipo | Descripción |
|------------|--------------|----------------------|------|-------------|
| paymentOrderProcedureInstanceReference | PaymentOrderProcedure | paymentOrderProcedure.paymentOrderProcedureInstanceReference | string (UUID) | ID único |
| paymentTransactionInitiatorReference | PaymentOrderProcedure | paymentOrderProcedure.paymentTransactionInitiatorReference | string | Referencia externa |
| payer | Payer | paymentOrderProcedure.payer | object | Información del pagador |
| payee | Payee | paymentOrderProcedure.payee | object | Información del beneficiario |
| paymentDetails | PaymentDetails | paymentOrderProcedure.paymentDetails | object | Detalles del pago |
| dateInformation | DateInformation | paymentOrderProcedure.dateInformation | object | Información de fechas |
| remittanceInformation | PaymentOrderProcedure | paymentOrderProcedure.remittanceInformation | string | Información de remesa |
| paymentOrderProcedureInstanceStatus | PaymentOrderProcedure | paymentOrderProcedure.paymentOrderProcedureInstanceStatus | enum | Estado actual |
| createdDateTime | _metadata | _metadata.createdDateTime | datetime | Timestamp de creación |
| lastUpdateDateTime | _metadata | _metadata.lastUpdateDateTime | datetime | Última actualización |

### GET /payment-initiation/payment-orders/{id}/status (Retrieve Status)

#### Response - Tabla de Mapeo BIAN

| Campo REST | Entidad BIAN | Ruta Completa OpenAPI | Tipo | Descripción |
|------------|--------------|----------------------|------|-------------|
| paymentOrderProcedureInstanceReference | PaymentOrderProcedure | paymentOrderProcedure.paymentOrderProcedureInstanceReference | string (UUID) | ID único |
| paymentOrderProcedureInstanceStatus | PaymentOrderProcedure | paymentOrderProcedure.paymentOrderProcedureInstanceStatus | enum | Estado actual |
| lastUpdateDateTime | _metadata | _metadata.lastUpdateDateTime | datetime | Última actualización |

---

## 📝 Ejemplos Request/Response

### POST /payment-initiation/payment-orders

#### Request Body (JSON)

```json
{
  "paymentOrderProcedure": {
    "paymentTransactionInitiatorReference": "EXT-123",
    "payer": {
      "payerReference": "Juan Pérez",
      "payerBankReference": "BANK001",
      "payerProductInstanceReference": "EC12DEBTOR"
    },
    "payee": {
      "payeeReference": "María López",
      "payeeBankReference": "BANK002",
      "payeeProductInstanceReference": "EC98CREDITOR"
    },
    "paymentDetails": {
      "amount": 150.75,
      "currency": "USD",
      "paymentMechanismType": "CreditTransfer"
    },
    "dateInformation": {
      "dateType": "RequestedExecutionDate",
      "date": "2025-10-31"
    },
    "remittanceInformation": "Factura 001-123"
  }
}
```

#### Response Body (201 Created)

```json
{
  "paymentOrderProcedure": {
    "paymentOrderProcedureInstanceReference": "550e8400-e29b-41d4-a716-446655440000",
    "paymentOrderProcedureInstanceStatus": "Initiated"
  },
  "_metadata": {
    "createdDateTime": "2025-10-30T14:30:00Z"
  },
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

### GET /payment-initiation/payment-orders/{id}

#### Response Body (200 OK)

```json
{
  "paymentOrderProcedure": {
    "paymentOrderProcedureInstanceReference": "550e8400-e29b-41d4-a716-446655440000",
    "paymentTransactionInitiatorReference": "EXT-123",
    "payer": {
      "payerReference": "Juan Pérez",
      "payerBankReference": "BANK001",
      "payerProductInstanceReference": "EC12DEBTOR"
    },
    "payee": {
      "payeeReference": "María López",
      "payeeBankReference": "BANK002",
      "payeeProductInstanceReference": "EC98CREDITOR"
    },
    "paymentDetails": {
      "amount": 150.75,
      "currency": "USD",
      "paymentMechanismType": "CreditTransfer"
    },
    "dateInformation": {
      "dateType": "RequestedExecutionDate",
      "date": "2025-10-31"
    },
    "remittanceInformation": "Factura 001-123",
    "paymentOrderProcedureInstanceStatus": "Completed"
  },
  "_metadata": {
    "createdDateTime": "2025-10-30T14:30:00Z",
    "lastUpdateDateTime": "2025-10-30T16:25:30Z"
  },
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

### GET /payment-initiation/payment-orders/{id}/status

#### Response Body (200 OK)

```json
{
  "paymentOrderProcedure": {
    "paymentOrderProcedureInstanceReference": "550e8400-e29b-41d4-a716-446655440000",
    "paymentOrderProcedureInstanceStatus": "Completed"
  },
  "_metadata": {
    "lastUpdateDateTime": "2025-10-30T16:25:30Z"
  }
}
```

---

## ✅ Validaciones OpenAPI

### Validaciones por Campo

| Campo BIAN | Validación | Descripción |
|------------|------------|-------------|
| paymentTransactionInitiatorReference | required, maxLength: 50, pattern: `^[A-Z0-9-]+$` | Alfanumérico con guiones |
| payer.payerProductInstanceReference | required, pattern: `^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$` | Formato IBAN válido |
| payer.payerReference | optional, maxLength: 100 | Nombre del pagador |
| payer.payerBankReference | optional, maxLength: 50 | Referencia del banco |
| payee.payeeProductInstanceReference | required, pattern: `^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$` | Formato IBAN válido |
| payee.payeeReference | optional, maxLength: 100 | Nombre del beneficiario |
| payee.payeeBankReference | optional, maxLength: 50 | Referencia del banco |
| paymentDetails.amount | required, minimum: 0.01, maximum: 999999999.99, multipleOf: 0.01 | Monto con 2 decimales |
| paymentDetails.currency | required, pattern: `^[A-Z]{3}$`, enum: [USD, EUR, etc.] | Código ISO 4217 |
| paymentDetails.paymentMechanismType | optional, maxLength: 50 | Tipo de mecanismo |
| dateInformation.dateType | required, enum: [RequestedExecutionDate, ValueDate] | Tipo de fecha |
| dateInformation.date | required, format: date, pattern: `^\d{4}-\d{2}-\d{2}$` | Formato ISO 8601 |
| remittanceInformation | optional, maxLength: 140 | Información de remesa |

---

## 🔄 Mapeo de Estados

### Estados SOAP → BIAN

| Estado SOAP | Estado BIAN | Descripción |
|-------------|-------------|-------------|
| ACCEPTED | Initiated | Orden iniciada y aceptada |
| ACCEPTED | Accepted | Orden validada y en proceso |
| SETTLED | Completed | Orden completada exitosamente |
| SETTLED | Settled | Orden liquidada |
| - | Pending | Orden pendiente de validación |
| - | Rejected | Orden rechazada |
| - | Failed | Orden fallida por error técnico |
| - | Cancelled | Orden cancelada por usuario |

### Máquina de Estados

```
Initiated → Pending → Accepted → InProgress → Completed → Settled
    ↓          ↓          ↓            ↓
Rejected   Rejected   Failed       Failed
    ↓          ↓          ↓            ↓
Cancelled  Cancelled  Cancelled    Cancelled
```

---

## 🏗️ Arquitectura Hexagonal

### Estructura de Paquetes

```
com.bank.paymentorders/
├── domain/
│   ├── model/
│   │   ├── PaymentOrderProcedure.java
│   │   ├── Payer.java
│   │   ├── Payee.java
│   │   ├── PaymentDetails.java
│   │   ├── DateInformation.java
│   │   └── PaymentOrderStatus.java (enum)
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

---

## 📚 Referencias BIAN

### Documentación Oficial BIAN 12.0

**Service Domain - Payment Initiation**:
- **BOM Diagram Payment Order**: https://bian.org/servicelandscape-12-0-0/views/view_28713.html
- **Service Domain Overview**: https://bian.org/servicelandscape-12-0-0/views/view_51891.html

**Recursos Adicionales**:
- **OpenAPI 3.0 Spec**: https://swagger.io/specification/
- **Hexagonal Architecture**: https://alistair.cockburn.us/hexagonal-architecture/
- **Spring Boot 3 Docs**: https://docs.spring.io/spring-boot/docs/current/reference/html/
- **RFC 7807 Problem Details**: https://tools.ietf.org/html/rfc7807

---

**Documento**: Análisis de Migración SOAP a REST  
**Versión**: 2.0  
**Fecha**: Noviembre 2025  
**Estado**: ✅ Completo - Listo para Implementación

# Reglas Windsurf - Proyecto de Migración Payment Orders

## Contexto del Proyecto
Migración de servicios de pago SOAP legados a API REST alineada con BIAN (Banking Industry Architecture Network) Service Domain Payment Initiation.

**Crítico**: Este es un proyecto de migración SOAP-a-REST para una entidad bancaria. La calidad, seguridad y alineación BIAN son obligatorias.

---

## Reglas de Documentación (OBLIGATORIO)

### Convención de Archivos de Documentación
- **Ubicación**: Todos los documentos deben generarse en la carpeta `ai/`
- **Formato**: Usar formato Markdown (extensión `.md`)
- **Idioma**: Todos los documentos deben estar en **español**
- **Nomenclatura**: Usar **snake_case** para nombres de archivos
  - ✅ Correcto: `analisis_migracion.md`, `decisiones_arquitectura.md`, `guia_instalacion.md`
  - ❌ Incorrecto: `AnalisisMigracion.md`, `decisiones-arquitectura.md`, `Guía Instalación.md`

### Estructura de Carpeta `ai/`
```
ai/
├── analisis_migracion.md         # Análisis WSDL y mapeo a REST
├── decisiones_arquitectura.md    # Decisiones de diseño
├── guia_instalacion.md           # Pasos de instalación y ejecución
├── prompts.md                    # Prompts utilizados con IA
├── decisiones.md                 # Decisiones tomadas con ayuda de IA
└── generations/                  # Carpeta para código generado por IA
    ├── openapi_v1.yaml
    ├── domain_models.java
    └── test_examples.java
```

### Estructura de Carpeta `doc/` (Archivos Proporcionados)
```
doc/
├── instrucciones.md              # Instrucciones del ejercicio (proporcionado)
├── PaymentOrderService.wsdl      # WSDL original (proporcionado)
├── postman_collection.json       # Colección Postman (proporcionado)
└── ejemplos/                     # Carpeta para ejemplos XML/JSON (proporcionado)
    ├── SubmitPaymentOrderRequest.xml
    ├── SubmitPaymentOrderResponse.xml
    ├── GetPaymentOrderStatusRequest.xml
    └── GetPaymentOrderStatusResponse.xml
```

### Reglas de Calidad de Documentación (OBLIGATORIO)

**Al actualizar cualquier documento en `ai/`, SIEMPRE verificar**:

#### 1. Tabla de Contenidos
- ✅ Debe existir al inicio del documento (después del título principal)
- ✅ Debe incluir enlaces internos a todas las secciones principales
- ✅ Debe usar formato Markdown con anclas (`[Texto](#seccion)`)
- ✅ Debe estar actualizada con todas las secciones del documento

#### 2. Estructura del Documento
- ✅ Título principal con `#` (H1) - solo uno por documento
- ✅ Secciones principales con `##` (H2)
- ✅ Subsecciones con `###` (H3) y `####` (H4)
- ✅ Uso consistente de separadores `---` entre secciones principales
- ✅ Información del documento al final (versión, fecha, estado)

#### 3. Códigos de Ejemplo
- ✅ Todos los ejemplos JSON deben tener sintaxis válida
- ✅ Todos los ejemplos XML deben tener sintaxis válida
- ✅ Usar bloques de código con lenguaje especificado: ` ```json `, ` ```xml `, ` ```java `
- ✅ Incluir comentarios explicativos cuando sea necesario
- ✅ Ejemplos de Request y Response para cada endpoint

#### 4. Ejemplos Request/Response
**Para cada endpoint documentado, incluir**:
- ✅ Ejemplo de Request completo (si aplica)
- ✅ Ejemplo de Response exitoso (200/201)
- ✅ Ejemplo de Response de error (400/404/500) cuando sea relevante
- ✅ Headers importantes (Content-Type, Location, etc.)
- ✅ Formato consistente (JSON con indentación de 2 espacios)

#### 5. Tablas de Mapeo
**Todas las tablas de mapeo deben incluir**:
- ✅ Columna de origen (Campo SOAP, Campo REST, etc.)
- ✅ Columna de destino (Campo REST, Campo BIAN, etc.)
- ✅ Columna de tipo de dato
- ✅ Columna de obligatoriedad (Sí/No/Opcional)
- ✅ Columna de notas o descripción
- ✅ **Columna de mapeo BIAN completo** (Ruta completa: `PaymentOrderProcedure.Campo`)
- ✅ Formato Markdown correcto con alineación de columnas
- ✅ Separación clara entre tablas de diferentes endpoints

#### 6. Enlaces y Referencias
- ✅ Todos los enlaces externos deben ser válidos y accesibles
- ✅ Enlaces a BIAN deben apuntar a la versión correcta (12.0)
- ✅ Enlaces internos deben funcionar correctamente
- ✅ Agrupar enlaces por categoría (BIAN, ISO, Documentación técnica, etc.)

#### 7. Consistencia de Nomenclatura
- ✅ Nombres de campos consistentes en todo el documento
- ✅ Usar nomenclatura BIAN oficial para campos mapeados
- ✅ Usar camelCase para campos REST
- ✅ Usar PascalCase para campos BIAN
- ✅ Usar UPPER_CASE para estados/enums

#### Checklist de Revisión Pre-Commit
Antes de confirmar cambios en documentos, verificar:
```markdown
- [ ] Tabla de contenidos actualizada
- [ ] Estructura de secciones correcta
- [ ] Todos los códigos de ejemplo son válidos
- [ ] Request/Response completos para cada endpoint
- [ ] Tablas de mapeo incluyen columna BIAN completa
- [ ] Enlaces verificados y funcionando
- [ ] Nomenclatura consistente
- [ ] Sin errores de ortografía
- [ ] Formato Markdown correcto
- [ ] Información de versión actualizada
```

---

## Stack Tecnológico (OBLIGATORIO)

### Tecnologías Core
- **Java**: 17 o superior
- **Spring Boot**: 3.x
- **Herramienta de Build**: Maven
- **Arquitectura**: Hexagonal (Puertos y Adaptadores)
- **Diseño API**: Contract-First con OpenAPI 3.0

### Testing y Calidad (OBLIGATORIO)
- **Testing**: JUnit 5, AssertJ, Mockito
- **Tests de Integración**: WebTestClient o RestAssured
- **Cobertura de Código**: JaCoCo ≥80% cobertura de líneas
- **Calidad de Código**: Checkstyle, SpotBugs (cero violaciones)
- **Validación**: `mvn verify` debe pasar sin errores

### Contenerización (OBLIGATORIO)
- **Docker**: Dockerfile multi-stage
- **Orquestación**: docker-compose.yml

#### Configuración Docker con Properties Personalizadas

**Objetivo**: Ejecutar el JAR de Spring Boot dentro del contenedor Docker apuntando a un archivo `application-docker.properties` específico.

##### Archivos Requeridos

**1. application-docker.properties**
- **Ubicación en host**: `/application-docker.properties` (raíz del proyecto)
- **Ubicación en contenedor**: `/app/config/application-docker.properties`
- **Permisos**: `644` (lectura para todos, escritura solo para owner)
- **Contenido**: Configuración específica para Docker
  - URL de PostgreSQL usando hostname `postgres` (del docker-compose)
  - Logging en modo DEBUG para troubleshooting
  - Configuración de Actuator y OpenAPI

**2. entrypoint.sh**
- **Ubicación en host**: `/entrypoint.sh` (raíz del proyecto)
- **Ubicación en contenedor**: `/app/entrypoint.sh`
- **Permisos**: `755` (ejecutable)
- **Script**:
```bash
#!/bin/sh
# Ejecutar Spring Boot con configuración personalizada
exec java $JAVA_OPTS -jar /app/app.jar --spring.config.location=file:/app/config/application-docker.properties
```

##### Estructura del Dockerfile

**Paso 1: Crear directorio de configuración**
```dockerfile
RUN mkdir -p /app/config
```

**Paso 2: Copiar application-docker.properties con permisos**
```dockerfile
COPY application-docker.properties /app/config/application-docker.properties
RUN chmod 644 /app/config/application-docker.properties
```

**Paso 3: Copiar entrypoint.sh con permisos de ejecución**
```dockerfile
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
```

**Paso 4: Cambiar ownership al usuario spring**
```dockerfile
RUN chown -R spring:spring /app
USER spring:spring
```

**Paso 5: Usar entrypoint.sh como punto de entrada**
```dockerfile
ENTRYPOINT ["/app/entrypoint.sh"]
```

##### Comandos de Construcción y Ejecución

**Construir imagen**:
```bash
docker build -t payment-orders:latest .
```

**Ejecutar con docker-compose**:
```bash
docker-compose up --build -d
```

**Verificar configuración**:
```bash
# Ver logs para confirmar que usa application-docker.properties
docker-compose logs -f payment-orders-app

# Verificar que la app está corriendo
curl http://localhost:8080/actuator/health
```

##### Ventajas de este Enfoque

1. ✅ **Separación de configuraciones**: Diferentes configs para local vs Docker
2. ✅ **Seguridad**: Usuario no-root ejecuta la aplicación
3. ✅ **Flexibilidad**: Fácil cambiar configuración sin reconstruir imagen
4. ✅ **Permisos correctos**: 644 para configs, 755 para scripts
5. ✅ **Variables de entorno**: Se pueden sobrescribir desde docker-compose

##### Checklist de Verificación Docker

Antes de hacer commit, verificar:
- [ ] `application-docker.properties` existe en la raíz del proyecto
- [ ] `entrypoint.sh` existe en la raíz del proyecto
- [ ] `entrypoint.sh` tiene permisos de ejecución (755)
- [ ] Dockerfile copia ambos archivos correctamente
- [ ] Dockerfile establece permisos correctos
- [ ] `docker-compose.yml` está configurado correctamente
- [ ] Build de Docker completa sin errores
- [ ] Contenedor inicia correctamente
- [ ] Health check responde OK
- [ ] Logs muestran conexión exitosa a PostgreSQL

### Opcional (Nice to Have)
- Spring WebFlux (reactivo)
- R2DBC con PostgreSQL
- Testcontainers
- RFC 7807 (application/problem+json) para manejo de errores
- Micrometer/Actuator para observabilidad
- Mecanismos de idempotencia

---

## Reglas de Alineación BIAN

### Service Domain
- **SD**: Payment Initiation
- **BQ**: PaymentOrder

### Convención de Nombres de Recursos
Todos los endpoints REST DEBEN seguir la nomenclatura BIAN:
- Ruta base: `/payment-initiation/payment-orders`
- Formato de ID de recurso: Usar kebab-case para rutas
- Usar terminología BIAN en DTOs y modelos de dominio

### Endpoints Requeridos
1. **POST** `/payment-initiation/payment-orders` - Iniciar orden de pago
2. **GET** `/payment-initiation/payment-orders/{id}` - Recuperar orden de pago
3. **GET** `/payment-initiation/payment-orders/{id}/status` - Recuperar estado de orden de pago

### Valores de Estado
Mapear estados SOAP a estados alineados con BIAN:
- SOAP `ACCEPTED` → REST `INITIATED` o `ACCEPTED`
- SOAP `SETTLED` → REST `COMPLETED` o `SETTLED`
- Considerar: PENDING, REJECTED, FAILED, CANCELLED

### Estructura de Entidades BIAN (OBLIGATORIO)

**El OpenAPI DEBE usar nomenclatura BIAN con entidades anidadas que representen el modelo BIAN 12.0**

#### Entidades BIAN Requeridas

1. **PaymentOrderProcedure** (Control Record Principal)
   - Contiene todos los datos de la orden de pago
   - Incluye entidades anidadas: Payer, Payee, PaymentDetails, DateInformation

2. **Payer** (Entidad para información del pagador/deudor)
   - `payerReference` - Nombre o referencia del pagador
   - `payerBankReference` - Referencia del banco del pagador
   - `payerProductInstanceReference` - IBAN o número de cuenta

3. **Payee** (Entidad para información del beneficiario/acreedor)
   - `payeeReference` - Nombre o referencia del beneficiario
   - `payeeBankReference` - Referencia del banco del beneficiario
   - `payeeProductInstanceReference` - IBAN o número de cuenta

4. **PaymentDetails** (Entidad para detalles del pago)
   - `amount` - Monto de la transacción
   - `currency` - Código de moneda ISO 4217
   - `paymentMechanismType` - Tipo de mecanismo de pago

5. **DateInformation** (Entidad para información de fechas)
   - `dateType` - Tipo de fecha (ej: "RequestedExecutionDate")
   - `date` - Valor de la fecha en formato ISO 8601

#### Estructura JSON Requerida (Request)

```json
{
  "paymentOrderProcedure": {
    "paymentTransactionInitiatorReference": "string",
    "payer": {
      "payerReference": "string",
      "payerBankReference": "string",
      "payerProductInstanceReference": "string"
    },
    "payee": {
      "payeeReference": "string",
      "payeeBankReference": "string",
      "payeeProductInstanceReference": "string"
    },
    "paymentDetails": {
      "amount": 0.00,
      "currency": "string",
      "paymentMechanismType": "string"
    },
    "dateInformation": {
      "dateType": "RequestedExecutionDate",
      "date": "2025-10-31"
    },
    "remittanceInformation": "string"
  }
}
```

#### Estructura JSON Requerida (Response)

```json
{
  "paymentOrderProcedure": {
    "paymentOrderProcedureInstanceReference": "uuid",
    "paymentOrderProcedureInstanceStatus": "Initiated"
  },
  "_metadata": {
    "createdDateTime": "2025-10-30T14:30:00Z"
  },
  "_links": {
    "self": {
      "href": "/payment-initiation/payment-orders/{id}"
    }
  }
}
```

#### Mapeo SOAP → BIAN con Entidades

| Campo SOAP | Entidad BIAN | Campo BIAN | Ruta Completa OpenAPI |
|------------|--------------|------------|----------------------|
| externalId | PaymentOrderProcedure | paymentTransactionInitiatorReference | paymentOrderProcedure.paymentTransactionInitiatorReference |
| debtorIban | Payer | payerProductInstanceReference | paymentOrderProcedure.payer.payerProductInstanceReference |
| - | Payer | payerReference | paymentOrderProcedure.payer.payerReference |
| creditorIban | Payee | payeeProductInstanceReference | paymentOrderProcedure.payee.payeeProductInstanceReference |
| - | Payee | payeeReference | paymentOrderProcedure.payee.payeeReference |
| amount | PaymentDetails | amount | paymentOrderProcedure.paymentDetails.amount |
| currency | PaymentDetails | currency | paymentOrderProcedure.paymentDetails.currency |
| remittanceInfo | PaymentOrderProcedure | remittanceInformation | paymentOrderProcedure.remittanceInformation |
| requestedExecutionDate | DateInformation | date | paymentOrderProcedure.dateInformation.date |

---

## Reglas de Arquitectura (Hexagonal)

### Estructura de Paquetes
```
src/main/java/com/bank/paymentorders/
├── domain/                    # Lógica de negocio core (sin dependencias de framework)
│   ├── model/                # Entidades de dominio
│   ├── port/                 # Interfaces (puertos in/out)
│   ├── service/              # Servicios de dominio
│   └── exception/            # Excepciones de dominio
├── application/              # Casos de uso / Servicios de aplicación
│   └── usecase/
├── adapter/                  # Adaptadores (in/out)
│   ├── in/
│   │   └── rest/            # Controladores REST (generados desde OpenAPI)
│   └── out/
│       ├── persistence/     # Implementaciones de repositorio
│       └── external/        # Clientes de servicios externos
└── config/                   # Configuración Spring
```

### Reglas de Dependencias
- **Capa Domain**: SIN dependencias externas (Spring, Jackson, etc.)
- **Capa Application**: Puede depender de domain
- **Adapters**: Pueden depender de domain y application
- **Config**: Conecta todo junto

### Convención de Nombres de Puertos
- Puertos de entrada (casos de uso): interfaz `*UseCase`
- Puertos de salida (repositorios): interfaz `*Port`

---

## Desarrollo Contract-First

### Generación OpenAPI
1. Crear `openapi.yaml` en `src/main/resources/api/`
2. Usar `openapi-generator-maven-plugin` para generar:
   - Interfaces API (los controladores implementan estas)
   - DTOs (modelos request/response)
3. **NUNCA** modificar código generado manualmente
4. Los controladores implementan las interfaces generadas

### Requisitos de Especificación OpenAPI
- Versión OpenAPI: 3.0.3 o superior
- Incluir `info`, `servers`, `paths`, `components`
- Definir todos los schemas en `components/schemas`
- Usar `$ref` para reutilización
- Incluir restricciones de validación (required, pattern, min, max)
- Documentar todas las respuestas (200, 400, 404, 500)

---

## Mapeo WSDL a REST

### Mapeo de Campos SOAP → REST
| Campo SOAP | Campo REST | Notas |
|------------|------------|-------|
| externalId | externalReference | Identificador externo |
| debtorIban | debtorAccount.iban | Objeto anidado |
| creditorIban | creditorAccount.iban | Objeto anidado |
| amount | instructedAmount.amount | Objeto anidado |
| currency | instructedAmount.currency | Objeto anidado |
| remittanceInfo | remittanceInformation | Opcional |
| requestedExecutionDate | requestedExecutionDate | Fecha ISO 8601 |
| paymentOrderId | paymentOrderId | ID interno |
| status | status | Valores enum |
| lastUpdate | lastUpdateDateTime | Datetime ISO 8601 |

### Mapeo de Operaciones
| Operación SOAP | Endpoint REST | Método HTTP |
|----------------|---------------|-------------|
| SubmitPaymentOrder | /payment-initiation/payment-orders | POST |
| GetPaymentOrderStatus | /payment-initiation/payment-orders/{id}/status | GET |
| (implícito) GetPaymentOrder | /payment-initiation/payment-orders/{id} | GET |

---

## Reglas de Estilo y Calidad de Código

### Estilo de Código Java
- Usar características de Java 17+ (records, sealed classes, pattern matching)
- Preferir inmutabilidad (campos final, records)
- Usar nombres de variables significativos (sin letras sueltas excepto contadores de bucle)
- Longitud máxima de método: 20 líneas
- Longitud máxima de clase: 300 líneas

### Reglas de Testing
- **Tests Unitarios**: Probar lógica de dominio en aislamiento
- **Tests de Integración**: Probar endpoints REST completos con `@SpringBootTest`
- **Cobertura de Tests**: Mínimo 80% cobertura de líneas (JaCoCo)
- Usar `@DisplayName` para nombres de test legibles
- Seguir patrón AAA: Arrange, Act, Assert
- Usar AssertJ para aserciones fluidas

### Manejo de Errores
- Usar excepciones de dominio en capa de dominio
- Mapear a códigos de estado HTTP en capa de adaptador
- Considerar RFC 7807 Problem Details para errores
- Nunca exponer stack traces a clientes

---

## Reglas de Docker

### Requisitos del Dockerfile
- Usar build multi-stage
- Imagen base: `eclipse-temurin:17-jre-alpine` para runtime
- Stage de build: `eclipse-temurin:17-jdk-alpine`
- Exponer puerto 8080
- Usar usuario no-root
- Optimizar caché de capas

### Requisitos de docker-compose.yml
- Nombre del servicio: `payment-orders-service`
- Incluir servicio PostgreSQL (si se usa persistencia)
- Definir redes
- Usar variables de entorno
- Incluir health checks

---

## Documentación de Uso de IA (OBLIGATORIO)

### Documentación Requerida
Crear carpeta `ai/` con archivos en **español** y nomenclatura **snake_case**:

1. **prompts.md**: Todos los prompts usados con IA (en español)
   - Prompt para análisis de WSDL
   - Prompt para generación de OpenAPI
   - Prompt para esqueleto de arquitectura hexagonal
   - Prompt para generación de tests

2. **generaciones/**: Carpeta con fragmentos de código generados por IA
   - Guardar outputs iniciales de IA antes de correcciones manuales
   - Archivos con nomenclatura: `generacion_openapi.yaml`, `generacion_dominio.java`, etc.

3. **decisiones.md**: Documentar correcciones manuales (en español)
   - Qué se cambió del output de IA
   - Por qué se cambió
   - Proceso de validación

### Estructura de Carpeta `ai/`
```
ai/
├── prompts.md                    # Todos los prompts en español
├── decisiones.md                 # Decisiones y correcciones manuales
└── generaciones/                 # Código generado por IA
    ├── generacion_openapi.yaml
    ├── generacion_entidades.java
    ├── generacion_tests.java
    └── generacion_dockerfile.txt
```

### Plantilla de Prompt de IA
```markdown
## Prompt: [Propósito]
**Fecha**: YYYY-MM-DD
**Herramienta**: [ChatGPT/Claude/Copilot/etc.]

### Input
[Texto de tu prompt]

### Resumen del Output
[Resumen breve de la respuesta de IA]

### Correcciones Manuales
- [Corrección 1]: [Razón]
- [Corrección 2]: [Razón]
```

---

## Flujo de Trabajo de Desarrollo

### Fase 1: Análisis y Diseño
1. Analizar estructura del WSDL
2. Mapear operaciones SOAP a endpoints REST
3. Diseñar especificación OpenAPI
4. Definir modelo de dominio

### Fase 2: Configuración Contract-First
1. Crear `openapi.yaml`
2. Configurar `openapi-generator-maven-plugin`
3. Generar interfaces API y DTOs
4. Verificar generación con `mvn clean compile`

### Fase 3: Implementación Arquitectura Hexagonal
1. Definir entidades de dominio y objetos de valor
2. Crear interfaces de puertos (entrada/salida)
3. Implementar servicios de dominio
4. Crear adaptadores (controladores REST, repositorios)
5. Conectar dependencias en clases de configuración

### Fase 4: Testing
1. Escribir tests unitarios para lógica de dominio
2. Escribir tests de integración para endpoints REST
3. Verificar cobertura ≥80% con JaCoCo
4. Ejecutar `mvn verify` (Checkstyle, SpotBugs)

### Fase 5: Contenerización
1. Crear Dockerfile multi-stage
2. Crear docker-compose.yml
3. Probar build local de Docker
4. Probar docker-compose up

### Fase 6: Documentación
1. Actualizar README.md con:
   - Contexto del proyecto
   - Decisiones de arquitectura
   - Instrucciones de setup local
   - Instrucciones de setup Docker
   - Ejemplos de uso de API
2. Documentar uso de IA en carpeta `ai/`

---

## Configuración Maven

### Plugins Requeridos
- `spring-boot-maven-plugin`
- `openapi-generator-maven-plugin`
- `maven-compiler-plugin` (Java 17)
- `maven-surefire-plugin` (tests unitarios)
- `maven-failsafe-plugin` (tests de integración)
- `jacoco-maven-plugin` (cobertura)
- `maven-checkstyle-plugin`
- `spotbugs-maven-plugin`

### Comandos Maven
- `mvn clean compile` - Generar código OpenAPI
- `mvn test` - Ejecutar tests unitarios
- `mvn verify` - Ejecutar todas las verificaciones (tests, cobertura, calidad)
- `mvn spring-boot:run` - Ejecutar aplicación localmente
- `mvn package` - Construir JAR

---

## Convención de Commits Git

Usar commits convencionales:
- `feat:` - Nueva funcionalidad
- `fix:` - Corrección de bug
- `docs:` - Documentación
- `test:` - Tests
- `refactor:` - Refactorización de código
- `chore:` - Cambios de build/configuración

Ejemplo: `feat: implementar endpoint de iniciar orden de pago`

---

## Checklist de Validación

Antes de entregar, verificar:
- [ ] Los 3 endpoints REST implementados
- [ ] Enfoque contract-first con OpenAPI usado
- [ ] Arquitectura hexagonal estructurada correctamente
- [ ] Tests unitarios escritos (capa de dominio)
- [ ] Tests de integración escritos (E2E)
- [ ] Cobertura JaCoCo ≥80%
- [ ] `mvn verify` pasa (Checkstyle, SpotBugs)
- [ ] Dockerfile (multi-stage) creado
- [ ] docker-compose.yml creado
- [ ] README.md completo con instrucciones de setup
- [ ] Uso de IA documentado en carpeta `ai/`
- [ ] Tests de colección Postman pasan

---

## Errores Comunes a Evitar

1. **NO** poner anotaciones de Spring en capa de dominio
2. **NO** modificar código generado de OpenAPI
3. **NO** saltarse tests de integración
4. **NO** hardcodear valores de configuración
5. **NO** exponer IDs internos en API REST (usar UUIDs o IDs de negocio)
6. **NO** retornar entidades de dominio directamente desde controladores
7. **NO** ignorar warnings de Checkstyle/SpotBugs
8. **NO** hacer commit sin ejecutar `mvn verify`

---

## Consideraciones de Seguridad

- Validar todos los datos de entrada
- Usar anotaciones de Bean Validation
- Sanitizar mensajes de error
- No registrar datos sensibles en logs (IBANs, montos)
- Usar HTTPS en producción (documentar en README)
- Considerar autenticación/autorización de API (documentar si se implementa)

---

## Guías de Performance

- Usar connection pooling para base de datos
- Implementar paginación para endpoints de listado (si se agregan)
- Considerar caching para operaciones de lectura
- Usar procesamiento asíncrono para operaciones de larga duración (si aplica)
- Monitorear con métricas de Actuator (si se implementa)

---

## Convenciones de Código Java (OBLIGATORIO)

### Nomenclatura de Métodos
- **SIEMPRE usar lowerCamelCase** para nombres de métodos
- Los nombres de métodos deben ser **descriptivos y verbosos**
- Usar verbos que indiquen la acción: `get`, `set`, `create`, `update`, `delete`, `find`, `validate`, `process`, `calculate`
- Para métodos booleanos usar prefijos: `is`, `has`, `can`, `should`

#### Ejemplos Correctos:
```java
// ✅ Correcto
public void initiatePaymentOrder() { }
public PaymentOrder findPaymentOrderById(UUID id) { }
public boolean isPaymentOrderValid() { }
public void validatePaymentDetails() { }
public Mono<PaymentOrder> retrievePaymentOrderStatus(UUID id) { }
```

#### Ejemplos Incorrectos:
```java
// ❌ Incorrecto
public void InitiatePaymentOrder() { }  // PascalCase
public void initiate_payment_order() { }  // snake_case
public void init() { }  // No descriptivo
public void process() { }  // Muy genérico
```

### Nomenclatura de Tests
- Los métodos de test deben ser **extremadamente descriptivos**
- Usar formato: `should[ExpectedBehavior]When[StateUnderTest]`
- O formato: `given[Precondition]When[Action]Then[ExpectedResult]`
- **NO usar guiones bajos** en nombres de métodos de test

#### Ejemplos de Tests Correctos:
```java
// ✅ Correcto - Formato should/when
@Test
void shouldCreatePaymentOrderWhenValidDataProvided() { }

@Test
void shouldThrowExceptionWhenPaymentOrderNotFound() { }

@Test
void shouldReturnInitiatedStatusWhenPaymentOrderCreated() { }

// ✅ Correcto - Formato given/when/then
@Test
void givenValidRequestWhenInitiatingPaymentOrderThenReturnCreatedStatus() { }

@Test
void givenNonExistentIdWhenRetrievingPaymentOrderThenThrowNotFoundException() { }
```

#### Ejemplos de Tests Incorrectos:
```java
// ❌ Incorrecto
@Test
void test_initiate_payment_order() { }  // snake_case

@Test
void testInitiate() { }  // No descriptivo

@Test
void test1() { }  // Nombre sin significado
```

### Nomenclatura de Clases
- **PascalCase** para nombres de clases
- Sufijos según tipo:
  - `Service` para servicios de aplicación
  - `Controller` para controladores REST
  - `Repository` para repositorios
  - `Adapter` para adaptadores
  - `Mapper` para mappers
  - `Exception` para excepciones personalizadas
  - `Test` para tests unitarios
  - `IntegrationTest` para tests de integración

### Nomenclatura de Variables
- **lowerCamelCase** para variables locales y parámetros
- **UPPER_SNAKE_CASE** para constantes
- Nombres descriptivos, evitar abreviaturas

```java
// ✅ Correcto
private static final int MAX_RETRY_ATTEMPTS = 3;
private PaymentOrderService paymentOrderService;
String paymentOrderId;

// ❌ Incorrecto
private static final int max_retry = 3;  // snake_case
private PaymentOrderService pos;  // Abreviatura
String id;  // Muy genérico
```

---

## Manejo de Errores (OBLIGATORIO)

### Estrategia Global de Manejo de Errores

**CRÍTICO**: El manejo de errores DEBE ser uniforme, completo y seguir RFC 7807 (Problem Details for HTTP APIs)

### Requisitos Obligatorios

#### 1. GlobalExceptionHandler
- **DEBE** existir un `@RestControllerAdvice` que maneje todas las excepciones
- **DEBE** ser compatible con Spring WebFlux y Spring Boot 3
- **DEBE** usar `ProblemDetail` de Spring 6+ (NO usar ErrorResponse personalizado)
- **OBLIGATORIO**: Usar `org.springframework.http.ProblemDetail` nativo
- **DEBE** manejar al menos estos tipos de errores:
  - Errores de validación (`WebExchangeBindException`, `MethodArgumentNotValidException`)
  - Errores de dominio (excepciones personalizadas del negocio)
  - Errores técnicos (excepciones genéricas, errores de infraestructura)
  - Errores de deserialización JSON/XML

#### 2. Estructura RFC 7807 (Problem Details)
Todas las respuestas de error DEBEN incluir:
```json
{
  "type": "https://api.bank.com/errors/not-found",
  "title": "Payment Order Not Found",
  "status": 404,
  "detail": "Payment order not found: 123e4567-e89b-12d3-a456-426614174000",
  "instance": "/payment-initiation/payment-orders/123e4567-e89b-12d3-a456-426614174000",
  "timestamp": "2025-11-19T15:30:00Z"
}
```

**Campos obligatorios**:
- `type` (URI): Identificador único del tipo de error
- `title` (string): Título legible del error
- `status` (integer): Código de estado HTTP
- `detail` (string): Descripción específica del error
- `instance` (URI): Ruta del recurso donde ocurrió el error
- `timestamp` (string): Marca de tiempo ISO 8601

#### 3. Excepciones de Dominio

**DEBE** existir una jerarquía de excepciones personalizadas:

```java
// Excepción base de dominio
public abstract class PaymentOrderDomainException extends RuntimeException {
  protected PaymentOrderDomainException(String message) {
    super(message);
  }
  
  protected PaymentOrderDomainException(String message, Throwable cause) {
    super(message, cause);
  }
}

// Excepciones específicas
public class PaymentOrderNotFoundException extends PaymentOrderDomainException {
  private final UUID paymentOrderId;
  
  public PaymentOrderNotFoundException(UUID paymentOrderId) {
    super("Payment order not found: " + paymentOrderId);
    this.paymentOrderId = paymentOrderId;
  }
  
  public UUID getPaymentOrderId() {
    return paymentOrderId;
  }
}

public class InvalidPaymentOrderException extends PaymentOrderDomainException {
  public InvalidPaymentOrderException(String message) {
    super(message);
  }
  
  public InvalidPaymentOrderException(String message, Throwable cause) {
    super(message, cause);
  }
}
```

#### 4. Mapeo de Excepciones a Códigos HTTP

| Excepción | HTTP Status | Tipo RFC 7807 |
|-----------|-------------|---------------|
| `PaymentOrderNotFoundException` | 404 NOT_FOUND | `/errors/not-found` |
| `InvalidPaymentOrderException` | 400 BAD_REQUEST | `/errors/validation-error` |
| `WebExchangeBindException` | 400 BAD_REQUEST | `/errors/validation-error` |
| `IllegalArgumentException` | 400 BAD_REQUEST | `/errors/invalid-argument` |
| `Exception` (genérica) | 500 INTERNAL_SERVER_ERROR | `/errors/internal-error` |

#### 5. Implementación del GlobalExceptionHandler

**Ejemplo de implementación obligatoria**:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Maneja PaymentOrderNotFoundException.
   */
  @ExceptionHandler(PaymentOrderNotFoundException.class)
  public ResponseEntity<ProblemDetail> handlePaymentOrderNotFound(
      PaymentOrderNotFoundException ex) {
    
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.NOT_FOUND, 
        ex.getMessage()
    );
    problemDetail.setType(URI.create("https://api.bank.com/errors/not-found"));
    problemDetail.setTitle("Payment Order Not Found");
    problemDetail.setProperty("paymentOrderId", ex.getPaymentOrderId());
    problemDetail.setProperty("timestamp", OffsetDateTime.now());
    
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
  }

  /**
   * Maneja InvalidPaymentOrderException.
   */
  @ExceptionHandler(InvalidPaymentOrderException.class)
  public ResponseEntity<ProblemDetail> handleInvalidPaymentOrder(
      InvalidPaymentOrderException ex) {
    
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST, 
        ex.getMessage()
    );
    problemDetail.setType(URI.create("https://api.bank.com/errors/validation-error"));
    problemDetail.setTitle("Invalid Payment Order");
    problemDetail.setProperty("timestamp", OffsetDateTime.now());
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
  }

  /**
   * Maneja errores de validación de Bean Validation.
   */
  @ExceptionHandler(WebExchangeBindException.class)
  public ResponseEntity<ProblemDetail> handleValidationErrors(
      WebExchangeBindException ex) {
    
    String details = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining(", "));
    
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST, 
        details
    );
    problemDetail.setType(URI.create("https://api.bank.com/errors/validation-error"));
    problemDetail.setTitle("Validation Error");
    problemDetail.setProperty("timestamp", OffsetDateTime.now());
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
  }

  /**
   * Maneja excepciones genéricas.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
    log.error("Unexpected error occurred", ex);
    
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR, 
        "An unexpected error occurred"
    );
    problemDetail.setType(URI.create("https://api.bank.com/errors/internal-error"));
    problemDetail.setTitle("Internal Server Error");
    problemDetail.setProperty("timestamp", OffsetDateTime.now());
    
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
  }
}
```

#### 6. Documentación en OpenAPI

**DEBE** documentar todas las respuestas de error en `openapi.yaml`:

```yaml
components:
  schemas:
    ErrorResponse:
      type: object
      required:
        - type
        - title
        - status
        - detail
        - timestamp
      properties:
        type:
          type: string
          format: uri
          description: URI que identifica el tipo de error
          example: "https://api.bank.com/errors/not-found"
        title:
          type: string
          description: Título legible del error
          example: "Payment Order Not Found"
        status:
          type: integer
          description: Código de estado HTTP
          example: 404
        detail:
          type: string
          description: Descripción específica del error
          example: "Payment order not found: 123e4567-e89b-12d3-a456-426614174000"
        instance:
          type: string
          format: uri
          description: URI del recurso donde ocurrió el error
          example: "/payment-initiation/payment-orders/123e4567-e89b-12d3-a456-426614174000"
        timestamp:
          type: string
          format: date-time
          description: Marca de tiempo del error
          example: "2025-11-19T15:30:00Z"

  responses:
    BadRequest:
      description: Solicitud inválida
      content:
        application/problem+json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            type: "https://api.bank.com/errors/validation-error"
            title: "Validation Error"
            status: 400
            detail: "amount: must be greater than 0"
            instance: "/payment-initiation/payment-orders"
            timestamp: "2025-11-19T15:30:00Z"
    
    NotFound:
      description: Recurso no encontrado
      content:
        application/problem+json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            type: "https://api.bank.com/errors/not-found"
            title: "Payment Order Not Found"
            status: 404
            detail: "Payment order not found: 123e4567-e89b-12d3-a456-426614174000"
            instance: "/payment-initiation/payment-orders/123e4567-e89b-12d3-a456-426614174000"
            timestamp: "2025-11-19T15:30:00Z"
    
    InternalServerError:
      description: Error interno del servidor
      content:
        application/problem+json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            type: "https://api.bank.com/errors/internal-error"
            title: "Internal Server Error"
            status: 500
            detail: "An unexpected error occurred"
            instance: "/payment-initiation/payment-orders"
            timestamp: "2025-11-19T15:30:00Z"
```

**En cada endpoint, referenciar las respuestas**:
```yaml
paths:
  /payment-initiation/payment-orders:
    post:
      responses:
        '201':
          description: Orden de pago creada exitosamente
        '400':
          $ref: '#/components/responses/BadRequest'
        '500':
          $ref: '#/components/responses/InternalServerError'
```

#### 7. Tests del GlobalExceptionHandler

**DEBE** existir una suite completa de tests:

```java
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  void shouldReturn404WhenHandlingPaymentOrderNotFoundException() {
    // Given
    UUID orderId = UUID.randomUUID();
    PaymentOrderNotFoundException exception = new PaymentOrderNotFoundException(orderId);

    // When
    ResponseEntity<ProblemDetail> response = handler.handlePaymentOrderNotFound(exception);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(404);
    assertThat(response.getBody().getTitle()).isEqualTo("Payment Order Not Found");
    assertThat(response.getBody().getDetail()).contains(orderId.toString());
  }

  @Test
  void shouldReturn400WhenHandlingInvalidPaymentOrderException() {
    // Given
    InvalidPaymentOrderException exception = new InvalidPaymentOrderException("Invalid data");

    // When
    ResponseEntity<ProblemDetail> response = handler.handleInvalidPaymentOrder(exception);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(400);
  }

  @Test
  void shouldReturn500WhenHandlingGenericException() {
    // Given
    Exception exception = new RuntimeException("Unexpected error");

    // When
    ResponseEntity<ProblemDetail> response = handler.handleGenericException(exception);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(500);
  }
}
```

#### 8. Checklist de Verificación

Antes de considerar completo el manejo de errores, verificar:

```markdown
- [ ] GlobalExceptionHandler existe y está anotado con @RestControllerAdvice
- [ ] Usa ProblemDetail de Spring 6+ (org.springframework.http.ProblemDetail)
- [ ] NO usa ErrorResponse personalizado - solo ProblemDetail nativo
- [ ] Maneja excepciones de dominio personalizadas
- [ ] Maneja errores de validación (WebExchangeBindException)
- [ ] Maneja excepciones genéricas con logging apropiado
- [ ] Todas las respuestas incluyen campos RFC 7807 obligatorios
- [ ] ProblemDetail está documentado en OpenAPI (schema nativo)
- [ ] Todos los endpoints referencian respuestas de error
- [ ] Content-Type es application/problem+json
- [ ] Tests unitarios cubren todos los handlers
- [ ] Tests de integración verifican respuestas de error completas
- [ ] Logs de error incluyen stack traces (solo para errores 500)
- [ ] No se exponen detalles sensibles en mensajes de error
```

#### 9. Buenas Prácticas

**HACER**:
- ✅ Usar `ProblemDetail` de Spring 6+ para compatibilidad nativa
- ✅ Incluir `timestamp` en todas las respuestas de error
- ✅ Usar URIs descriptivos en el campo `type`
- ✅ Loggear errores 500 con stack trace completo
- ✅ Incluir información de contexto útil (IDs, parámetros relevantes)
- ✅ Usar `application/problem+json` como Content-Type
- ✅ Documentar todos los códigos de error en OpenAPI

**NO HACER**:
- ❌ Exponer stack traces en respuestas de error
- ❌ Incluir información sensible (contraseñas, tokens, datos personales)
- ❌ Usar mensajes de error genéricos sin contexto
- ❌ Retornar diferentes formatos de error según el tipo
- ❌ Olvidar documentar errores en OpenAPI
- ❌ Usar códigos de estado HTTP incorrectos
- ❌ Loggear errores de validación (400) como errores críticos

---

## Resumen de Entregables

1. **Repositorio**: Repositorio Git con historial de commits limpio
2. **README.md**: Documentación completa en español
3. **openapi.yaml**: Contrato de API REST en `src/main/resources/api/`
4. **Código Fuente**: Implementación de arquitectura hexagonal
5. **Tests**: Unitarios + Integración (≥80% cobertura)
6. **Reportes de Calidad**: JaCoCo, Checkstyle, SpotBugs
7. **Docker**: Dockerfile + docker-compose.yml
8. **Documentación Técnica**: Carpeta `doc/` con archivos `.md` en español y snake_case
   - `doc/analisis_migracion.md`
   - `doc/decisiones_arquitectura.md`
   - `doc/guia_instalacion.md`
9. **Documentación de IA**: Carpeta `ai/` con archivos en español y snake_case
   - `ai/prompts.md`
   - `ai/decisiones.md`
   - `ai/generaciones/`
10. **Colección Postman**: Actualizada y probada en `doc/postman_collection.json`

---

## Sugerencia de Timeline (3 días)

### Día 1: Análisis y Setup
- Analizar WSDL y requerimientos
- Diseñar especificación OpenAPI
- Configurar estructura del proyecto
- Configurar plugins Maven
- Documentar prompts de IA para fase de análisis

### Día 2: Implementación y Testing
- Implementar arquitectura hexagonal
- Implementar los 3 endpoints
- Escribir tests unitarios
- Escribir tests de integración
- Lograr ≥80% de cobertura
- Documentar prompts de IA para implementación

### Día 3: Calidad, Docker y Documentación
- Ejecutar y corregir issues de Checkstyle/SpotBugs
- Crear Dockerfile y docker-compose
- Probar setup de Docker
- Completar README.md
- Finalizar documentación de IA
- Probar colección Postman
- Verificación final

---

## Comandos de Referencia Rápida

```bash
# Generar código OpenAPI
mvn clean compile

# Ejecutar tests con cobertura
mvn clean test jacoco:report

# Verificación completa (calidad + tests)
mvn clean verify

# Ejecutar aplicación localmente
mvn spring-boot:run

# Construir imagen Docker
docker build -t payment-orders:latest .

# Ejecutar con docker-compose
docker-compose up -d

# Ver logs
docker-compose logs -f payment-orders-service

# Detener contenedores
docker-compose down
```

---

## Recursos de Apoyo

- **Documentación BIAN**: https://bian.org/
- **Especificación OpenAPI**: https://swagger.io/specification/
- **Arquitectura Hexagonal**: https://alistair.cockburn.us/hexagonal-architecture/
- **Docs Spring Boot 3**: https://docs.spring.io/spring-boot/docs/current/reference/html/

---

**Recuerda**: Calidad sobre velocidad. El ejercicio enfatiza arquitectura limpia, testing apropiado y documentación exhaustiva. Usa IA como asistente, pero siempre valida y entiende el código generado.

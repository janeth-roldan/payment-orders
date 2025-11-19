# Reglas Windsurf - Proyecto de Migración Payment Orders

## Contexto del Proyecto
Migración de servicios de pago SOAP legados a API REST alineada con BIAN (Banking Industry Architecture Network) Service Domain Payment Initiation.

**Crítico**: Este es un proyecto de migración SOAP-a-REST para una entidad bancaria. La calidad, seguridad y alineación BIAN son obligatorias.

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
Crear carpeta `ai/` con:

1. **prompts.md**: Todos los prompts usados con IA
   - Prompt para análisis de WSDL
   - Prompt para generación de OpenAPI
   - Prompt para esqueleto de arquitectura hexagonal
   - Prompt para generación de tests

2. **generations/**: Carpeta con fragmentos de código generados por IA
   - Guardar outputs iniciales de IA antes de correcciones manuales

3. **decisions.md**: Documentar correcciones manuales
   - Qué se cambió del output de IA
   - Por qué se cambió
   - Proceso de validación

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

## Resumen de Entregables

1. **Repositorio**: Repositorio Git con historial de commits limpio
2. **README.md**: Documentación completa
3. **openapi.yaml**: Contrato de API REST
4. **Código Fuente**: Implementación de arquitectura hexagonal
5. **Tests**: Unitarios + Integración (≥80% cobertura)
6. **Reportes de Calidad**: JaCoCo, Checkstyle, SpotBugs
7. **Docker**: Dockerfile + docker-compose.yml
8. **Documentación de IA**: Carpeta `ai/` con prompts y decisiones
9. **Colección Postman**: Actualizada y probada

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

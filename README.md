# Transaction Orchestrator

![CI Pipeline](https://github.com/NiGilnRo/transaction-orchestrator/actions/workflows/ci.yml/badge.svg?branch=main)

Microservicio orquestador de transacciones de pago desarrollado con Java 21, Spring Boot 3 y arquitectura hexagonal (Ports & Adapters).

---

## Cómo correr localmente

**Requisitos:** Java 21, Maven 3.8+

### Perfil dev (por defecto)

Usa H2 en memoria — no requiere base de datos externa.

```bash
./mvnw spring-boot:run
```

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| H2 Console | http://localhost:8080/h2-console |
| API Docs | http://localhost:8080/api-docs |

**Credenciales H2:**
- JDBC URL: `jdbc:h2:mem:transactiondb`
- Usuario: `sa` / Contraseña: _(vacía)_

### Perfil prod

Requiere PostgreSQL. Configurar las siguientes variables de entorno antes de ejecutar:

```bash
export DB_URL=jdbc:postgresql://host:5432/transactiondb
export DB_USER=usuario
export DB_PASSWORD=contraseña

./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

> En prod el esquema **no se crea automáticamente** (`ddl-auto: none`). Ejecutar el script SQL manualmente antes del primer arranque:
> ```bash
> psql -U usuario -d transactiondb -f src/main/resources/schema.sql
> ```

---

## Cómo correr con Docker

**Requisitos:** Docker y Docker Compose instalados.

### App + PostgreSQL + SonarQube

```bash
docker-compose up --build
```

Esto levanta 4 contenedores:

| Contenedor | Descripción | Puerto |
|---|---|---|
| `transaction-orchestrator` | La app en perfil prod | 8080 |
| `transaction-db` | PostgreSQL con el schema aplicado automáticamente | 5432 |
| `sonarqube` | Servidor de análisis de calidad | 9000 |
| `sonar-db` | PostgreSQL exclusivo de SonarQube | — |

> El schema.sql se monta directamente en el contenedor de PostgreSQL y se ejecuta al primer arranque.

### Solo app + PostgreSQL (sin SonarQube)

```bash
docker-compose up --build app db
```

### Análisis de calidad con SonarQube

Una vez que SonarQube esté corriendo en `http://localhost:9000`:

1. Ingresar con usuario `admin` / contraseña `admin`
2. Crear un proyecto y generar un token
3. Ejecutar el análisis:

```bash
mvn test sonar:sonar -Dsonar.token=TU_TOKEN
```

El reporte de cobertura de JaCoCo se envía automáticamente a SonarQube.

### Reporte de cobertura local (sin SonarQube)

```bash
mvn test
```

El reporte se genera en `target/site/jacoco/index.html`.

---

## Endpoints disponibles

```
POST /v1/transactions        Crear una transacción de pago
GET  /v1/transactions/{id}   Consultar una transacción por ID
```

### Ejemplo de request

```json
POST /v1/transactions
{
  "client_transaction_id": "ORD-2026-001",
  "amount": 1000000,
  "currency": "COP",
  "country": "CO",
  "payment_method_id": "PSE",
  "webhook_url": "https://mi-sistema.com/webhook",
  "redirect_url": "https://mi-sistema.com/redirect",
  "customer": {
    "document_type": "CC",
    "document_number": "123456789",
    "country_code": "+57",
    "phone": "3001234567",
    "email": "juan@example.com",
    "first_name": "Juan",
    "last_name": "Pérez"
  }
}
```

### Ejemplo de response

```json
HTTP 201
{
  "code": "000",
  "message": "Successful operation",
  "data": {
    "transaction_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "processed_at": "2026-05-28T10:00:00",
    "client_transaction_id": "ORD-2026-001",
    "payment_method_id": "PSE",
    "currency": "COP",
    "country": "CO"
  }
}
```

### Métodos de pago disponibles (catálogo semilla)

| ID | Nombre | Activo |
|---|---|---|
| `PSE` | PSE — Pagos Seguros en Línea | ✅ |
| `CREDIT_CARD` | Tarjeta de Crédito | ✅ |
| `DEBIT_CARD` | Tarjeta Débito | ✅ |
| `CASH` | Efectivo (Corresponsal) | ❌ |

---

## Arquitectura

El proyecto sigue el patrón **Hexagonal (Ports & Adapters)** que aísla la lógica de negocio de cualquier tecnología externa.

![Diagrama de componentes](docs/componentes.png)

> Fuente editable: [`docs/component-diagram.puml`](docs/component-diagram.puml)

### Estructura de carpetas

```
com.nicolasdev.transactionorchestrator
├── domain/
│   ├── model/              Entidades de negocio y enums (Java puro)
│   ├── ports/
│   │   ├── in/             Contratos de entrada (casos de uso)
│   │   └── out/            Contratos de salida (repositorio, proveedor)
│   └── exception/          Excepciones del dominio
├── application/
│   └── usecase/            Implementaciones de los casos de uso
└── infrastructure/
    ├── adapter/
    │   ├── in/rest/        Controllers REST y DTOs
    │   └── out/
    │       ├── persistence/ Adaptadores JPA, mappers, repositorios Spring Data
    │       └── provider/    Adaptador del proveedor de pagos (mock)
    └── config/             Configuración Spring, OpenAPI, manejo de errores
```

---

## Decisiones arquitectónicas

**¿Por qué arquitectura hexagonal?**
Permite cambiar cualquier tecnología externa (base de datos, proveedor de pagos, protocolo de entrada) sin modificar la lógica de negocio. El dominio no depende de Spring, JPA ni ninguna librería externa.

**¿Por qué separar entidades de dominio de entidades JPA?**
Las entidades JPA están acopladas al modelo relacional (`@Entity`, `@ManyToOne`, etc.). Si el dominio usara esas clases directamente, un cambio en la BD requeriría modificar la lógica de negocio. La separación garantiza que cada capa cambie por sus propias razones.

**¿Por qué H2 en desarrollo?**
Elimina la dependencia de infraestructura externa para desarrollar y testear. El `schema.sql` es compatible con PostgreSQL para producción — solo cambia el driver y la URL de conexión.

**¿Por qué el UUID lo genera el caso de uso y no la BD?**
El `transaction_id` es parte del contrato del API — se devuelve en el response y se envía al proveedor. Generarlo en el dominio garantiza que exista antes de persistir, sin depender del resultado del `save()`.

**¿Por qué patrón Strategy en el proveedor de pagos?**
El caso de uso depende de `PaymentProviderPort` (interfaz), no de una implementación concreta. Agregar un nuevo proveedor (PSE real, Stripe) solo requiere crear un nuevo adaptador — el caso de uso no cambia.

---

## Suposiciones

- Cada transacción crea un nuevo registro de cliente. No se implementó deduplicación de clientes por documento.
- `payment_method_id` debe existir en el catálogo de la BD. Los métodos de pago se insertan como datos semilla al iniciar la app.
- El campo `amount` se recibe en centavos como entero (`Long`) para evitar errores de punto flotante.
- El proveedor de pagos siempre responde `APPROVED` (mock). En producción, el status dependería de la respuesta real del proveedor.
- No se implementó autenticación ni autorización en esta versión.

---

## Riesgos identificados

| Riesgo | Impacto | Mitigación sugerida |
|---|---|---|
| Mock del proveedor siempre aprueba | Alto | Implementar adaptador real con manejo de reintentos y timeouts |
| No hay autenticación en el API | Alto | Agregar Spring Security con JWT o API keys |
| H2 en memoria pierde datos al reiniciar | Medio | Usar PostgreSQL desde el inicio en entornos no locales |
| Sin paginación en consultas | Medio | Agregar `Pageable` si se agregan endpoints de listado |
| Sin idempotencia en el POST | Medio | Validar `client_transaction_id` único antes de crear |

---

## Patrones de diseño utilizados

| Patrón | Dónde | Por qué |
|---|---|---|
| **Hexagonal / Ports & Adapters** | Arquitectura global | Aislar dominio de tecnología |
| **Strategy** | `PaymentProviderPort` | Intercambiar proveedores sin modificar el caso de uso |
| **Repository** | `TransactionRepositoryPort` | Abstraer la persistencia del dominio |
| **Mapper** | `TransactionMapper` | Convertir entre modelos de dominio e infraestructura |
| **DTO** | Request / Response | Separar el contrato del API del modelo interno |

---

## Estrategia de calidad

| Herramienta | Propósito |
|---|---|
| **JUnit 5** | Framework de tests unitarios e integración |
| **Mockito** | Mocks para aislar dependencias en tests unitarios |
| **@DataJpaTest** | Tests de integración de repositorios con H2 real |
| **JaCoCo** | Cobertura de código (objetivo: 80%+) |
| **SonarQube** | Análisis estático: bugs, code smells, vulnerabilidades |
| **Checkstyle** | Estilo y convenciones de código |

Los tests unitarios del caso de uso corren sin Spring context — solo Mockito. Esto los hace rápidos y deterministas.

---

## CI/CD

Pipeline implementado con **GitHub Actions** — ver [`.github/workflows/ci.yml`](.github/workflows/ci.yml).

Se dispara automáticamente en cada push o PR a `main` y `develop`:

```
push / PR → Build → Test → JaCoCo Report (artifact)
```

| Etapa | Comando | Qué hace |
|---|---|---|
| **Build** | `mvnw compile` | Verifica que el código compila |
| **Test** | `mvnw verify` | Corre todos los tests y genera reporte JaCoCo |
| **Coverage Report** | `upload-artifact` | Publica el reporte HTML de cobertura como artifact descargable |

**Evolución propuesta del pipeline:**

| Etapa futura | Herramienta |
|---|---|
| Análisis de calidad | SonarCloud integrado al pipeline |
| Build de imagen | Docker build + push a registry |
| Deploy staging | Kubernetes / ECS en cada merge a `develop` |
| Deploy producción | Aprobación manual + deploy en merge a `main` |

Ramas:
- `main` → producción
- `develop` → staging
- `feature/*` → solo CI en cada PR

---

## Scripts SQL

El esquema de base de datos se encuentra en `src/main/resources/schema.sql`.

Para ejecutarlo manualmente en PostgreSQL:

```bash
psql -U usuario -d base_de_datos -f src/main/resources/schema.sql
```

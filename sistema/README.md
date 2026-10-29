# Sistema para Gestión de Universidad — Backend

Microservicio REST en **Spring Boot** (Java 17) para la administración de divisiones académicas y carreras de la universidad. Base de datos **H2** en memoria.

## Requisitos

- Java 17
- Maven (incluye el wrapper `mvnw`, no es necesario instalar Maven)

## Puesta en marcha

```bash
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8081`.

## API — Carreras

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/carreras` | Lista todas las carreras |
| GET | `/api/carreras?estado=BORRADOR` | Lista carreras filtradas por estado |
| GET | `/api/carreras/{id}` | Obtiene una carrera por id |
| POST | `/api/carreras` | Crea una carrera (estado inicial: `BORRADOR`) |
| PUT | `/api/carreras/{id}` | Actualiza una carrera existente |
| DELETE | `/api/carreras/{id}` | Elimina una carrera |
| POST | `/api/carreras/{id}/publicar` | Cambia el estado a `PUBLICADA` |

### Modelo de datos

| Campo | Tipo | Notas |
|---|---|---|
| `id` | `long` | Generado por la base de datos |
| `nombre` | `string` | Nombre de la carrera |
| `facultad` | `string` | Facultad o área |
| `descripcion` | `string` | Descripción y plan de estudios (máx. 1000) |
| `estado` | `string` | `BORRADOR` o `PUBLICADA` |

### Ejemplo de petición

```bash
curl -X POST http://localhost:8081/api/carreras \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ingeniería en Software","facultad":"Tecnologías de la Información","descripcion":"Desarrollo web, móvil y bases de datos."}'
```

## Arquitectura

```
controller  →  service  →  repository  →  entity
   HTTP        lógica de   acceso a      modelo de
   REST        negocio     datos (JPA)   datos
```

- `controller/CarreraController` — endpoints REST, CORS para `http://localhost:5173`
- `service/CarreraService` — operaciones de negocio transaccionales
- `repository/CarreraRepository` — `JpaRepository` + consulta por estado
- `entity/Carrera` — entidad persistida

## Base de datos

H2 en memoria: `jdbc:h2:mem:alumnos` (se reinicia en cada arranque). Consola H2 disponible en `http://localhost:8081/h2-console`.

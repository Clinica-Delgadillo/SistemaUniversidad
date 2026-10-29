# Reporte de Desarrollo (DE)

**Materia:** Desarrollo Web Integral
**Institución:** Universidad Tecnológica de Querétaro
**Equipo:** Clínica Delgadillo (Oscar Daniel, Nuvia Yasmin, Elian Eduardo, Aldair, Estefany Ariadna, Fernanda)
**Repositorio backend:** https://github.com/Clinica-Delgadillo/SistemaUniversidad
**Repositorio frontend:** https://github.com/heraldev/SistemaUniversidadSalon
**Responsable de este documento:** Elian Eduardo Ramírez (renyam2) — capa repository y service del microservicio

---

## 1. Caso de estudio y product backlog

El microservicio implementa el módulo de **administración académica** de la universidad: las **divisiones** y las **carreras** que pertenecen a cada división. A partir del caso de estudio se definió el siguiente backlog, organizado por epic y por historias de usuario con sus criterios de aceptación.

### Epic 01 — Administración académica de divisiones y carreras

#### Rol 1 — Administrador

| ID | Historia de usuario | Criterios de aceptación |
|----|---------------------|--------------------------|
| HU01.1 | Como administrador, quiero **registrar una carrera** asociada a una división, para completar el catálogo académico de la universidad. | 1. Puedo crear una carrera indicando nombre, descripción y división (201). 2. La carrera se guarda con estado inicial BORRADOR. 3. El sistema no permite crear una carrera si la división no existe (409). |
| HU01.2 | Como administrador, quiero **consultar las carreras** del sistema, para conocer el catálogo académico. | 1. Puedo listar todas las carreras (200). 2. Puedo filtrar carreras por estado, por ejemplo BORRADOR (200). 3. Puedo obtener una carrera por su id (200) y recibir 404 si no existe. |
| HU01.3 | Como administrador, quiero **editar una carrera**, para corregir o actualizar su información. | 1. Puedo actualizar nombre, descripción y división de una carrera existente (200). 2. Recibo 404 si intento editar una carrera que no existe. |
| HU01.4 | Como administrador, quiero **publicar una carrera**, para que sea visible para los usuarios generales. | 1. Puedo publicar una carrera (BORRADOR → PUBLICADA) mediante `POST /api/carreras/{id}/publicar` (200). 2. Recibo 404 si la carrera no existe. |
| HU01.5 | Como administrador, quiero **eliminar una carrera**, para quitarla del catálogo académico. | 1. Puedo eliminar una carrera existente (204). 2. Al verificarla después, el sistema devuelve 404. 3. Recibo 404 si intento eliminar una carrera que no existe. |

#### Rol 2 — Usuario general

| ID | Historia de usuario | Criterios de aceptación |
|----|---------------------|--------------------------|
| HU02.1 | Como usuario general, quiero **ver las carreras publicadas**, para elegir mi plan de estudios. | 1. Puedo listar únicamente las carreras con estado PUBLICADA mediante `GET /api/carreras?estado=PUBLICADA` (200). 2. La respuesta incluye la información de cada carrera (nombre, descripción, división). |
| HU02.2 | Como usuario general, quiero **ver el detalle de una carrera**, para conocer su información completa. | 1. Puedo obtener una carrera por su id (200) con nombre, descripción, división y estado. 2. Recibo 404 si la carrera no existe. |

> **Nota:** las divisiones (crear, listar, actualizar y eliminar) son el **prerrequisito** de las historias del rol administrador: una carrera siempre se registra contra una división existente. La capa repository y service de divisiones se implementó en el PR #5.

---

## 2. Arquitectura del microservicio (estado actual)

```
sistema/src/main/java/com/universidad/sistema/
├── SistemaApplication.java
├── controller/
│   ├── CarreraController.java   (delega al service; maneja 404 y 409)
│   └── DivisionController.java  (nuevo en PR #5)
├── dto/
│   ├── CarreraRequest.java
│   ├── CarreraResponse.java
│   ├── DivisionRequest.java
│   └── DivisionResponse.java
├── entity/
│   ├── Carrera.java
│   └── Division.java
├── repository/
│   ├── CarreraRepository.java
│   └── DivisionRepository.java  (nuevo en PR #5)
└── service/
    ├── CarreraService.java       (ampliado en PR #5: valida división existente)
    └── DivisionService.java      (nuevo en PR #5)
```

### Responsabilidad de cada capa

| Capa | Responsabilidad | Reglas cumplidas |
|------|-----------------|------------------|
| entity | Define las tablas y la relación 1-a-many (una división tiene varias carreras). | `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@ManyToOne`, `@OneToMany(mappedBy = "division")`. |
| dto | Define qué información recibe y devuelve la API. | `Request` recibe, `Response` devuelve; sin `@Entity`, sin BD, sin lógica de negocio; campo `divisionId` para identificar la división. |
| repository | Acceso a datos mediante `JpaRepository`. | `CarreraRepository` y `DivisionRepository` extienden `JpaRepository<Entidad, Long>` sin consultas SQL propias. |
| service | Lógica de negocio. | No consulta la BD directamente (usa el repository), no maneja HTTP, verifica que el registro exista antes de actualizar/eliminar, valida que la división exista al crear una carrera y no permite eliminar una división con carreras. |
| controller | Rutas de la API. | Solo delega al service, mapea estados HTTP y no contiene lógica de negocio. |

### Decisión de contrato de la API (acuerdo del equipo)

- La capa de **divisiones** usa los DTO acordados (`DivisionRequest` / `DivisionResponse`).
- La capa de **carreras** devuelve la entidad `Carrera` (formato `id`, `nombre`, `descripcion`, `estado`, `division`). Esta decisión se tomó porque el **frontend ya está enlazado y funcionando** con ese formato: las vistas de `SistemaUniversidadSalon` consumen `carrera.id`, `carrera.nombre`, `carrera.descripcion`, `carrera.estado` y `carrera.facultad` (ver `src/services/adminService.js` en el repositorio frontend). Cambiar el contrato a DTO puro rompería el frontend, por lo que se mantiene el formato actual y se documenta aquí.
- Corrección aplicada en el service: al crear una carrera, el service ahora carga la entidad completa de la división desde el repository (en lugar de usar solo el `id` que llega en el cuerpo), de modo que la respuesta de creación incluye `division.nombre` correctamente.

---

## 3. Endpoints implementados

### Divisiones (PR #5)

| Método | Ruta | Descripción | Estado esperado |
|--------|------|-------------|-----------------|
| GET | /api/divisiones | Listar todas las divisiones | 200 |
| GET | /api/divisiones/{id} | Obtener una división por id | 200 / 404 |
| POST | /api/divisiones | Crear una división | 201 |
| PUT | /api/divisiones/{id} | Actualizar el nombre de una división | 200 / 404 |
| DELETE | /api/divisiones/{id} | Eliminar una división | 204 / 404 / 409 (si tiene carreras) |

### Carreras (PR #1 y #4, ampliado en PR #5)

| Método | Ruta | Descripción | Estado esperado |
|--------|------|-------------|-----------------|
| GET | /api/carreras | Listar carreras | 200 |
| GET | /api/carreras?estado=PUBLICADA | Listar carreras publicadas | 200 |
| GET | /api/carreras/{id} | Obtener una carrera por id | 200 / 404 |
| POST | /api/carreras | Crear una carrera (estado inicial BORRADOR) | 201 / 409 (división inexistente) |
| PUT | /api/carreras/{id} | Actualizar una carrera | 200 / 404 |
| DELETE | /api/carreras/{id} | Eliminar una carrera | 204 / 404 |
| POST | /api/carreras/{id}/publicar | Publicar una carrera (BORRADOR → PUBLICADA) | 200 / 404 |

---

## 4. Flujo de desarrollo (control de versiones)

### Ramas creadas por el equipo (backend)

| Rama | Autor | Contenido | PR |
|------|-------|-----------|----|
| feature/back | Oscar Daniel | Repository CarreraRepository + DataSeeder | PR #1 (cerrado) |
| feature/api-carreras | Nuvia Yasmin | DTOs + READMEs por capa | PR #2 (cerrado) |
| docs/readme-backend | Daniel | READMEs de entity, dto, repository, service y controller | PR #3 (fusionado) |
| feature/carrera-service | Elian Eduardo | CarreraService + controller delegando al service | PR #4 (fusionado) |
| feature/divisiones-repo-service | Elian Eduardo | DivisionRepository, DivisionService, DivisionController, validaciones de negocio, colección Postman | PR #5 (abierto, en revisión) |

### Commits de la capa repository y service (Elian Eduardo)

| Commit | Mensaje |
|--------|---------|
| 39c1e21 | feat(backend): implementa CarreraRepository con JpaRepository |
| b0cd533 | DataSeeder: siembra carreras iniciales si la BD está vacía |
| b662246 | feat(backend): capa de servicio CarreraService, controller delega al service y READMEs por capa |
| b9bd0df | feat(backend): capa repository y service de Divisiones, DivisionController y validaciones de negocio |

### Frontend (heraldev/SistemaUniversidadSalon)

| Participación | Descripción |
|---------------|-------------|
| heraldev | Estructura base del frontend (vistas de administración de carreras y divisiones). |
| Daniel | Mejoras visuales del panel de administración. |
| Elian Eduardo (renyam2) | Enlace frontend-backend: `adminService.js` apuntando a `http://localhost:8081/api/carreras`, PR #1 y PR #2 del repositorio frontend. |

El frontend consume la API con `fetch` y usa los campos `carrera.id`, `carrera.nombre`, `carrera.descripcion`, `carrera.estado` y `carrera.facultad`, por lo que el contrato de la API de carreras se mantuvo en formato de entidad (ver sección 2).

---

## 5. Pruebas por historia de usuario (Postman)

Se generó la colección **`Postman_Sistema_Universidad.json`** (incluida en el repositorio) con todas las peticiones organizadas por historia de usuario. La colección se ejecutó de principio a fin contra la aplicación corriendo (Spring Boot 3.5, Java 17, H2 en memoria, base de datos limpia) y **las 21 peticiones arrojaron el resultado esperado**. Las respuestas capturadas corresponden a la ejecución real de la colección.

### Prerrequisito — Divisiones (rol administrador)

| # | Petición | Cuerpo | Resultado esperado | Resultado obtenido |
|---|----------|--------|--------------------|--------------------|
| 1 | POST /api/divisiones | {"nombre":"Ingeniería"} | 201 + id | 201 {"id":1,"nombre":"Ingeniería"} |
| 2 | POST /api/divisiones | {"nombre":"Ciencias"} | 201 + id | 201 {"id":2,"nombre":"Ciencias"} |
| 3 | GET /api/divisiones | — | 200 lista | 200 [{"id":1,...},{"id":2,...}] |

![Prerrequisito — Divisiones](capturas/postman-prerrequisito.png)

### HU01.1 — Registrar carrera (administrador)

| # | Petición | Cuerpo | Resultado esperado | Resultado obtenido |
|---|----------|--------|--------------------|--------------------|
| 4 | POST /api/carreras | {"nombre":"Computación","descripcion":"Licenciatura en Computación","division":{"id":1}} | 201, estado BORRADOR | 201 {"id":1,"nombre":"Computación","division":{"id":1,"nombre":"Ingeniería"},"estado":"BORRADOR"} |
| 5 | POST /api/carreras | {"nombre":"Matemáticas","descripcion":"Licenciatura en Matemáticas","division":{"id":2}} | 201, estado BORRADOR | 201 {"id":2,"nombre":"Matemáticas","division":{"id":2,"nombre":"Ciencias"},"estado":"BORRADOR"} |
| 6 | POST /api/carreras | {"nombre":"Fantasma","descripcion":"Carrera sin división válida","division":{"id":999}} | 409 (división no existe) | 409 {"error":"No se puede crear la carrera porque la división con id 999 no existe."} |

![HU01.1 — Registrar carrera](capturas/postman-hu01-1.png)

### HU01.2 — Consultar carreras (administrador)

| # | Petición | Resultado esperado | Resultado obtenido |
|---|----------|--------------------|--------------------|
| 7 | GET /api/carreras | 200 lista completa | 200 [Computación, Matemáticas] |
| 8 | GET /api/carreras?estado=BORRADOR | 200 solo borradores | 200 [2 carreras BORRADOR] |
| 9 | GET /api/carreras/1 | 200 detalle | 200 {"id":1,"nombre":"Computación","division":{"id":1,"nombre":"Ingeniería"},"estado":"BORRADOR"} |
| 10 | GET /api/carreras/999 | 404 | 404 |

![HU01.2 — Consultar carreras](capturas/postman-hu01-2.png)

### HU01.3 — Editar carrera (administrador)

| # | Petición | Cuerpo | Resultado esperado | Resultado obtenido |
|---|----------|--------|--------------------|--------------------|
| 11 | PUT /api/carreras/1 | {"nombre":"Computación Avanzada","descripcion":"Licenciatura en Computación Avanzada","division":{"id":1}} | 200 | 200 {"id":1,"nombre":"Computación Avanzada",...} |
| 12 | PUT /api/carreras/999 | {"nombre":"No existe","descripcion":"x","division":{"id":1}} | 404 | 404 |

![HU01.3 — Editar carrera](capturas/postman-hu01-3.png)

### HU01.4 — Publicar carrera (administrador)

| # | Petición | Resultado esperado | Resultado obtenido |
|---|----------|--------------------|--------------------|
| 13 | POST /api/carreras/1/publicar | 200, estado PUBLICADA | 200 {"estado":"PUBLICADA"} |
| 14 | POST /api/carreras/2/publicar | 200, estado PUBLICADA | 200 {"estado":"PUBLICADA"} |
| 15 | POST /api/carreras/999/publicar | 404 | 404 |

![HU01.4 — Publicar carrera](capturas/postman-hu01-4.png)

### HU01.5 — Eliminar carrera (administrador)

| # | Petición | Resultado esperado | Resultado obtenido |
|---|----------|--------------------|--------------------|
| 16 | DELETE /api/carreras/1 | 204 | 204 |
| 17 | GET /api/carreras/1 | 404 (ya eliminada) | 404 |
| 18 | DELETE /api/carreras/999 | 404 | 404 |

![HU01.5 — Eliminar carrera](capturas/postman-hu01-5.png)

### HU02.1 — Ver carreras publicadas (usuario general)

| # | Petición | Resultado esperado | Resultado obtenido |
|---|----------|--------------------|--------------------|
| 19 | GET /api/carreras?estado=PUBLICADA | 200 solo publicadas | 200 [Matemáticas (PUBLICADA, división Ciencias)] |

![HU02.1 — Ver carreras publicadas](capturas/postman-hu02-1.png)

### HU02.2 — Ver detalle de una carrera (usuario general)

| # | Petición | Resultado esperado | Resultado obtenido |
|---|----------|--------------------|--------------------|
| 20 | GET /api/carreras/2 | 200 detalle | 200 {"id":2,"nombre":"Matemáticas","division":{"id":2,"nombre":"Ciencias"},"estado":"PUBLICADA"} |
| 21 | GET /api/carreras/999 | 404 | 404 |

![HU02.2 — Ver detalle de una carrera](capturas/postman-hu02-2.png)

> **Nota para el equipo:** las capturas de la sección 5 corresponden a la ejecución real de la colección `Postman_Sistema_Universidad.json`. Para reproducirlas en Postman: Import → seleccionar el archivo → ejecutar cada petición en el orden indicado → capturar la respuesta (método, URL, estado y cuerpo).

---

## 6. Evidencias del repositorio (capturas)

### 6.1 Repositorio y estructura

![Repositorio](capturas/repo-main.png)

![Paquete del microservicio](capturas/repo-paquete.png)

![Ramas del repositorio](capturas/branches.png)

### 6.2 Commits

![Historial de commits](capturas/commits.png)

![Commit b9bd0df — capa repository y service de Divisiones](capturas/commit-mio.png)

### 6.3 Pull requests del equipo

![PR #1 — CarreraRepository (Oscar Daniel)](capturas/pr1.png)

![PR #2 — DTOs y READMEs por capa (Nuvia Yasmin)](capturas/pr2.png)

![PR #3 — READMEs por capa (Daniel)](capturas/pr3.png)

![PR #4 — CarreraService (Elian Eduardo)](capturas/pr4.png)

![PR #5 — DivisionRepository, DivisionService y validaciones (Elian Eduardo)](capturas/pr5.png)

![PR #5 — archivos modificados](capturas/pr5-files.png)

### 6.4 Código de la capa repository y service

![DivisionRepository.java](capturas/code-divisionrepository.png)

![DivisionService.java](capturas/code-divisionservice.png)

![DivisionController.java](capturas/code-divisioncontroller.png)

![CarreraService.java (ampliado con validación de división)](capturas/code-carreraservice.png)

### 6.5 Entidades y DTOs

![Division.java (entidad)](capturas/code-division-entity.png)

![CarreraRequest.java (DTO)](capturas/code-carrerequest-dto.png)

![CarreraResponse.java (DTO)](capturas/code-carrereresponse-dto.png)

![README de la capa dto (contrato acordado)](capturas/code-dto-readme.png)

### 6.6 Frontend (heraldev/SistemaUniversidadSalon)

![Repositorio frontend](capturas/fe-main.png)

![Commits del frontend](capturas/fe-commits.png)

![PR #1 del frontend](capturas/fe-pr1.png)

![PR #2 del frontend](capturas/fe-pr2.png)

![adminService.js — enlace frontend-backend](capturas/fe-adminservice.png)

![Vista SubirCarrera](capturas/fe-subircarrera.png)

---

## 7. Pendientes

1. **Revisión y fusión del PR #5:** un compañero del equipo debe revisar y aprobar el PR #5 (GitHub no permite auto-revisión). El PR ya incluye un comentario del autor explicando las decisiones de diseño; una vez fusionado a `main`, este reporte se actualizará con el commit de fusión.
2. **Capturas de los demás integrantes:** cada integrante agrega las capturas de su propio código, commits y PRs al reporte de control de versiones del equipo (ya se incluyen los PR #1, #2, #3 y #4 del equipo y el código de la capa repository/service).
3. **Reproducción de las pruebas en Postman:** la colección `Postman_Sistema_Universidad.json` está lista para importarse; las capturas de la sección 5 corresponden a la ejecución real de la colección contra la API.

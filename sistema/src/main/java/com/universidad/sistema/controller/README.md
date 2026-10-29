controller: quí se crean las rutas de la API para recibir las peticiones del frontend o de herramientas como Thunder Client.

## Archivos que debes crear

- `DivisionController.java`
- `CarreraController.java`

## Rutas de divisiones

| Método | Ruta | Función |
|---|---|---|
| GET | /api/divisiones | Consultar todas |
| GET | /api/divisiones/{id} | Consultar una |
| POST | /api/divisiones | Registrar |
| PUT | /api/divisiones/{id} | Actualizar |
| DELETE | /api/divisiones/{id} | Eliminar |

## Rutas de carreras

| Método | Ruta | Función |
|---|---|---|
| GET | /api/carreras | Consultar todas |
| GET | /api/carreras/{id} | Consultar una |
| POST | /api/carreras | Registrar |
| PUT | /api/carreras/{id} | Actualizar |
| DELETE | /api/carreras/{id} | Eliminar |

## Reglas

1. Utilizar `@RestController` en las clases.
2. Utilizar `@RequestMapping` para definir la ruta principal.
3. Utilizar `@GetMapping`, `@PostMapping`, `@PutMapping` y `@DeleteMapping`.
4. Recibir la información mediante los DTO `Request`.
5. Devolver la información mediante los DTO `Response`.
6. Llamar a los métodos de `service` para realizar las operaciones.
7. No realizar consultas directas a la base de datos.
8. No colocar lógica de negocio en los controladores.
9. Utilizar las rutas definidas en este README.


service: aquí se programa la lógica del sistema.

Los servicios utilizan los repositorios para guardar, consultar, actualizar y eliminar información.

## Archivos que debes crear

- `DivisionService.java`
- `CarreraService.java`

## Métodos que deben tener

**DivisionService**

- `listarDivisiones()`
- `obtenerDivision(Long id)`
- `crearDivision(DivisionRequest datos)`
- `actualizarDivision(Long id, DivisionRequest datos)`
- `eliminarDivision(Long id)`

**CarreraService**

- `listarCarreras()`
- `obtenerCarrera(Long id)`
- `crearCarrera(CarreraRequest datos)`
- `actualizarCarrera(Long id, CarreraRequest datos)`
- `eliminarCarrera(Long id)`

## Reglas

1. Utilizar `@Service` en las clases.
2. Acceder a H2 mediante los repositorios.
3. No escribir consultas directamente en esta carpeta.
4. Verificar que un registro exista antes de editarlo o eliminarlo.
5. Al crear una carrera, comprobar que la división exista.
6. No permitir eliminar una división que tenga carreras registradas.
7. Usar los DTO acordados para recibir y devolver información.
8. No modificar las rutas HTTP; eso corresponde a `controller`.
9. Mantener los métodos cortos y fáciles de entender.

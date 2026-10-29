repository: quí se crean las interfaces que permiten consultar, guardar y eliminar información en H2.

## Archivos que debes crear

- `DivisionRepository.java`
- `CarreraRepository.java`

## Reglas

1. Cada entidad debe tener su propio repositorio.
2. Los repositorios deben extender de `JpaRepository`.
3. Utilizar `Long` como tipo de identificador.
4. No escribir consultas SQL si JPA ya permite realizar la operación.
5. No colocar lógica de negocio en esta carpeta.
6. No modificar las entidades ni los controladores.

## Estructura que debes utilizar

**DivisionRepository.java**

```java
public interface DivisionRepository
        extends JpaRepository<Division, Long> {
}
```

**CarreraRepository.java**

```java
public interface CarreraRepository
        extends JpaRepository<Carrera, Long> {
}
```

Operaciones disponibles

Al utilizar `JpaRepository`, tendrás métodos como:

- `findAll()` — Consultar todos.
- `findById()` — Buscar por ID.
- `save()` — Guardar o actualizar.
- `deleteById()` — Eliminar.
- `existsById()` — Comprobar si existe.

entity: aquí se crean las clases que representan las tablas de la base de datos H2.

## Archivos que debes crear

- `Division.java`
- `Carrera.java`

## Campos de cada clase
Division.java

| Campo | Tipo |
|---|---|
| id | Long |
| nombre | String |
| carreras | List<Carrera> |

Carrera.java

| Campo | Tipo |
|---|---|
| id | Long |
| nombre | String |
| descripcion | String |
| division | Division |

## Reglas

1. Utilizar `@Entity` para identificar las entidades.
2. Utilizar `@Id` y `@GeneratedValue` para generar los identificadores.
3. Las tablas deben llamarse `divisiones` y `carreras`.
4. Una división puede tener muchas carreras: `@OneToMany`.
5. Una carrera pertenece a una sola división: `@ManyToOne`.
6. La relación debe utilizar la columna `division_id` en la tabla `carreras`.
7. No crear tablas adicionales para esta relación.
8. Evitar que la relación genere referencias circulares al devolver JSON.
9. No colocar consultas ni lógica de negocio en estas clases.

## Importante
Estas clases serán utilizadas por las carpetas `repository`, `service` y `controller`.

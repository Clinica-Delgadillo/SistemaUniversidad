dto: Para qué sirve esta carpeta?

Aquí se crean las clases que definen qué información recibe y devuelve la API.
Sirven para evitar enviar directamente las entidades de la base de datos.

Archivos que debes crear

- `DivisionRequest.java`
- `DivisionResponse.java`
- `CarreraRequest.java`
- `CarreraResponse.java`

## Campos

**DivisionRequest**

- nombre: String

**DivisionResponse**

- id: Long
- nombre: String

**CarreraRequest**

- nombre: String
- descripcion: String
- divisionId: Long

**CarreraResponse**

- id: Long
- nombre: String
- descripcion: String
- divisionId: Long
- divisionNombre: String

## Reglas

1. Los archivos `Request` reciben información del usuario.
2. Los archivos `Response` devuelven información al usuario.
3. No incluir anotaciones de tablas como `@Entity`.
4. No consultar directamente la base de datos.
5. No colocar lógica de negocio.
6. Utilizar los nombres de campos definidos anteriormente.
7. Utilizar `divisionId` para identificar la división de una carrera.


## Importante

Los DTO serán utilizados por las carpetas `controller` y `service`.

No cambiar los nombres de los campos sin avisar al equipo.
# API: cambios y alcance de la Parte 2

## Actualización compatible con Java 21 y el esquema existente
- Spring Boot 3.5.16; anotaciones JPA y validación migradas a Jakarta.
- Hibernate 6 usa `GenerationType.IDENTITY` en las cinco entidades para respetar los `AUTO_INCREMENT` de MariaDB. No se crean secuencias adicionales ni se altera la base existente.
- Overrides coordinados de Jackson 2.21.8, Tomcat 10.1.61, Log4j 2.26.1 y MariaDB Connector/J 3.5.11. Consultar el informe SCA del repositorio para avisos residuales y su evaluación contextual.
- La contraseña de la base debe proporcionarse en `DB_PASSWORD`; se eliminó la clave predeterminada del código.

## Cambios de código y contrato HTTP
- Los cuerpos HTTP usan DTOs con campos explícitos y validación Jakarta; no se enlazan entidades persistentes directamente. Las rutas públicas conservan sus nombres.
- Los CRUD devuelven 400 ante identificadores o datos inválidos y 404 cuando no existe el registro. Un alta no acepta un ID que pudiera sobrescribir registros existentes.
- Se eliminan comparaciones `obj.equals(null)` y se usa inyección por constructor.
- Los fallos de persistencia devuelven un mensaje genérico, sin detalles SQL internos.
- Registro de usuario: BCrypt con coste 12, contraseña de 8 a 72 bytes UTF-8; no se guarda texto plano. El rol de un nuevo registro se fija a `Usuario` y no se acepta del cliente. Actualizar el perfil conserva el rol existente.
- Login: consulta parametrizada por correo y comprobación del hash. Credenciales incorrectas devuelven 401. El frontend debe manejar ese estado, no tratarlo como fallo de conexión.
- Los campos `contrasena` aceptan entrada donde corresponde, pero nunca se serializan en las respuestas de usuarios o listados.

## Datos anteriores y límites explícitos
No se han leído, migrado ni cambiado contraseñas de la base real. Las contraseñas anteriores almacenadas en claro ya no permiten iniciar sesión; las cuentas necesitan restablecimiento mediante un proceso de administración autorizado, o registro nuevo en el entorno de práctica. No se añadió una conversión silenciosa ni una comparación alternativa en texto plano.

La API original no tiene un sistema completo de sesión/token ni autorización de cada operación. Esta entrega no presenta el login como un mecanismo completo de autorización de CRUD ni afirma que el producto esté listo para exponerse en Internet. Ese límite debe registrarse como riesgo real, no como falso positivo.

## Pruebas y cobertura
La suite completa incluye pruebas HTTP de CRUD y login, entradas inválidas, inexistencia, fallos de persistencia y no exposición de contraseñas; contratos JSON de entidades/DTOs; BCrypt; y persistencia con H2 en modo MariaDB. El perfil `test` utiliza exclusivamente una base en memoria, crea y elimina su esquema y comprueba IDs generados, fechas y consultas parametrizadas. No escribe en la MariaDB del usuario.

La prueba `contextoSeguroSinSseNiXslt` inspecciona el contexto MVC real para asegurar que no hay vistas/resolutores XSLT, rutas `/**`, retornos SSE ni productores `text/event-stream`. Es una guarda de las condiciones contextuales evaluadas en el informe SCA; no declara que la versión de Spring esté fuera de los rangos afectados.

JaCoCo aplica un mínimo de 100% al contador METHOD. La única exclusión es `com/mx/cbtis/gestor/Ejercicio5MyApiGestorApplication.class`, el arranque de Spring sin lógica de dominio. Se verifica el contexto real en integración, sin escribir una prueba que replique únicamente la llamada a `SpringApplication.run`. No se excluyen modelos, controladores, DTOs ni PasswordService; los porcentajes efectivos y resultados se toman del reporte generado después de `mvn verify`.

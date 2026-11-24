# Guion del despliegue — 3 a 4 minutos

## 1. Aplicación pública

**Mostrar:** la URL del cliente, la página inicial y el formulario de acceso. Registrar una cuenta ficticia o entrar con una cuenta de demostración. Mostrar la pantalla de proyectos y una consulta.

**Decir:** «La aplicación está formada por un cliente web Java, una API Java y una base de datos. El navegador abre el cliente; el cliente consulta la API y la API trabaja con TiDB. Estoy usando datos de demostración.»

## 2. Servicios Docker en Render

**Mostrar:** los dos servicios del proyecto gestion-procesos-software, su plan Free y sus ejecuciones Live. Abrir el commit de cada despliegue y una parte del registro de construcción.

**Decir:** «Creé dos servicios independientes en Render. Cada uno construye su imagen a partir del Dockerfile del repositorio. La primera etapa utiliza Maven y Java 21 para compilar. La segunda ejecuta el JAR con Java 21 y un usuario sin privilegios. El puerto lo proporciona Render y la comprobación de salud utiliza /healthz.»

**Mostrar:** API_BASE_URL en el cliente y únicamente los nombres de las variables de la API. Mantener ocultos los valores secretos.

**Decir:** «Retiré las direcciones locales de las llamadas Java. Ahora la dirección de la API se configura desde el entorno. La URL, el usuario y la contraseña de la base se guardan en Render; la contraseña no está en GitHub.»

## 3. TiDB y adaptación del esquema

**Mostrar:** la vista Overview de gestion-procesos-db, el plan Starter y el gasto limitado a cero. Abrir db/schema-tidb.sql, sin mostrar el panel de contraseñas.

**Decir:** «La base está en TiDB Starter. Comprobé la conexión cifrada y la verificación del certificado. Importé el esquema en una base nueva, sin copiar datos personales. La colación anterior de MariaDB no era compatible con esta instancia; preparé un esquema identificado para TiDB con UTF-8 y conservé el esquema original.»

## 4. Imágenes del sitio y problema de integración

**Mostrar:** optimizacion-imagenes.json y las referencias WebP en las hojas de estilo o plantillas.

**Decir:** «Preparé trece recursos WebP. Los originales sumaban aproximadamente 287 MB y las alternativas ocupan menos de un megabyte. Los GIF convertidos usan un fotograma estático, por lo que pierden la animación. Los videos no se comprimieron.»

**Mostrar:** el método verificarUsuario y la prueba loginNoReutilizaLaLongitudDelCuerpoDeLaApi.

**Decir:** «Durante la integración, la API respondía correctamente, pero el cliente reenviaba los encabezados de esa respuesta después de modificar su contenido. Cambié el cliente para que genere una respuesta nueva. La prueba comprueba que no conserve una longitud de contenido ajena y que no devuelva la contraseña.»

## 5. Evidencias y entrega

**Mostrar:** VALIDACION_DESPLIEGUE.md, los JSON de comprobación pública, las capturas, los Dockerfile y la rama featured/despliegue.

**Decir:** «Estas evidencias identifican las URLs, los commits y las comprobaciones del despliegue. Los reportes de Jenkins de la Parte 2 conservan sus propios builds y fechas; esta entrega añade la ejecución de la aplicación en la nube. El plan gratuito puede suspender los servicios cuando no se usan y tardar en reactivarlos.»

No mostrar contraseñas, tokens ni archivos privados de pruebas. Leer los resultados finales del documento de validación y no presentar una compilación iniciada como un despliegue terminado.
# Demostración adicional: editar un proyecto

En «Mis Proyectos», abre Editar en un proyecto de demostración. Cambia el nombre y la descripción, elige un fondo y pulsa Guardar. Recarga la página y muestra que los cambios permanecen y que solo existe una tarjeta de ese proyecto.

Puedes decir: «También corregí la edición. Antes, el formulario intentaba crear un proyecto con un identificador que ya existía. Ahora actualiza el mismo registro. Corregí el campo del fondo y las rutas de los colores. Lo comprobé con pruebas automatizadas y guardando cambios desde esta pantalla. Al recargar se conserva la información».


# Despliegue verificado — 23 de noviembre de 2026

| Servicio Docker | URL pública | Estado observado | Commit desplegado |
| --- | --- | --- | --- |
| gestion-procesos-frontend | [https://gestion-procesos-frontend.onrender.com](https://gestion-procesos-frontend.onrender.com) | Live | 61ff730 |
| gestion-procesos-api | [https://gestion-procesos-api.onrender.com](https://gestion-procesos-api.onrender.com) | Live | 256f1ec |

Base: **TiDB Cloud Starter**, instancia gestion-procesos-db, base dbgestor, AWS Oregon, gasto máximo **$0/mes**. La conexión verifica el certificado TLS. Se importaron seis tablas en una base nueva con el esquema adaptado a utf8mb4_bin, sin migrar datos personales. Las cuentas utilizadas en la comprobación son ficticias.

## Funcionamiento comprobado

**13 comprobaciones públicas aprobadas**, registradas en [HTTP](evidencias/comprobacion-http.json) e [integración](evidencias/comprobacion-integracion.json): salud del cliente y API, página inicial, registro, login correcto, contraseña incorrecta con HTTP 401, ausencia del valor de contraseña en las respuestas, consulta de proyectos y registro mediante el formulario del cliente. La prueba integrada final se recogió a las 2026-11-23T21:06:52.000201+00:00 (UTC).

El acceso desde el navegador llegó a **Mis Proyectos**. Ver [aplicación después del login](evidencias/aplicacion-proyectos.png), [cliente en Render](evidencias/render-cliente-live.png) y [API en Render](evidencias/render-api-live.png). Los enlaces de administración requieren acceso a la cuenta de Render; las URLs públicas permiten abrir la aplicación.

Siete comprobaciones adicionales de [edición en el navegador](evidencias/comprobacion-edicion.json) verifican la carga del modal, el selector de fondo, el guardado de cambios, el ID conservado sin duplicados, la persistencia al recargar y la imagen cargada. Ver [captura de edición](evidencias/edicion-proyecto-verificada.png). Total: **20 comprobaciones públicas aprobadas**.

## Pruebas Java de esta actualización

La API ejecutó **57 pruebas**, sin fallos, errores ni omisiones. El cliente ejecutó **DeploymentConfigurationTests y ProyectosClienteTests: 11 pruebas seleccionadas**, todas aprobadas. Comprueban la URL remota y que el login no reutilice Content-Length de la API al generar su respuesta. Los XML y resúmenes están en evidencias/pruebas-api y evidencias/pruebas-cliente.

Los builds Jenkins #4, sus 123 pruebas y la cobertura de la Parte 2 pertenecen al commit histórico que identifica aquella entrega. Se conservan en su paquete. Las comprobaciones de este despliegue tienen su alcance y sus fechas propios; no se declara una nueva medición del 100% de cobertura con las pruebas seleccionadas.

## Incidencias resueltas

TiDB rechazó latin1_swedish_ci. Se creó db/schema-tidb.sql con utf8mb4 y utf8mb4_bin, conservando el esquema de MariaDB para el entorno local. Las comparaciones en la base nueva usan esa colación binaria.

La API respondía correctamente, pero el cliente devolvía la ResponseEntity recibida después de modificar el objeto. Los encabezados de encuadre de la respuesta original podían dejar de corresponder al cuerpo serializado del cliente y el proxy devolvía HTTP 502. Se reconstruye la respuesta con ResponseEntity.ok(usuario), conservando la eliminación de la contraseña y dejando que el cliente genere sus encabezados. Se añadió una regresión con una longitud de contenido upstream incompatible. La prueba pública completa pasó después del cambio.

El login limita la conexión a diez segundos y la lectura a noventa, para permitir la reactivación del plan gratuito sin una espera indefinida. El registro de fallos conserva tipo y causa de la conexión, sin imprimir credenciales ni cuerpos de solicitudes.

La edición enviaba un ID existente a /addProyecto. Ahora utiliza /updateProyecto y conserva el ID. Se corrigió el campo editar_fondo, el destino del selector compartido, el ID del título del modal y las rutas de colores incompletas. Tres nuevas regresiones comprueban edición sin duplicados, ID inválido y proyecto eliminado.

## Recursos y archivos de entrega

Se convirtieron trece recursos: **286,996,080 bytes originales → 472,662 bytes WebP**. Los GIF convertidos utilizan el primer fotograma estático y un máximo de 1920×1080. Se actualizan referencias y se excluyen los originales del JAR. Los dos videos permanecen sin comprimir. [Detalle por archivo](optimizacion-imagenes.json).

[Instrucciones](README.md), [estado y revisiones](evidencias/servicios.json) y [configuración](configuracion/). El proyecto completo está en [GitHub](https://github.com/agustn134/ProyectoGestionProcesosSoftware/tree/featured/despliegue).

## Límites y disponibilidad

Ambos servicios utilizan Free y pueden suspenderse por inactividad. La aplicación conserva los límites de autorización CRUD y los avisos de dependencias documentados en la Parte 2; utilizar datos de demostración. Este despliegue no añade sesiones ni permisos completos. Las credenciales se almacenaron en el servicio autorizado de Render y no se incluyen en las evidencias.

Docker Desktop del equipo no logró volver a iniciar después de recuperar el entorno local. Jenkins y SonarQube locales requieren recuperar ese motor; sus reportes anteriores se conservan en los paquetes. La comprobación pública de Render y TiDB está adjunta.

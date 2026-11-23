# Despliegue de los dos microservicios

Rama `featured/despliegue`. Las evidencias históricas de la Parte 2 corresponden a su commit y a los builds #4, no prueban automáticamente este despliegue.

## Cambios

Cada servicio tiene un Dockerfile de varias etapas: Maven 3.9.9 con Java 21 compila el JAR y una imagen JRE 21 lo ejecuta con un usuario sin privilegios. Las pruebas se ejecutan por separado; el empaquetado Docker utiliza `-DskipTests`. `PORT` permite recibir el puerto asignado por Render y `/healthz` comprueba que Spring atiende HTTP. La API valida el esquema durante el arranque.

El cliente recibe `API_BASE_URL`, sin barra final. Para ejecutar los servicios en contenedores separados debe apuntar a la API, no a localhost. El perfil `tidb` utiliza el conector MySQL administrado por Spring Boot, `DB_URL`, `DB_USER` y `DB_PASSWORD`. La conexión debe verificar el certificado con `sslMode=VERIFY_IDENTITY`.

TiDB no admite la colación `latin1_swedish_ci` del esquema original en esta instancia. `db/schema-tidb.sql` conserva tablas, columnas y tipos, usando `utf8mb4` y `utf8mb4_bin`. Se importa solo en una base nueva y vacía; no migra cuentas ni datos personales de MariaDB. Los puertos 4000 de TiDB y 3306 de MariaDB pertenecen a servicios distintos.

## Render

Crear dos **Web Services**, plan **Free**, región Oregon, rama `featured/despliegue`:

| Dato | API | Cliente |
| --- | --- | --- |
| Nombre | gestion-procesos-api | gestion-procesos-frontend |
| Root Directory | Ejercicio5MyAPIGestor | Ejercicio5Cliente |
| Dockerfile Path | ./Dockerfile | ./Dockerfile |
| Docker Build Context | . | . |
| Health Check Path | /healthz | /healthz |
| Variables | SPRING_PROFILES_ACTIVE=tidb, DB_URL, DB_USER, DB_PASSWORD | API_BASE_URL con URL HTTPS de la API |

No incluir contraseñas en Git, capturas ni documentos. Usar las variables del panel de Render. No confundir una compilación iniciada con un servicio Live ni con una prueba completa de registro y login. Las instancias Free se suspenden al quedar inactivas y comparten la cuota mensual del espacio de trabajo.

## Arranque local completo

Configurar DB_PASSWORD y DB_ROOT_PASSWORD fuera de Git. Desde la raíz ejecutar:

```sh
docker compose -f compose.app.yaml up --build -d
```

Cliente: http://localhost:8080. API: http://localhost:8081/healthz. La base de esta composición no publica puertos y utiliza un volumen independiente; no sustituye la base local existente ni la de CI.

## Recursos multimedia

`optimizacion-imagenes.json` registra tamaños antes/después de las conversiones a WebP. Los GIF decorativos convertidos se representan por su primer fotograma estático, con un máximo de 1920×1080; se actualizan las referencias HTML, CSS y JavaScript. Los originales permanecen en Git pero no se incluyen en el contexto Docker cuando tienen una alternativa WebP. Los dos videos originales no se comprimen: el Dockerfile resuelve los objetos LFS públicos si la plataforma entrega sus punteros, y falla si la descarga falla.

## Límites

La aplicación conserva el límite de autorización CRUD documentado en la Parte 2. Usar datos de demostración. El despliegue no corrige por sí mismo esa limitación ni los avisos de dependencias. Las cuentas heredadas con contraseñas en claro requieren restablecimiento; no se modifican automáticamente.

La documentación final debe anexar URLs efectivas, commit, estado Live, resultado de /healthz y comprobaciones de registro/login. No se declaran esos resultados hasta verificarlos.

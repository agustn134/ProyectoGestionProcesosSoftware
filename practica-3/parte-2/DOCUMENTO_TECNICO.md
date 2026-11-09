# Práctica 3 — Parte 2: pruebas, seguridad y tres pipelines

## Alcance y organización

Esta entrega corresponde a la segunda mitad de la práctica: pruebas automatizadas de los métodos evaluables, cobertura verificable, análisis de vulnerabilidades con clasificación y atención, y pipelines independientes de frontend, backend y base de datos. El guion permite demostrar las ejecuciones y explicar lo aprendido; el estudiante debe grabar la demostración.

El trabajo se encuentra en `featured/parte-2`. `main` conserva la Parte 1; `develop` se utiliza como punto de integración. Las ramas `featured/parte-1` y `featured/upgrade-java21` conservan referencias del estado entregado. Ver [organización de ramas](https://github.com/agustn134/ProyectoGestionProcesosSoftware/blob/featured/parte-2/RAMAS.md). No se fusionó la Parte 2 automáticamente en las ramas estables.

## Correspondencia con la rúbrica

| Criterio | Máximo | Evidencia |
| --- | ---: | --- |
| Pruebas completas de métodos y casos relevantes | 0.30 | Suites ejecutadas, resultados Surefire y casos documentados |
| JaCoCo y evidencia verificable de cobertura | 0.20 | HTML/XML por servicio y contadores METHOD |
| Escaneo excluyendo target | 0.20 | Alcance/configuración Sonar, inventarios CycloneDX, OSV y Gitleaks |
| Clasificación y atención de incidencias | 0.20 | Reportes iniciales, comparación final, correcciones y revisión contextual |
| Tres pipelines funcionales | 0.40 | Frontend, backend y base CI con resultados y artefactos |
| Video y explicación del aprendizaje | 0.20 | [Guion para grabar](GUION_VIDEO.md), pantallas y resultados identificados |
| Total máximo | 1.50 | La calificación la asigna el profesor |

Los resultados concretos, números de build, commits y contadores están en [VALIDACION.md](VALIDACION.md). Esta documentación diferencia objetivos configurados de resultados efectivamente ejecutados.

## Pruebas y cobertura

Se sustituyó la selección limitada de seis pruebas MVC por servicio por la ejecución de la suite completa con `mvn clean verify`, sin `-Dtest`. Se prueban los controladores, la transferencia y serialización de datos, las validaciones, las respuestas de error y las operaciones de autenticación modificadas. El contexto de la API utiliza H2 en los tests locales, sin credenciales ni escrituras en la base de trabajo.

JaCoCo 0.8.12 prepara el agente, produce HTML/XML y ejecuta `check` con contador METHOD y mínimo 1.0. No se excluyen controladores, modelos, servicios ni métodos privados de negocio. Se excluye únicamente el punto de arranque de cada aplicación:

- Cliente: `Ejercicio5ClienteApplication.class`.
- API: `com/mx/cbtis/gestor/Ejercicio5MyApiGestorApplication.class`.

Son clases de infraestructura cuyo `main` delega el arranque en Spring, no métodos de negocio evaluables. Los contextos Spring sí se prueban. La exclusión de cobertura no elimina esos archivos del análisis de seguridad. Los números finales de métodos incluidos/excluidos y los porcentajes se consultan en los XML adjuntos; 100% de métodos no implica 100% de ramas ni demuestra por sí mismo calidad de las aserciones.

Surefire 3.2.5 publica casos, fallos, errores y omisiones. Las pruebas deben comprobar resultados observables: estado y contenido HTTP, valores serializados, llamadas de persistencia esperadas, rechazo de entradas inválidas, hashes BCrypt y no exposición de contraseñas. No se cuentan pruebas omitidas como aprobadas.

## Correcciones de código y contraseñas

El análisis inicial de la API detectó 18 vulnerabilidades `java:S4684`: las solicitudes se enlazaban directamente con entidades persistentes. Se separaron entradas DTO y entidades, controlando qué campos se trasladan a persistencia. La ausencia de esos hallazgos debe verificarse en el análisis final, no inferirse solamente por cambiar el nombre de una clase.

También existían 15 bugs `java:S2159` por `objeto.equals(null)`, que no comprueba correctamente null y puede lanzar una excepción, y cinco observaciones `java:S6813` por inyección de campos. Se reemplazaron por comprobaciones/validación explícita e inyección mediante constructor.

La API deja de comparar contraseñas en texto plano en una consulta SQL. Los nuevos registros utilizan BCrypt y el login compara contra el hash. La contraseña no se incluye en las respuestas JSON. El registro no acepta que un cliente se otorgue un rol privilegiado. Se retiró la contraseña predeterminada de la configuración de producción: debe definirse `DB_PASSWORD` fuera del código.

**Compatibilidad de datos anteriores:** no se ejecutó una migración ni actualización de contraseñas sobre la base real. Las contraseñas anteriores almacenadas en claro requieren restablecimiento para utilizar el nuevo login. No se presenta ese dato heredado como corregido en producción. Las cuentas de Jenkins y SonarQube son independientes de las cuentas de la aplicación.

La aplicación original no tiene una sesión/token ni autorización completa de cada operación CRUD. Esta entrega no afirma haber añadido ese sistema de control de acceso; se registra como límite arquitectónico real, no como falso positivo. El restablecimiento de cuentas antiguas requiere un proceso de administración autorizado o un registro nuevo en el entorno de práctica; no se añadió un flujo de recuperación ni se cambió la base real.

## Dependencias y compatibilidad

La migración inicial a Java 21 no corregía las vulnerabilidades de las bibliotecas antiguas. Se actualizó Spring Boot a 3.5.16 en los dos servicios y se fijaron revisiones adicionales obtenidas de Maven Central y contrastadas con OSV:

| Biblioteca/BOM | Revisión fijada |
| --- | --- |
| Spring Boot | 3.5.16 |
| Jackson BOM | 2.21.8 |
| Tomcat | 10.1.61 |
| Log4j 2 BOM | 2.26.1 |
| MariaDB Connector/J | 3.5.11 |

La API migra los imports de persistencia de `javax.persistence` a `jakarta.persistence`. La generación de IDs utiliza IDENTITY para conservar las columnas `AUTO_INCREMENT` existentes y evitar que Hibernate 6 requiera tablas de secuencias nuevas. Se conserva Java 21, Maven 3.9.9 y el SonarScanner obligatorio 5.5.0.6356. Las suites deben validar la compatibilidad de estos cambios.

El inventario inicial completo contiene 155 componentes únicos y 215 avisos por componente e identificador. No equivalen a 215 vulnerabilidades independientes: existen identificadores y componentes compartidos. Los reportes originales se conservan íntegros; la comparación final permite comprobar qué avisos desaparecen con cada cambio de versión.

## Alcance de los análisis

| Herramienta | Insumo y propósito | Límite |
| --- | --- | --- |
| SonarQube | Fuente Java y pruebas, revisión estática y métricas | No sustituye pruebas dinámicas ni SCA |
| CycloneDX 2.9.1 + OSV 2.7.0 | Grafo resuelto por Maven, incluidas dependencias de prueba | Aviso por versión no demuestra una explotación concreta |
| Gitleaks 8.30.1 | Fuentes del módulo en el estado actual, reglas predeterminadas y redacción total | No representa un análisis del historial Git |
| Comprobaciones locales CI | Tres reglas explícitas sobre null, contraseña predeterminada y SQL global | No es un SAST completo |

`target`, `ci-reports` y las carpetas de evidencias quedan fuera del análisis como código fuente; no se excluyen `src/main` ni `src/test`. Leer bytecode o XML JaCoCo de target como insumos no equivale a escanear sus archivos generados como código. El SBOM se escribe en `ci-reports/<módulo>/bom.json` y es el único insumo de OSV; así no se vuelven a analizar los POM originales ni bibliotecas empaquetadas dentro de target.

Los scripts distinguen hallazgos de errores de herramienta/red. Un inventario vacío o una ejecución fallida no se declara limpia. Los reportes de secretos se redactan y no se guardan tokens/contraseñas en Git.

## Dos avisos contextuales de Spring

Spring 6.2.19 es la última revisión pública disponible de esa línea al comprobar esta entrega. Los avisos CVE-2026-47890 y CVE-2026-47884 conservan una versión afectada. El proveedor publica la corrección 6.2.20 mediante soporte comercial y 7.0.9 en la línea OSS; no se oculta esa limitación.

- [CVE-2026-47890, aviso del proveedor](https://spring.io/security/cve-2026-47890/): requiere enviar fragmentos de vista por Server-Sent Events y datos controlados por un atacante. El proveedor lo clasifica LOW, mientras que la base consultada por OSV puede mostrar CRITICAL.
- [CVE-2026-47884, aviso del proveedor](https://spring.io/security/cve-2026-47884/): requiere XsltView y un mapping `/**` que deduzca el nombre de vista. El proveedor lo clasifica MEDIUM; el aviso de la base de terceros puede mostrar CRITICAL.

La aplicación actual no utiliza esas funciones. Se comprueba en fuente y con una prueba del contexto MVC llamada `contextoSeguroSinSseNiXslt`. La revisión contextual se limita al paquete/versión/identificadores exactos, vence el 7 de diciembre de 2026 y falla si se introducen las funciones afectadas, se omite/falla la prueba o aparece un aviso no revisado. Ver `ci/contextual-advisories.json` y los resultados finales.

No se llama “biblioteca corregida” ni “cero avisos” a este resultado. Los dos avisos y su severidad original permanecen en el JSON. Se registra `VERSION_AFFECTED_CONDITIONS_NOT_OBSERVED`, con las condiciones no observadas, referencias y acción futura. Antes de introducir esas funciones o desplegar fuera del entorno de práctica, debe reevaluarse la migración a una línea corregida.

## Tres pipelines y base aislada

Los trabajos `Practica3-Parte2-Frontend`, `Practica3-Parte2-Backend` y `Practica3-Parte2-BaseDatos` hacen checkout de `featured/parte-2`, muestran versiones y publican sus resultados de calidad. Los dos primeros ejecutan suite completa, check de cobertura, revisión de dependencias/secretos y Sonar. La subida del análisis no se confunde con su procesamiento: se espera SUCCESS del servidor y se valida la revisión Git antes de consultar el Quality Gate.

El tercer pipeline aplica y valida `db/schema.sql` en `admin-proyectos-db-ci`, base `ci_gestor`, usuario `ci_runner`. El contenedor no publica puertos y usa tmpfs; no comparte datos con `admin-proyectos-mariadb`. El usuario CI tiene permisos sobre esa base, sin privilegios globales ni acceso al socket Docker desde Jenkins.

El módulo `db-tests` rechaza otra URL/usuario antes de reconstruir tablas. Comprueba esquema, tipos, estado inicial de la secuencia, repetición de importación y operaciones JDBC con transacciones/rollback. No se afirma integridad referencial que el SQL no declara: los índices KEY del esquema original no son claves foráneas.

El operador aprovisiona el contenedor de CI; el pipeline verifica importación, consultas y restricciones de esa instancia. No se presenta como un pipeline que crea automáticamente una infraestructura productiva. La base de trabajo y sus registros no se modificaron para estas pruebas.

## Reproducción y evidencia

1. Cambiar a `featured/parte-2`, instalar Git LFS y descargar los recursos del cliente.
2. Configurar JDK21 y Maven3 3.9.9 y construir la imagen de [Jenkins](https://github.com/agustn134/ProyectoGestionProcesosSoftware/blob/featured/parte-2/ci/README.md).
3. Configurar `sonar-token` y `ci-db` en Jenkins sin incluir sus valores en Git.
4. Preparar la base CI conforme a [db-tests](https://github.com/agustn134/ProyectoGestionProcesosSoftware/blob/featured/parte-2/db-tests/README.md).
5. Ejecutar los tres trabajos y consultar pruebas, reportes y commits de esas mismas ejecuciones.

La [validación final](VALIDACION.md) y el paquete de evidencias identifican los resultados reales. Para abrir JaCoCo HTML, descomprimir el ZIP conservando todos sus recursos. El [guion](GUION_VIDEO.md) indica qué mostrar y qué explicar; no sustituye la grabación del video.



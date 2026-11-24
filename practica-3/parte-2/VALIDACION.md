# Resultados verificados de la Parte 2

Rama **featured/parte-2**. Código evaluado: [8a6491f](https://github.com/agustn134/ProyectoGestionProcesosSoftware/commit/8a6491fe29df05f275c76635fe864edbf750ddb3). Los tres trabajos evaluaron ese mismo commit. Los commits posteriores que anexan evidencia no se confunden con el código ejecutado.

## Jenkins, pruebas y cobertura
| Pipeline | Build | Resultado | Pruebas aprobadas | Fallos/errores | Omitidas |
| --- | --- | --- | ---: | ---: | ---: |
| Frontend | [#4](http://localhost:8090/job/Practica3-Parte2-Frontend/4/) | SUCCESS | 58 | 0 | 0 |
| Backend | [#4](http://localhost:8090/job/Practica3-Parte2-Backend/4/) | SUCCESS | 57 | 0 | 0 |
| Base de datos | [#4](http://localhost:8090/job/Practica3-Parte2-BaseDatos/4/) | SUCCESS | 8 | 0 | 0 |

Total: **123 pruebas Java/JDBC aprobadas**. Además, cada pipeline ejecutó nueve pruebas de las reglas de revisión contextual de CI. Java 21.0.9, Maven 3.9.9 y ciclo `clean verify`, sin seleccionar clases con `-Dtest`. La consola de los servicios registra el check de JaCoCo aprobado.

| Servicio | Métodos evaluables cubiertos/total | Métodos | Líneas | Ramas |
| --- | ---: | ---: | ---: | ---: |
| Ejercicio5Cliente | 97/97 | 100% | 330/336 | 42/44 |
| Ejercicio5MyAPIGestor | 144/144 | 100% | 324/324 | 58/58 |

Se excluyó únicamente la clase de arranque Spring de cada servicio, con dos métodos por clase. No se excluyen modelos, controladores, DTOs, servicios ni lógica de negocio. El 100% se refiere a los métodos Java incluidos; no mide JavaScript ni equivale automáticamente a calidad de las aserciones. DB contiene solo pruebas JDBC, sin clases productivas que medir con JaCoCo.

Reportes: [cliente HTML](reportes/cliente/jacoco/index.html), [cliente XML](reportes/cliente/jacoco/jacoco.xml), [API HTML](reportes/api/jacoco/index.html), [API XML](reportes/api/jacoco/jacoco.xml), resultados Surefire de [cliente](reportes/cliente/surefire/), [API](reportes/api/surefire/) y [DB](reportes/db/surefire/). Abrir HTML después de descomprimir el ZIP.

## Seguridad y atención de hallazgos
| Módulo | Componentes OSV | Avisos OSV conservados | Bloqueantes no revisados | Secretos Gitleaks | Resultado de política |
| --- | ---: | ---: | ---: | ---: | --- |
| Ejercicio5Cliente | 69 | 2 | 0 | 0 | PASS_WITH_CONTEXTUAL_EXCEPTIONS |
| Ejercicio5MyAPIGestor | 93 | 2 | 0 | 0 | PASS_WITH_CONTEXTUAL_EXCEPTIONS |
| db-tests | 9 | 0 | 0 | 0 | PASS |

La medición inicial contiene 155 componentes únicos y 215 avisos por componente/ID. Los CSV adjuntos conservan severidad, paquete, versión y acción de cada hallazgo. No se llaman “vulnerabilidades independientes” a todos esos avisos ni se declara que una versión afectada sea segura por falta de un exploit demostrado.

Se conservan dos avisos de Spring en cada servicio: CVE-2026-47890 y CVE-2026-47884. Son revisiones contextuales limitadas a Spring MVC 6.2.19, con condiciones verificadas y vencimiento 2026-12-07. La versión sigue afectada. No se usó una supresión silenciosa ni se cambió el reporte a cero: el JSON conserva los avisos, `suppressedFindings` está vacío y la política falla ante otra vulnerabilidad o al invalidarse sus condiciones.

SonarScanner 5.5.0.6356 terminó, el servidor procesó ambos análisis y los dos Quality Gates quedaron OK. El check adicional exige cero vulnerabilidades reportadas por Sonar y código analizado no vacío. Esto es independiente de los avisos de dependencias de OSV. La exportación del operador contrasta la revisión del historial con el build, el registro SCM y la tarea CE.

La API tenía 18 hallazgos S4684, 15 bugs S2159 y cinco observaciones S6813; consultar [clasificación de código](evidencias/clasificacion-codigo.csv) y los JSON antes/después para su contraste. Los reportes de código fuente y dependencias no se presentan como una auditoría exhaustiva ni como autorización completa de CRUD.

## Base de datos y evidencia portable
La conexión de pruebas se limita a `admin-proyectos-db-ci:3306/ci_gestor`, usuario `ci_runner`. Se probaron destino permitido/rechazado, recreación e importación repetida del esquema, seis tablas, columnas/tipos, semilla técnica, CRUD con apóstrofes, rollback y rechazo de PK duplicada. La estructura permanece en CI; los registros de prueba se revierten. No se modificó la base de trabajo ni se afirmó que el SQL, sin la preparación del test, fuese idempotente.

Ver [aislamiento de DB](evidencias/aislamiento-db.json), [metadatos completos](evidencias/resultados.json), [clasificación de dependencias](evidencias/clasificacion-dependencias.csv), [código](evidencias/clasificacion-codigo.csv) y [documento técnico](DOCUMENTO_TECNICO.md).

Las ejecuciones interrumpidas por el cambio de nombre de ramas o por el reinicio de Docker no se cuentan como aprobadas. Los enlaces anteriores identifican únicamente los builds finales. Las ligas localhost requieren el equipo de la práctica; las copias del ZIP y GitHub permiten revisar los reportes sin acceso a ese Jenkins.

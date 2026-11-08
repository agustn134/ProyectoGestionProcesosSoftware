# Validación final: Práctica 3, Parte 1

## Compilación y pruebas en Jenkins
| Servicio | Ejecución | Resultado | Pruebas aprobadas | Fallos | Omitidas | Código analizado |
| --- | --- | --- | ---: | ---: | ---: | --- |
| Ejercicio5Cliente | [#4](http://localhost:8090/job/Unidad2-Cliente/4/) | SUCCESS | 6 | 0 | 0 | [commit](https://github.com/agustn134/ProyectoGestionProcesosSoftware/commit/00f74e27d624623b66cef282aa64149c59c55bb1) |
| Ejercicio5MyAPIGestor | [#2](http://localhost:8090/job/Unidad2-API/2/) | SUCCESS | 6 | 0 | 0 | [commit](https://github.com/agustn134/ProyectoGestionProcesosSoftware/commit/69ca27b6c12882f56fbcae675000207c29aa861e) |

Jenkins 2.541.3 ejecuta Java 21.0.9. Las herramientas son JDK21 y Maven3 (Maven 3.9.9). Los dos pipelines compilaron con `release 21`, ejecutaron seis pruebas MVC cada uno y archivaron JAR, JaCoCo y XML de Surefire. Son doce pruebas aprobadas en total; no se ejecutó la suite completa ni la integración con MariaDB.

Configuración y trazabilidad: [herramientas](herramientas-jenkins.json), [datos consolidados](jenkins-parte1.json), [consola cliente](consola-cliente.txt), [consola API](consola-api.txt). Los timestamps originales están en UTC y se incluye su conversión a America/Mexico_City.

## JaCoCo: métodos de todo el servicio
| Servicio | Build | Métodos cubiertos/total | Cobertura de métodos | Exclusiones |
| --- | --- | ---: | ---: | --- |
| Ejercicio5Cliente | #4 | 24/98 | 24.49% | Ninguna |
| Ejercicio5MyAPIGestor | #2 | 20/103 | 19.42% | Ninguna |

Los contadores se extrajeron de `METHOD` en los XML adjuntos. No se alcanzó el 100%; no se excluyeron clases para elevar el porcentaje. Pruebas aprobadas y cobertura total son condiciones diferentes. La ampliación corresponde a la Parte 2.

Reportes: [cliente HTML](../reportes/cliente/jacoco/index.html), [cliente XML](../reportes/cliente/jacoco/jacoco.xml), [API HTML](../reportes/api/jacoco/index.html), [API XML](../reportes/api/jacoco/jacoco.xml). Descargar el ZIP y conservar los recursos junto a los HTML. [Pruebas del cliente](pruebas-cliente.json) y [pruebas de la API](pruebas-api.json) identifican los resultados publicados por Jenkins.

## SonarScanner y procesamiento en SonarQube
SonarScanner for Maven **5.5.0.6356** se ejecutó en ambos builds con `ANALIZAR_SONAR=true`. Se comprobó `ANALYSIS SUCCESSFUL`, el estado SUCCESS del procesamiento en el servidor y la coincidencia entre la revisión de Git del build y la revisión del análisis. El servidor es SonarQube Community Build 26.10.0.133078.

| Servicio | Análisis procesado | Quality Gate | Bugs reportados | Vulnerabilidades reportadas | Code smells | Coverage de Sonar |
| --- | --- | --- | ---: | ---: | ---: | ---: |
| Ejercicio5Cliente | SUCCESS | OK | 0 | 0 | 0 | 30.9% |
| Ejercicio5MyAPIGestor | SUCCESS | OK | 15 | 18 | 5 | 15.0% |

Son clasificaciones de la herramienta, pendientes de revisión manual; no se presentan como incidencias confirmadas o corregidas. Un Quality Gate OK refleja las condiciones configuradas y no borra los hallazgos globales. El pipeline no incorpora `waitForQualityGate`; se consultó el estado por separado. La métrica Coverage de Sonar incluye líneas/condiciones y **no equivale al porcentaje de métodos de JaCoCo**.

Evidencias: [cliente](sonar-cliente.json) y [API](sonar-api.json), con IDs de análisis, revisión, métricas y estado. Dashboards locales: [cliente](http://localhost:9000/dashboard?id=ejercicio5-cliente) y [API](http://localhost:9000/dashboard?id=ejercicio5-api). No se ha ejecutado en esta entrega un escaneo de dependencias/SCA, una revisión completa de vulnerabilidades ni su corrección.

## Cuatro POM y recursos del cliente
Se verificó Java 17 en los dos originales, Java 21 en los dos actualizados y coincidencia SHA-256 entre cada actualizado y el POM activo. Ambos incluyen Surefire 3.2.5, JaCoCo 0.8.12 y SonarScanner 5.5.0.6356. Los hashes están en el JSON consolidado.

El cliente #3 falló en Checkout al intentar instalar hooks de LFS deshabilitados por Jenkins. La corrección se verificó en el cliente #4: `git lfs install --local --skip-repo` seguido de `git lfs pull`. Se comprobaron los cinco archivos completos y sus tamaños dentro del JAR, no solo el resultado de compilación. Consultar [incidencia](incidencia-lfs.md), [estado LFS](lfs-cliente.txt) y [entradas del JAR](jar-recursos-cliente.txt).

## Alcance pendiente
Los tres pipelines de la práctica completa (incluido base de datos), las pruebas completas, la cobertura máxima de métodos y la clasificación/atención de seguridad de la Parte 2 no se declaran terminados. El guion adjunto demuestra únicamente la Parte 1 y explica estas limitaciones.

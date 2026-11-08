# Práctica 3 — Parte 1: actualización tecnológica

**Proyecto:** Gestión del Proceso de Desarrollo de Software  
**Repositorio de entrega:** [ProyectoGestionProcesosSoftware](https://github.com/agustn134/ProyectoGestionProcesosSoftware)  
**Alcance:** actualización de dos servicios a Java 21 y verificación de su construcción en Jenkins.

## 1. Objetivo y alcance de la entrega

Actualizar los dos servicios a Java 21, configurar las herramientas que utiliza Jenkins, conservar los POM anteriores y explicar los cambios con evidencia de compilación, pruebas y cobertura. La entrega sigue los siete criterios de la Parte 1, cuyo valor máximo es **1.50 puntos**.

La evaluación completa de la práctica también contempla tres pipelines, pruebas completas, análisis de seguridad y video. Esos resultados requieren sus propias ejecuciones y evidencias en la Parte 2. Esta entrega no presenta esas actividades como concluidas ni afirma cobertura de métodos del 100%.

## 2. Servicios y funcionamiento

| Servicio | Responsabilidad | Ejecución local |
| --- | --- | --- |
| `Ejercicio5Cliente` | Interfaz web con Spring MVC y Thymeleaf; solicita operaciones a la API. | Puerto `8080` |
| `Ejercicio5MyAPIGestor` | API de gestión de proyectos, tareas, usuarios, comentarios y asignaciones; utiliza JPA y MariaDB. | Puerto `8081` |
| MariaDB | Persistencia de la aplicación. | Puerto local `3297` hacia `3306` del contenedor |

Los dos servicios se conservan en carpetas independientes de un único repositorio, según la organización elegida para esta entrega. Cada carpeta tiene su POM, su código y su Jenkinsfile. Las llamadas actuales del cliente apuntan a la API en `localhost:8081`; la API usa la base `dbgestor` en `localhost:3297` cuando se ejecuta desde Windows. Ejecutar las aplicaciones dentro de contenedores requeriría adaptar esos nombres de conexión al entorno de Docker.

El proyecto de origen se reconoce en el README del repositorio. La estructura funcional y las modificaciones que ya existían antes de esta actividad no se presentan como desarrollos nuevos de esta Parte 1.

## 3. Criterios y evidencia

| Criterio de la Parte 1 | Máximo | Evidencia de esta entrega |
| --- | ---: | --- |
| Ambos servicios migrados y compilando con Java 21 | 0.30 | POM actualizados, versiones registradas y resultados de Jenkins |
| Jenkins configurado para utilizar JDK 21 | 0.20 | Herramienta `JDK21`, controlador con Java 21 y evidencia de versiones |
| Maven compatible con Java 21 y configurado en Jenkins | 0.15 | Herramienta `Maven3`, versión `3.9.9`, ejecutada con Java 21 |
| Dos POM originales y dos actualizados, identificados | 0.25 | Cuatro archivos XML enlazados en la sección 4 |
| SonarScanner, JaCoCo y Surefire incorporados y configurados | 0.20 | Versiones explícitas, configuración, reportes de pruebas/cobertura y estado de Sonar documentado |
| Documento técnico con explicación y justificación | 0.20 | Este documento |
| Dos ligas GitHub con cambios y proyectos utilizables | 0.20 | Enlaces individuales a ambos servicios, instrucciones de uso y Jenkinsfile |
| **Total máximo de la rúbrica** | **1.50** | La calificación corresponde al docente; esta tabla identifica evidencia, no asigna puntos obtenidos. |

La evidencia final de ejecuciones y del análisis que efectivamente se haya realizado está concentrada en [validacion.md](evidencias/validacion.md) y [jenkins-parte1.json](evidencias/jenkins-parte1.json). Para valorar los plugins debe distinguirse entre **declaración**, **configuración**, **ejecución** y **reporte producido**.

## 4. POM originales y actualizados

| Servicio | POM original | POM actualizado |
| --- | --- | --- |
| Cliente | [Original, Java 17](pom-originales/Ejercicio5Cliente-pom-java17.xml) | [Actualizado, Java 21](pom-actualizados/Ejercicio5Cliente-pom-java21.xml) |
| API | [Original, Java 17](pom-originales/Ejercicio5MyAPIGestor-pom-java17.xml) | [Actualizado, Java 21](pom-actualizados/Ejercicio5MyAPIGestor-pom-java21.xml) |

**Procedencia de los originales:** son copias exactas del estado local de los POM al iniciar la actualización a Java 21. Ya contenían Surefire y SonarScanner. No se generaron quitando cambios del POM actualizado, no se reconstruyeron a partir de una versión supuesta y no se presentan como archivos recuperados de un commit original de Git.

Los POM activos se encuentran en [el cliente](https://github.com/agustn134/ProyectoGestionProcesosSoftware/blob/main/Ejercicio5Cliente/pom.xml) y [la API](https://github.com/agustn134/ProyectoGestionProcesosSoftware/blob/main/Ejercicio5MyAPIGestor/pom.xml). Las copias actualizadas de la evidencia deben coincidir con ellos.

## 5. Cambios y justificación

| Elemento | Estado inicial | Estado actualizado | Justificación |
| --- | --- | --- | --- |
| `java.version` de ambos servicios | `17` | `21` | Establecer la versión de Java requerida por la actividad. |
| Maven Compiler Plugin | Configuración heredada de Spring Boot | `3.13.0`, con `release=21` | Hacer explícito el compilador y generar código para Java 21 en ambos servicios. |
| JaCoCo Maven Plugin | No declarado | `0.8.12` | Incorporar una versión compatible con Java 21 y producir cobertura mediante las pruebas. |
| Maven Surefire Plugin | `3.2.5` | Se conserva `3.2.5` | Ejecutar las pruebas y generar los reportes; su presencia ya era parte del estado inicial. |
| Sonar Maven Plugin | `5.5.0.6356` | Se conserva `5.5.0.6356` | Mantener exactamente la versión exigida. Su declaración no sustituye la evidencia de un análisis. |
| JDK indicado por los Jenkinsfile | `JDK17` | `JDK21` | Ejecutar la construcción con el JDK que corresponde a los POM actualizados. |
| Runtime del controlador Jenkins | Java 17 | Temurin Java 21 | Alinear el entorno del controlador con la actualización y conservar su configuración. |
| Herramienta Maven en Jenkins | `Maven3` | `Maven3`, versión `3.9.9` | Utilizar una versión de Maven compatible y verificar con qué JDK se ejecuta. |
| Jenkins | `2.541.3` | Se conserva `2.541.3` | Cambiar Java sin introducir una actualización adicional del servidor Jenkins. |
| Spring Boot del cliente | `3.2.5` | Se conserva `3.2.5` | Verificar la actualización a Java 21 sin ampliar el alcance a otra migración de framework. |
| Spring Boot de la API | `2.7.17` | Se conserva `2.7.17` | Esta versión documenta compatibilidad con Java 21; no se realiza una migración adicional de `javax.persistence` a Jakarta. |

El requisito `release=21` indica al compilador la plataforma de destino. **No instala un JDK ni cambia el Java con el que se ejecuta Maven.** `JAVA_HOME` y el `PATH` determinan las herramientas disponibles en la terminal; Jenkins también debe resolver correctamente las instalaciones nombradas en el bloque `tools` de cada pipeline.

Deben comprobarse por separado el runtime del controlador de Jenkins y el JDK de construcción del agente. En la configuración local se utilizan el mismo entorno y la ruta `/opt/java/openjdk`, pero esa coincidencia no se debe asumir para otros agentes.

Maven tiene una numeración independiente de Java. Maven `3.9.9` no necesita llamarse “21”: la comprobación relevante es que se ejecute con un JDK compatible y compile los proyectos para Java 21.

## 6. Configuración de Jenkins y Maven

En **Administrar Jenkins > Tools** se usan estos nombres, que deben coincidir exactamente con los Jenkinsfile:

| Herramienta | Nombre en Jenkins | Configuración |
| --- | --- | --- |
| JDK | `JDK21` | `JAVA_HOME=/opt/java/openjdk`; sin instalación automática en la imagen utilizada |
| Maven | `Maven3` | Instalador Apache Maven, versión `3.9.9` |

El código reproducible de la configuración está en [ci/](https://github.com/agustn134/ProyectoGestionProcesosSoftware/tree/main/ci). Se conservó el volumen `jenkins_home` para mantener los trabajos y la configuración. El contenedor anterior de Java 17 quedó detenido como respaldo y no debe ejecutarse simultáneamente sobre el mismo volumen.

Los trabajos usan **Pipeline from SCM**, el repositorio de entrega, la rama `main` y estos Script Path:

- `Ejercicio5Cliente/Jenkinsfile`
- `Ejercicio5MyAPIGestor/Jenkinsfile`

Los pipelines hacen `checkout scm`, muestran las versiones, construyen cada carpeta con `clean verify`, publican resultados de pruebas y conservan sus artefactos configurados. Sus comandos `sh` requieren un agente Linux.

Las compilaciones iniciales de ambos trabajos terminaron en `SUCCESS`. Las ejecuciones que integran la evidencia específica de esta Parte 1, sus números y su resultado deben consultarse en [la validación final](evidencias/validacion.md). Los enlaces a los trabajos permiten seleccionar la ejecución correspondiente:

- [Jenkins: Unidad2-Cliente](http://localhost:8090/job/Unidad2-Cliente/)
- [Jenkins: Unidad2-API](http://localhost:8090/job/Unidad2-API/)

Estas ligas de Jenkins son locales al equipo de la práctica; el profesor puede consultar las copias de evidencia del repositorio y del paquete entregable sin acceder a ese servidor.

## 7. Plugins: configuración y ejecución

### Surefire `3.2.5`

Se declara en ambos POM con versión explícita. Se ejecuta en la fase `test` y genera los reportes XML y de texto en `target/surefire-reports/`. Jenkins publica los XML JUnit para consultar casos, fallos y errores.

La validación inicial ejecutó seis pruebas MVC del cliente y seis de la API, todas sin fallos ni errores. Los reportes adjuntos permiten comprobar cuáles se ejecutaron; esta cantidad no se debe confundir con una suite completa de toda la aplicación.

### JaCoCo `0.8.12`

La ejecución `prepare-agent` prepara el agente antes de las pruebas. La ejecución `report`, asociada a `verify`, lee los datos registrados y genera:

- `target/site/jacoco/index.html`: reporte para consultar en un navegador.
- `target/site/jacoco/jacoco.xml`: reporte estructurado de contadores de cobertura.

Las copias entregables se organizan fuera de `target`, en [reportes/cliente/](reportes/cliente/) y [reportes/api/](reportes/api/). Dentro de cada carpeta se incluyen los reportes disponibles de JaCoCo y Surefire de las ejecuciones identificadas por la evidencia.

### SonarScanner for Maven `5.5.0.6356`

Ambos POM conservan este bloque obligatorio:

```xml
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>5.5.0.6356</version>
</plugin>
```

El análisis no se ejecuta automáticamente al declarar el plugin ni por terminar `clean verify`. Se invoca de forma explícita, mediante `sonar:sonar` o el nombre completo del objetivo. Por ejemplo, desde un entorno autenticado y configurado:

```bash
mvn -B -ntp -f Ejercicio5Cliente/pom.xml org.sonarsource.scanner.maven:sonar-maven-plugin:5.5.0.6356:sonar
mvn -B -ntp -f Ejercicio5MyAPIGestor/pom.xml org.sonarsource.scanner.maven:sonar-maven-plugin:5.5.0.6356:sonar
```

El host de SonarQube, las claves de proyecto y la autenticación deben estar configurados en ese entorno. El token se proporciona mediante credenciales, sin guardarlo en el POM, el código, los comandos del documento ni las evidencias. Los Jenkinsfile incluyen el parámetro `ANALIZAR_SONAR` para controlar esa etapa.

**Estado de verificación:** SonarScanner 5.5.0.6356 se ejecutó en cliente #4 y API #2. Ambos análisis fueron procesados con SUCCESS y sus Quality Gates se consultaron por separado. Los IDs, revisiones, métricas y hallazgos se incluyen en [validacion.md](evidencias/validacion.md). No se declara una revisión completa de seguridad.

## 8. Cobertura observada y límites de la validación

La medición base de la actualización, obtenida de los contadores `METHOD` de JaCoCo, es:

| Servicio | Métodos cubiertos | Métodos totales | Cobertura de métodos |
| --- | ---: | ---: | ---: |
| Cliente | 24 | 98 | 24.49% |
| API | 20 | 103 | 19.42% |

No se configuraron exclusiones de cobertura. Los porcentajes reflejan que estas pruebas se concentran en operaciones de proyectos y dejan otros métodos y clases sin recorrer. Por ejemplo, un modelo puede tener cobertura completa y aun así el servicio completo conservar una cobertura baja.

**No se alcanzó el 100% de métodos del criterio general para máxima calificación.** La ejecución satisfactoria de doce pruebas no significa que todos los métodos estén cubiertos. Tampoco un porcentaje elevado demuestra por sí mismo que las aserciones sean suficientes o correctas. La ampliación de las pruebas requiere considerar el comportamiento de cada operación y sus casos de error, sin excluir código para aparentar cobertura total.

Si una nueva ejecución cambia estos contadores, debe identificarse su número y consultarse su reporte. No mezclar resultados de pruebas de un build con cobertura generada por otro.

## 9. Reproducir la construcción

### Requisitos

JDK 21, Maven `3.9.9`, Git y Git LFS. Para ejecutar la aplicación o las pruebas que levantan el contexto completo de la API se requieren también MariaDB, el esquema y las credenciales apropiadas.

### Obtener el código

```bash
git clone https://github.com/agustn134/ProyectoGestionProcesosSoftware.git
cd ProyectoGestionProcesosSoftware
git lfs install --local
git lfs pull
java -version
mvn -version
```

Verificar que `JAVA_HOME` apunta al JDK 21. Git LFS es necesario para descargar los videos y GIF grandes reales antes de generar el JAR del cliente; los archivos de referencia de LFS no contienen el recurso multimedia completo.

### Repetir la selección de pruebas usada en Jenkins

Desde la raíz del repositorio:

```bash
mvn -B -ntp -f Ejercicio5Cliente/pom.xml clean verify -Dtest=ProyectosClienteTests
mvn -B -ntp -f Ejercicio5MyAPIGestor/pom.xml clean verify -Dtest=ProyectosControllerTests
```

`-Dtest` limita qué clases de prueba ejecuta Surefire. Estos comandos reproducen la selección de seis pruebas MVC por servicio de la validación inicial; no ejecutan automáticamente todas las pruebas existentes. Utilizan dependencias simuladas y no validan operaciones reales contra la base de datos.

Para intentar la suite sin esa selección:

```bash
mvn -B -ntp -f Ejercicio5Cliente/pom.xml clean verify
mvn -B -ntp -f Ejercicio5MyAPIGestor/pom.xml clean verify
```

La prueba `contextLoads` de la API necesita una MariaDB accesible con el esquema correcto y credenciales válidas. Su ejecución y cualquier suite ampliada deben documentarse aparte de la selección anterior.

### Base de datos y arranque local

En el equipo usado para la actividad, la base existente se inicia con:

```bash
docker start admin-proyectos-mariadb
```

No iniciar otro contenedor sobre el mismo puerto `3297`, borrar los datos existentes ni importar nuevamente el esquema sobre esa base. Para una instalación nueva, el README explica el archivo `.env`, `compose.yaml` y el esquema [db/schema.sql](https://github.com/agustn134/ProyectoGestionProcesosSoftware/blob/main/db/schema.sql), que no incorpora registros personales de usuarios. La API debe recibir `DB_PASSWORD` en su entorno con la clave configurada para MariaDB.

Después de compilar, ejecutar en terminales separadas:

```bash
java -jar Ejercicio5MyAPIGestor/target/Ejercicio5MyAPIGestor-0.0.1-SNAPSHOT.jar
java -jar Ejercicio5Cliente/target/Ejercicio5Cliente-0.0.1-SNAPSHOT.jar
```

Abrir `http://localhost:8080`. Esta instrucción permite utilizar los servicios; no sustituye una prueba integral del sistema.

## 10. Evidencia adjunta y consulta

| Evidencia | Ubicación |
| --- | --- |
| Dos POM originales | [pom-originales/](pom-originales/) |
| Dos POM actualizados | [pom-actualizados/](pom-actualizados/) |
| Estado final y observaciones de verificación | [evidencias/validacion.md](evidencias/validacion.md) |
| Datos de las ejecuciones de Jenkins | [evidencias/jenkins-parte1.json](evidencias/jenkins-parte1.json) |
| Reportes del cliente | [reportes/cliente/](reportes/cliente/) |
| Reportes de la API | [reportes/api/](reportes/api/) |
| Orden de la demostración y guion del video | [GUION_VIDEO.md](GUION_VIDEO.md) |
| Configuración reproducible de Jenkins | [ci/](https://github.com/agustn134/ProyectoGestionProcesosSoftware/tree/main/ci) |

Para consultar los HTML, descargar el paquete entregable, descomprimirlo conservando su estructura y abrir el `index.html` del reporte correspondiente. GitHub permite inspeccionar los archivos, pero su vista de código no ejecuta el reporte HTML como una página web.

## 11. Dos ligas de los servicios y trazabilidad

- **Cliente:** https://github.com/agustn134/ProyectoGestionProcesosSoftware/tree/main/Ejercicio5Cliente
- **API:** https://github.com/agustn134/ProyectoGestionProcesosSoftware/tree/main/Ejercicio5MyAPIGestor
- **Historial de commits:** https://github.com/agustn134/ProyectoGestionProcesosSoftware/commits/main/

Se entregan dos ligas de los proyectos dentro de un solo repositorio, según la organización autorizada para la actividad. El historial permite distinguir la actualización de Java 21, los ajustes de Jenkins y la incorporación posterior de evidencias. Las modificaciones funcionales que ya existían al iniciar la actividad no se atribuyen a esta migración tecnológica.

## 12. Fuentes técnicas

- [Spring Boot 2.7.17: requisitos del sistema](https://docs.spring.io/spring-boot/docs/2.7.17/reference/html/getting-started.html#getting-started.system-requirements).
- [JaCoCo 0.8.12: publicación oficial](https://github.com/jacoco/jacoco/releases/tag/v0.8.12).
- [Maven: opción release del Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/examples/set-compiler-release.html).
- [Surefire: selección de pruebas](https://maven.apache.org/surefire/maven-surefire-plugin/examples/single-test.html).



## 13. Ajustes de evidencia de esta Parte 1
Se amplió el archivo de artefactos de ambos Jenkinsfile para conservar JaCoCo completo y los XML de Surefire junto con los JAR. La credencial de análisis de Sonar está almacenada en Jenkins como `sonar-token`, no en Git; el token creado para la práctica caduca el 7 de diciembre de 2026 y debe renovarse si se vuelve a analizar después de esa fecha.

En la repetición se observó que `git lfs pull` avisaba que Git LFS no estaba inicializado para ese checkout. El primer intento con `git lfs install --local` falló porque Jenkins deshabilita los hooks mediante `/dev/null`. Se corrigió con `git lfs install --local --skip-repo`: configura los filtros sin instalar hooks, y después se ejecuta `git lfs pull`. La evidencia final del cliente debe corresponder a la ejecución posterior a esa corrección; un build previo exitoso no era prueba suficiente de que los recursos multimedia estuvieran incorporados correctamente.

## Reportes de consulta directa
- Cliente: [JaCoCo HTML](reportes/cliente/jacoco/index.html), [JaCoCo XML](reportes/cliente/jacoco/jacoco.xml), [Surefire XML](reportes/cliente/surefire/TEST-com.mx.cbtis.controller.ProyectosClienteTests.xml).
- API: [JaCoCo HTML](reportes/api/jacoco/index.html), [JaCoCo XML](reportes/api/jacoco/jacoco.xml), [Surefire XML](reportes/api/surefire/TEST-com.mx.cbtis.gestor.controller.ProyectosControllerTests.xml).
- SonarQube: [evidencia del cliente](evidencias/sonar-cliente.json) y [evidencia de la API](evidencias/sonar-api.json). Los dashboards locales están en http://localhost:9000/dashboard?id=ejercicio5-cliente y http://localhost:9000/dashboard?id=ejercicio5-api.

Abrir los HTML después de descomprimir el ZIP; conservar todas las subcarpetas. La cobertura de métodos de JaCoCo no es la métrica Coverage de SonarQube.

Documentación de la integración empleada: https://docs.sonarsource.com/sonarqube-server/analyzing-source-code/ci-integration/jenkins-integration/global-setup


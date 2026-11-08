# Práctica 3 — Parte 1

Entrega de la actualización de los dos servicios a Java 21, configuración de JDK y Maven en Jenkins, POM comparativos, explicación técnica y evidencias de pruebas y cobertura.

## Abrir la entrega

1. Leer el [documento técnico](DOCUMENTO_TECNICO.md).
2. Consultar el [estado final de verificación](evidencias/validacion.md) y los [datos de Jenkins](evidencias/jenkins-parte1.json).
3. Revisar los cuatro POM de la tabla siguiente.
4. Consultar los reportes de [cliente](reportes/cliente/) y [API](reportes/api/).
5. Usar el [guion del video](GUION_VIDEO.md) para preparar la demostración.

## Cuatro POM identificados

| Servicio | Original, Java 17 | Actualizado, Java 21 |
| --- | --- | --- |
| Cliente | [POM original](pom-originales/Ejercicio5Cliente-pom-java17.xml) | [POM actualizado](pom-actualizados/Ejercicio5Cliente-pom-java21.xml) |
| API | [POM original](pom-originales/Ejercicio5MyAPIGestor-pom-java17.xml) | [POM actualizado](pom-actualizados/Ejercicio5MyAPIGestor-pom-java21.xml) |

Los originales son copias del estado local al iniciar la actualización; ya incluían SonarScanner y Surefire. El documento explica su procedencia y las diferencias.

## Código y Jenkins

| Servicio | Código GitHub | Trabajo Jenkins local |
| --- | --- | --- |
| Cliente | [Ejercicio5Cliente](https://github.com/agustn134/ProyectoGestionProcesosSoftware/tree/main/Ejercicio5Cliente) | [Unidad2-Cliente](http://localhost:8090/job/Unidad2-Cliente/) |
| API | [Ejercicio5MyAPIGestor](https://github.com/agustn134/ProyectoGestionProcesosSoftware/tree/main/Ejercicio5MyAPIGestor) | [Unidad2-API](http://localhost:8090/job/Unidad2-API/) |

Ambos servicios están en un solo repositorio. Los enlaces de Jenkins requieren acceso al equipo de la práctica; para consulta externa se adjuntan evidencias en esta carpeta. El [README principal](https://github.com/agustn134/ProyectoGestionProcesosSoftware/blob/main/README.md) contiene requisitos y comandos para utilizar los proyectos. El [historial de commits](https://github.com/agustn134/ProyectoGestionProcesosSoftware/commits/main/) permite revisar los cambios.

## Consultar los reportes HTML

Descargar el ZIP entregable y descomprimirlo conservando todas sus carpetas. Abrir el `index.html` de JaCoCo desde el equipo local; mantener junto a él sus archivos de estilos y recursos. La vista de archivos de GitHub muestra el código HTML, pero no presenta el reporte como un sitio web.

La cobertura base de métodos es **24/98 (24.49%) en el cliente** y **20/103 (19.42%) en la API**, sin exclusiones de cobertura. Se seleccionaron seis pruebas MVC por servicio en la validación inicial. Esos resultados no equivalen a cobertura del 100%, una suite completa ni una prueba integral de MariaDB. Cada reporte debe asociarse con su ejecución según [la evidencia final](evidencias/validacion.md).

## Alcance

Esta carpeta corresponde a la **Parte 1**, cuyo máximo en la rúbrica es **1.50 puntos**. La Parte 2 contempla tres pipelines, ampliación de pruebas y análisis de seguridad; sus resultados no se afirman aquí. El estado verificable de SonarScanner se documenta por separado en la evidencia final, distinguiendo configuración de ejecución y reporte.

El [guion](GUION_VIDEO.md) presenta un avance de 6–8 minutos de la Parte 1. Antes de grabar, comprobar los números de build, los reportes y los análisis de Sonar identificados en las evidencias. No mostrar contraseñas ni tokens.



## Reportes de consulta directa
- Cliente: [JaCoCo HTML](reportes/cliente/jacoco/index.html), [JaCoCo XML](reportes/cliente/jacoco/jacoco.xml), [Surefire XML](reportes/cliente/surefire/TEST-com.mx.cbtis.controller.ProyectosClienteTests.xml).
- API: [JaCoCo HTML](reportes/api/jacoco/index.html), [JaCoCo XML](reportes/api/jacoco/jacoco.xml), [Surefire XML](reportes/api/surefire/TEST-com.mx.cbtis.gestor.controller.ProyectosControllerTests.xml).
- SonarQube: [evidencia del cliente](evidencias/sonar-cliente.json) y [evidencia de la API](evidencias/sonar-api.json). Los dashboards locales están en http://localhost:9000/dashboard?id=ejercicio5-cliente y http://localhost:9000/dashboard?id=ejercicio5-api.

Abrir los HTML después de descomprimir el ZIP; conservar todas las subcarpetas. La cobertura de métodos de JaCoCo no es la métrica Coverage de SonarQube.


**Ejecuciones finales verificadas:** cliente #4 y API #2 en SUCCESS, 12 pruebas aprobadas; ambos análisis procesados en SonarQube. Consultar los hallazgos y límites en la validación adjunta.

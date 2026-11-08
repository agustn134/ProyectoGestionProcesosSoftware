# Guion de video: Práctica 3, avance de la Parte 1

Duración estimada: 7 minutos. Hablar con calma y mostrar cada resultado antes de pasar al siguiente punto.

Este guion demuestra la actualización tecnológica de la Parte 1. El video completo de la práctica también deberá incorporar después las evidencias de la Parte 2: tres pipelines, pruebas completas y análisis de seguridad. No presentar este avance como la práctica completa.

## Preparación antes de grabar

- Abrir el repositorio: https://github.com/agustn134/ProyectoGestionProcesosSoftware
- Abrir las dos carpetas de los servicios y sus POM.
- Tener disponibles los cuatro POM: dos en `practica-3/parte-1/pom-originales/` y dos en `practica-3/parte-1/pom-actualizados/`.
- Abrir Jenkins en http://localhost:8090 y entrar antes de grabar. No mostrar la contraseña ni las credenciales guardadas.
- Abrir los trabajos `Unidad2-Cliente` y `Unidad2-API`, la consola de una ejecución terminada y sus resultados de pruebas.
- Abrir los reportes JaCoCo del mismo build que se vaya a explicar. Las copias están en `reportes/cliente/jacoco/index.html` y `reportes/api/jacoco/index.html`, dentro de esta entrega. Abrirlas después de descomprimir el ZIP.
- Tener a la vista el documento técnico de la Parte 1.
- Abrir ambos dashboards de SonarQube y la tabla de validación de esta entrega.

No es necesario esperar toda la compilación dentro del video: se puede iniciar una ejecución y después mostrar una ejecución terminada, indicando claramente su número. Para esta entrega se muestran el cliente #4 y la API #2, asociados a los reportes copiados. Si se usa otra ejecución, actualizar el número y sus evidencias.

## Escena 1. Presentación y alcance — 0:00 a 0:35

### Qué mostrar

La página principal del repositorio y las carpetas `Ejercicio5Cliente` y `Ejercicio5MyAPIGestor`.

### Qué decir

> En este avance presento la Parte 1 de la Práctica 3. Actualicé dos servicios a Java 21: el cliente, que presenta la interfaz, y la API, que atiende las operaciones y se conecta a MariaDB. Voy a mostrar los cambios en los POM, la configuración de Jenkins y Maven, y las pruebas y reportes que comprueban que la actualización compila.
>
> Ambos servicios están en mi repositorio. Conservé también la referencia al proyecto de origen en el README.

## Escena 2. Los cuatro POM y el cambio a Java 21 — 0:35 a 1:40

### Qué mostrar

1. La carpeta `practica-3/parte-1/pom-originales/`, con los dos POM originales.
2. La carpeta `practica-3/parte-1/pom-actualizados/`, con los dos actualizados.
3. Un original y su actualización, lado a lado: `java.version` cambia de `17` a `21`.
4. Repetir brevemente la comparación para el otro servicio.
5. En un POM actualizado, mostrar `maven-compiler-plugin`, versión `3.13.0`, y `release` con valor `21`.

### Qué decir

> Aquí están los cuatro archivos solicitados, identificados por servicio y por su estado antes o después. Las copias originales conservan el estado que tenían al comenzar la actualización; ya contenían Surefire y Sonar. No reconstruí esos originales después de modificar el proyecto.
>
> En los dos servicios cambié la versión de Java de 17 a 21. También fijé el compilador de Maven en la versión 3.13.0 y configuré release 21 para que genere el código compilado para esa versión de Java.
>
> Conservé las versiones de Spring Boot que tenía cada servicio porque la actualización requerida era a Java 21 y la compilación se verificó con ellas. En la API no hice una migración adicional de las entidades a Jakarta.

## Escena 3. Jenkins utiliza JDK 21 y Maven compatible — 1:40 a 2:35

### Qué mostrar

1. Jenkins: Administrar Jenkins > Tools.
2. La instalación `JDK21`, con la ruta `/opt/java/openjdk`.
3. La herramienta `Maven3`, configurada con Maven `3.9.9`.
4. La etapa `Verificar versiones` de un build terminado: `java -version` y `mvn -version`.

### Qué decir

> Al principio, el contenedor de Jenkins todavía ejecutaba Java 17. Cambiar únicamente el POM no era suficiente, porque Jenkins también necesitaba compilar con el JDK correcto.
>
> Conservé Jenkins 2.541.3 y su volumen de configuración, y cambié su imagen a una que utiliza Java 21. Aquí está la herramienta JDK21 que usan los dos Jenkinsfile.
>
> También configuré Maven 3.9.9 con el nombre Maven3. La versión de Maven no tiene que llamarse 21: lo que verifico es que Maven se ejecute con Java 21 y compile el proyecto para esa versión. Esta salida de la consola muestra las versiones que realmente se usaron en Jenkins.

Nota para la grabación: en la validación inicial Jenkins reportó Temurin `21.0.9`; la compilación local en Windows usó JDK `21.0.12`. Ambas pertenecen a Java 21. Leer siempre la versión que aparezca en la ejecución mostrada.

## Escena 4. Plugins y propósito de cada uno — 2:35 a 3:20

### Qué mostrar

En ambos POM actualizados, localizar estos tres bloques:

| Plugin | Versión |
| --- | --- |
| `sonar-maven-plugin` | `5.5.0.6356` |
| `jacoco-maven-plugin` | `0.8.12` |
| `maven-surefire-plugin` | `3.2.5` |

Mostrar las ejecuciones `prepare-agent` y `report` de JaCoCo, esta última en la fase `verify`.

### Qué decir

> Los dos POM tienen los tres plugins con versiones explícitas. Surefire ejecuta las pruebas y genera los resultados. JaCoCo registra qué código recorren esas pruebas y genera su reporte de cobertura al terminar verify. SonarScanner conecta Maven con el análisis de SonarQube y conserva exactamente la versión solicitada, 5.5.0.6356.
>
> Incorporar un plugin en el POM y ejecutarlo son cosas que compruebo por separado. Para Surefire y JaCoCo voy a mostrar las pruebas y los reportes generados; el estado del análisis de Sonar lo explico con su evidencia específica.

## Escena 5. Compilación y pruebas exitosas en Jenkins — 3:20 a 4:25

### Qué mostrar

1. http://localhost:8090/job/Unidad2-Cliente/4/
2. Su consola con el comando `clean verify`, `release 21`, el resultado de las pruebas y `Finished: SUCCESS`.
3. http://localhost:8090/job/Unidad2-API/2/
4. Las mismas evidencias de la API y los JAR archivados.
5. La página de resultados de pruebas de cada trabajo.

Si se muestran builds posteriores, actualizar los números de las URLs anteriores y sus evidencias, y comprobar su resultado antes de grabar.

### Qué decir

> Estos son los dos trabajos conectados a la rama main de mi repositorio. Cada uno utiliza el Jenkinsfile de su servicio. Aquí se ve que compila con release 21 y termina en SUCCESS.
>
> En esta validación se seleccionaron seis pruebas MVC por servicio, doce en total, sin fallos ni errores. Estas pruebas revisan solicitudes, respuestas y algunos casos de error con las dependencias simuladas.
>
> La selección de estas pruebas no demuestra todavía todas las operaciones del sistema ni una prueba integral contra MariaDB. Ese alcance queda identificado en el documento. Además del resultado de las pruebas, Jenkins conservó el JAR generado de cada servicio.

## Escena 6. Cobertura real y problemas resueltos — 4:25 a 5:35

### Qué mostrar

1. La tabla general del reporte JaCoCo del cliente y el contador de métodos.
2. La misma tabla de la API.
3. La carpeta o sección que contiene los reportes y su vínculo con la ejecución mostrada.
4. Brevemente, la etapa `Checkout` del cliente y las instrucciones `git lfs install --local --skip-repo` y `git lfs pull`.
5. Opcionalmente, la consola del cliente #3 con el fallo de LFS y la etapa Checkout corregida del cliente #4.
6. El README: contenedor `admin-proyectos-mariadb`, puerto `3297`, y `db/schema.sql` para una instalación nueva.

### Qué decir

> JaCoCo muestra la cobertura real de las pruebas ejecutadas. En los reportes adjuntos de esta entrega, el cliente cubrió 24 de 98 métodos, aproximadamente 24.49 por ciento, y la API cubrió 20 de 103, aproximadamente 19.42 por ciento. No presento estos resultados como 100 por ciento de cobertura.
>
> Que las doce pruebas pasen significa que sus comprobaciones se cumplieron. No significa que ya se haya probado todo el código. La cobertura y la calidad de las comprobaciones se revisan por separado. En esta medición no se configuraron exclusiones para ocultar clases sin probar.
>
> Además de Java 17 en Jenkins, resolví dos aspectos del entorno. Los videos y GIF grandes se publicaron con Git LFS. La repetición mostró que había que inicializar sus filtros y respetar los hooks deshabilitados por Jenkins. Lo resolví con git lfs install --local --skip-repo, seguido de git lfs pull. Así se descargan los archivos reales antes de compilar. Para la base de datos identifiqué el contenedor correcto, admin-proyectos-mariadb, que usa el puerto 3297. Conservé los datos existentes. Para otro equipo incluí un esquema sin registros de usuarios.

Si los reportes actuales muestran cifras distintas por nuevas pruebas, leer sus valores exactos e identificar el build correspondiente. No mezclar el porcentaje de una ejecución con las pruebas de otra. Mantener las cifras iniciales anteriores solo como referencia histórica.

## Escena 7. Análisis ejecutado en SonarQube — 5:35 a 6:15

### Qué mostrar

1. La etapa SonarQube de cliente #4 y API #2, con `sonar:5.5.0.6356:sonar`, `ANALYSIS SUCCESSFUL` y el enlace al análisis.
2. Cliente: http://localhost:9000/dashboard?id=ejercicio5-cliente
3. API: http://localhost:9000/dashboard?id=ejercicio5-api
4. El estado del Quality Gate y la evidencia JSON/tabla de validación. Mostrar el nombre del proyecto para identificar cada resultado.

### Qué decir

> Además de declarar SonarScanner, ejecuté la versión 5.5.0.6356 en los dos servicios. Aquí se ve la ejecución y, en SonarQube, el análisis procesado de cada proyecto.
>
> Los dos Quality Gates aparecen en OK. En esta pantalla muestro la puerta de calidad, o Quality Gate, y los hallazgos que aún deben revisarse. Su estado corresponde a las reglas configuradas en SonarQube. No es el mismo indicador que el porcentaje de métodos que mostré en JaCoCo, y no significa que se haya probado todo ni que el software esté libre de vulnerabilidades.
>
> En la Parte 1 comprobé que el análisis funciona y dejé sus resultados como evidencia. La revisión y atención completa de incidencias de seguridad forma parte de la siguiente entrega.

## Escena 8. Documento, ligas y cierre — 6:15 a 7:00

### Qué mostrar

1. El documento técnico actualizado de la Parte 1: cambios, justificación y evidencia.
2. Los dos enlaces individuales:
   - Cliente: https://github.com/agustn134/ProyectoGestionProcesosSoftware/tree/main/Ejercicio5Cliente
   - API: https://github.com/agustn134/ProyectoGestionProcesosSoftware/tree/main/Ejercicio5MyAPIGestor
3. El commit de la entrega y el paquete de evidencias publicado.

### Qué decir

> El documento explica qué cambié, por qué lo cambié y cómo lo comprobé. La entrega reúne los dos POM originales, los dos actualizados, las referencias a Jenkins y los reportes disponibles.
>
> Estas son las dos ligas de los servicios dentro de un solo repositorio mío. Cada carpeta conserva su POM, su código y su Jenkinsfile. En el README están los requisitos y los pasos para utilizarlos.
>
> Con esto demuestro la actualización tecnológica de la Parte 1. La Parte 2 y sus tres pipelines, las pruebas completas y el análisis de seguridad se presentarán con sus propias ejecuciones y resultados. En este avance no afirmo que esas actividades ya estén terminadas.

## Comprobación final antes de entregar el video

- Los cuatro POM se distinguen claramente y corresponden a los servicios mostrados.
- Se leen Java 21 y Maven 3.9.9 en la consola real de Jenkins.
- Los dos resultados SUCCESS pertenecen a los números de build mencionados.
- Se muestran los resultados de Surefire y los reportes JaCoCo, identificando su ejecución.
- Los porcentajes y cantidades coinciden con esos reportes; no se afirma cobertura total.
- La explicación de Sonar corresponde a lo que realmente terminó y tiene evidencia.
- No se muestran contraseñas, tokens ni pantallas de credenciales.
- Las dos ligas GitHub y el commit se pueden abrir.
- El cierre mantiene claro que este video es un avance de la Parte 1.




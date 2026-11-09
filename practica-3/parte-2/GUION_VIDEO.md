# Guion de video — Práctica 3, Parte 2

**Guion final: mostrar las ejecuciones y reportes de esta entrega.**

Duración prevista: 8–10 minutos, sin esperar en cámara todas las descargas o compilaciones. Mostrar ejecuciones terminadas identificadas por número y commit. Hablar de lo que aparece en pantalla, sin leer comandos largos ni exponer credenciales.

## Datos concretos para la grabación

| Dato | Resultado verificado |
| --- | --- |
| Commit evaluado de esta entrega | 8a6491f |
| Pipeline Front: nombre, número, resultado, enlace | Frontend #4, SUCCESS: http://localhost:8090/job/Practica3-Parte2-Frontend/4/ |
| Pipeline API: nombre, número, resultado, enlace | Backend #4, SUCCESS: http://localhost:8090/job/Practica3-Parte2-Backend/4/ |
| Pipeline de base de datos: nombre, número, resultado, enlace | Base de datos #4, SUCCESS: http://localhost:8090/job/Practica3-Parte2-BaseDatos/4/ |
| Front: pruebas aprobadas, fallos, errores, omitidas | 58 aprobadas, cero fallos/errores y omitidas |
| API: pruebas aprobadas, fallos, errores, omitidas | 57 aprobadas, cero fallos/errores y omitidas |
| Front: métodos cubiertos/total y porcentaje, alcance y exclusiones | 97 de 97 métodos Java evaluables, cien por ciento |
| API: métodos cubiertos/total y porcentaje, alcance y exclusiones | 144 de 144 métodos Java evaluables, cien por ciento |
| Escáneres: versión, fecha, commit, alcance y reportes | SonarScanner 5.5.0.6356, OSV 2.7.0, Gitleaks 8.30.1; versiones y alcance en los reportes de cada build |
| Hallazgos confirmados, corregidos, pendientes y falsos positivos justificados | API: 18 bindings de entidades, 15 comparaciones null y cinco inyecciones corregidas/reanalizadas; dependencias actualizadas y dos avisos contextuales Spring conservados |
| Resultados de Sonar y Quality Gate, consultados por separado | Ambos análisis procesados y Quality Gates OK; ver evidencias/resultados.json y Sonar de cada servicio |
| Rutas o enlaces del documento y del paquete final de evidencias | Practica3-Parte2.zip y esta carpeta practica-3/parte-2 en GitHub |

No reutilizar como resultados actuales las cifras de la Parte 1. Aquella entrega tenía seis pruebas MVC por servicio y cobertura de métodos de 24/98 en Front y 20/103 en API. El objetivo de esta parte se debe comprobar con los reportes nuevos.

## Preparación de la grabación

- Abrir el repositorio, el commit de la entrega y el documento técnico.
- Tener abiertas las tres ejecuciones finales en Jenkins: Front, API y base de datos.
- Abrir los reportes Surefire y JaCoCo de las mismas ejecuciones.
- Abrir los reportes de análisis de código, dependencias y secretos que realmente se hayan producido.
- Tener abierta la tabla de clasificación y atención de hallazgos.
- Preparar un ejemplo de corrección con su prueba y su comparación antes/después.
- Acceder previamente a Jenkins y SonarQube. No mostrar pantallas de credenciales, tokens, claves ni datos personales de la base existente.
- Si se abre un reporte HTML local, descargar y descomprimir primero el paquete con todos sus recursos.

## Escena 1. Objetivo y estado inicial — 0:00 a 0:40

### Qué mostrar

Repositorio `ProyectoGestionProcesosSoftware`, documento de esta parte y commit 8a6491f.

### Qué decir

> En esta parte continúo con los dos servicios que ya estaban actualizados a Java 21. Ahora voy a mostrar las pruebas de los métodos evaluables, sus resultados de cobertura, la revisión de seguridad y los tres pipelines: Front, API y base de datos.
>
> La primera entrega comprobó la actualización y una selección de pruebas. Esta vez explico qué pruebas se agregaron, qué hallazgos aparecieron y cómo comprobé las correcciones. Los números que voy a mostrar corresponden a estas ejecuciones y a este commit.

## Escena 2. Pruebas completas y comprobaciones — 0:40 a 2:00

### Qué mostrar

1. Archivos de pruebas de Front y API.
2. Un caso de éxito y un caso de error con sus aserciones.
3. Un ejemplo de comprobación de interacción con una dependencia simulada.
4. El comando `mvn clean verify` del pipeline, sin selección de clases con `-Dtest`.
5. Resultados Surefire: 58 aprobadas, cero fallos/errores y omitidas y 57 aprobadas, cero fallos/errores y omitidas.

### Qué decir

> Aquí están las pruebas que se ejecutaron en esta entrega. No reviso solamente que un método termine: compruebo su resultado, la respuesta esperada y lo que debe ocurrir cuando recibe datos incorrectos o una dependencia falla.
>
> En este caso de éxito, esta comprobación valida el resultado que debe entregar la operación. En este caso de error, verifico la respuesta y que no se continúe con una operación que no corresponde.
>
> Estos son los resultados de Surefire de cada servicio. El total es el que aparece en estos reportes. Si una prueba usa una dependencia simulada, lo indico; no la presento como una operación real de MariaDB.

> El pipeline ejecuta la suite completa con clean verify, sin seleccionar clases. En total se aprobaron 58 pruebas del cliente, 57 de la API y ocho de la base de datos; no hubo fallos ni pruebas omitidas.

## Escena 3. Cobertura de métodos y exclusiones — 2:00 a 3:15

### Qué mostrar

1. Reporte JaCoCo de Front, contador de métodos y desglose por clase.
2. Reporte JaCoCo de API, contador de métodos y desglose por clase.
3. Archivo que documenta qué métodos se consideran evaluables.
4. Configuración de exclusiones, si existen, con la justificación de cada una.

### Qué decir

> Este reporte muestra cuántos métodos recorrieron las pruebas. En Front se cubrieron los 97 métodos Java evaluables y en API los 144. Ambos llegaron al cien por ciento de métodos. En el cliente quedaron seis líneas y dos ramas sin recorrer; por eso distingo esos contadores del de métodos.
>
> Aquí explico qué métodos entran en la evaluación y qué exclusiones existen. Una exclusión debe tener una razón concreta y quedar visible; no se usa para esconder una operación que falta probar.
>
> El porcentaje de cobertura y la calidad de las comprobaciones son dos cosas que reviso por separado. Por eso muestro también las aserciones de las pruebas, no solo el porcentaje.

> Con este alcance, el contador quedó en cien por ciento. Solo se excluye la clase que arranca Spring de cada servicio; los modelos, controladores y servicios sí se evalúan. El porcentaje no mide JavaScript ni sustituye las comprobaciones de comportamiento.

## Escena 4. Seguridad: alcance de los escaneos — 3:15 a 4:25

### Qué mostrar

1. Versiones y comandos reales de las herramientas usadas.
2. Configuración que excluye todos los directorios `target` y las evidencias generadas.
3. Confirmación de que los directorios `src` permanecen dentro del análisis.
4. Reportes separados de código, dependencias y secretos, que se ejecutaron en esta entrega.
5. Commit, fecha y cantidad de archivos o componentes analizados.

### Qué decir

> La revisión de seguridad está identificada por herramienta, versión, fecha y commit. Aquí se ve el alcance: excluí los archivos generados de target y las evidencias, conservando el código fuente dentro del análisis.
>
> El análisis de código identifica patrones en la aplicación. El análisis de dependencias revisa las versiones de las bibliotecas y sus avisos conocidos. El análisis de secretos busca posibles claves o tokens guardados en archivos. Presento el resultado de cada herramienta que realmente ejecuté y sus límites.
>
> Terminar un escaneo no significa que el proyecto esté libre de problemas. Por eso el siguiente paso es revisar y clasificar sus hallazgos.

> En estas ejecuciones usé Sonar para el código, OSV sobre el inventario completo de Maven, incluidas las dependencias de prueba, y Gitleaks para buscar secretos en los archivos actuales del módulo. Gitleaks no revisó el historial de Git.

## Escena 5. Hallazgos, correcciones y verificación — 4:25 a 5:50

### Qué mostrar

1. Tabla de hallazgos con regla o identificador, archivo/componente, severidad, clasificación, acción y evidencia.
2. Un hallazgo corregido: código anterior y código actualizado.
3. La prueba que comprueba el comportamiento corregido.
4. El reporte posterior a la corrección.
5. Un falso positivo o caso no aplicable, únicamente si existe una justificación verificable.

### Qué decir

> En esta tabla distingo lo que confirmó la revisión, lo que se corrigió, lo que sigue pendiente y los casos que no aplican por una condición concreta.
>
> Este ejemplo se corrigió por esta razón: la API recibía entidades de base de datos directamente. Ahora recibe DTOs que definen los campos permitidos. La prueba comprueba que un registro no se otorgue un rol privilegiado y que la contraseña se guarde como hash sin regresar en el JSON. Después de modificarlo, volví a ejecutar las pruebas de seguridad de usuarios y el análisis de Sonar y este es el resultado.
>
> Para descartar un hallazgo no basta decir que el sistema es una práctica. La justificación debe relacionarse con el archivo, la configuración y las condiciones que exige el problema. Si una dependencia sigue en una versión reportada como vulnerable, lo dejo indicado aunque no haya demostrado una ruta de explotación.

> En la API corregí los 18 hallazgos por recibir entidades directamente, los 15 errores de comparación con null y las cinco observaciones de inyección. El análisis nuevo ya no los reporta. OSV conserva dos avisos de Spring por servicio: las versiones siguen afectadas, pero las condiciones concretas no aparecen en esta aplicación y una prueba las comprueba. La revisión vence el 7 de diciembre de 2026; después debe reevaluarse o actualizarse. No digo que haya cero avisos.
>
> También queda un límite del proyecto original: todavía no cuenta con sesiones ni permisos completos para cada operación. Las contraseñas antiguas en texto plano requieren un restablecimiento autorizado; no cambié esos datos en la base existente.

## Escena 6. Tres pipelines en Jenkins — 5:50 a 7:10

### Qué mostrar

1. Lista de los tres trabajos y ejecuciones: Frontend #4, SUCCESS: http://localhost:8090/job/Practica3-Parte2-Frontend/4/, Backend #4, SUCCESS: http://localhost:8090/job/Practica3-Parte2-Backend/4/, Base de datos #4, SUCCESS: http://localhost:8090/job/Practica3-Parte2-BaseDatos/4/.
2. Pipeline Front: checkout, Java y Maven, pruebas, cobertura, análisis y artefactos configurados.
3. Pipeline API: las mismas etapas que efectivamente tenga.
4. Resultado final de cada ejecución y sus reportes.

### Qué decir

> Aquí están los tres pipelines de la entrega. Front y API obtienen el código del repositorio y usan las versiones de Java y Maven que muestra la consola.
>
> En cada ejecución se puede seguir el resultado de las etapas y abrir las pruebas y los artefactos. Estos números identifican las ejecuciones que estoy presentando; no estoy mezclando un resultado anterior con reportes nuevos.
>
> Al mostrar los análisis de Sonar, reviso por separado que el servidor haya procesado el análisis y cuál fue el resultado del Quality Gate. El estado de Jenkins no sustituye esas comprobaciones.

## Escena 7. Pipeline de base de datos y aislamiento — 7:10 a 8:15

### Qué mostrar

1. Jenkinsfile y scripts del pipeline de base de datos.
2. Nombre de la base o contenedor dedicado a CI: admin-proyectos-db-ci, base ci_gestor, sin puerto publicado ni datos de producción.
3. Aplicación del esquema, comprobaciones de tablas y prueba de escritura/lectura que realmente se haya ejecutado.
4. Limpieza o rollback y resultado de las comprobaciones.
5. Reporte del trabajo de base de datos.

### Qué decir

> Este tercer pipeline comprueba la base de datos en un entorno dedicado a las pruebas. La conexión no apunta a la base con los datos del proyecto que uso normalmente.
>
> El trabajo ejecuta la recreación y la importación del esquema sobre ci_gestor y comprueba seis tablas con sus columnas y tipos, la semilla técnica, las operaciones con parámetros y el rechazo de IDs duplicados. Al finalizar realiza rollback de los datos de prueba y cierre de la conexión; el esquema CI permanece y se recrea en el siguiente build. Así puedo repetir la comprobación sin modificar los registros de la base existente.
>
> Aquí también documento cómo se preparó ese entorno. Si el contenedor lo creó previamente el operador, lo explico: el pipeline comprueba el esquema y las operaciones, pero no afirmo que haya creado automáticamente el contenedor.

## Escena 8. Documento, evidencias y cierre — 8:15 a 9:00

### Qué mostrar

Documento técnico, tabla de resultados, commits y paquete de entrega Practica3-Parte2.zip y esta carpeta practica-3/parte-2 en GitHub.

### Qué decir

> La entrega reúne las pruebas, los reportes de cobertura, los escaneos y su clasificación, las correcciones y los resultados de los tres pipelines. También identifica los avisos de Spring conservados y los límites de acceso de la aplicación.
>
> Estos archivos permiten revisar el mismo commit y las mismas ejecuciones que mostré en el video. El documento explica los cambios, los problemas que aparecieron y la forma en que comprobé cada solución.

## Comprobación antes de grabar

- Comprobar los valores de la tabla y abrir los reportes de esos mismos builds.
- Confirmar nombres, números, resultados y enlaces de los tres pipelines.
- Confirmar que reportes, commit y ejecuciones corresponden entre sí.
- Leer la cobertura de `METHOD` de JaCoCo; no intercambiarla con Coverage de Sonar.
- Declarar exclusiones y denominadores del porcentaje mostrado.
- Mostrar el alcance de seguridad sin `target` ni reportes generados, conservando `src`.
- Diferenciar hallazgos de código, dependencias y secretos; indicar los límites de cada herramienta.
- No mostrar valores de claves, tokens ni registros personales.
- No afirmar corrección o ausencia de vulnerabilidades sin evidencia.
- Explicar quién aprovisionó la base de CI y qué comprobó realmente el pipeline.


Para explicar la organización, mostrar RAMAS.md: main conserva lo estable, develop sirve para integrar y las ramas de trabajo usan featured. No se reescribió la historia ni se fusionó la Parte 2 automáticamente.

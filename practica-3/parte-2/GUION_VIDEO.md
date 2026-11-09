# Guion de video — Práctica 3, Parte 2

**BORRADOR: completar los campos pendientes con evidencia real antes de grabar.** No presentar este archivo como guion definitivo mientras contenga marcadores `PENDIENTE`.

Duración prevista: 8–10 minutos, sin esperar en cámara todas las descargas o compilaciones. Mostrar ejecuciones terminadas identificadas por número y commit. Hablar de lo que aparece en pantalla, sin leer comandos largos ni exponer credenciales.

## Datos que deben completarse al terminar

| Dato | Resultado pendiente |
| --- | --- |
| Commit evaluado de esta entrega | PENDIENTE_COMMIT |
| Pipeline Front: nombre, número, resultado, enlace | PENDIENTE_FRONT_BUILD |
| Pipeline API: nombre, número, resultado, enlace | PENDIENTE_API_BUILD |
| Pipeline de base de datos: nombre, número, resultado, enlace | PENDIENTE_DB_BUILD |
| Front: pruebas aprobadas, fallos, errores, omitidas | PENDIENTE_FRONT_PRUEBAS |
| API: pruebas aprobadas, fallos, errores, omitidas | PENDIENTE_API_PRUEBAS |
| Front: métodos cubiertos/total y porcentaje, alcance y exclusiones | PENDIENTE_FRONT_COBERTURA |
| API: métodos cubiertos/total y porcentaje, alcance y exclusiones | PENDIENTE_API_COBERTURA |
| Escáneres: versión, fecha, commit, alcance y reportes | PENDIENTE_ESCANEOS |
| Hallazgos confirmados, corregidos, pendientes y falsos positivos justificados | PENDIENTE_TRIAGE |
| Resultados de Sonar y Quality Gate, consultados por separado | PENDIENTE_SONAR |
| Rutas o enlaces del documento y del paquete final de evidencias | PENDIENTE_ENTREGA |

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

Repositorio `ProyectoGestionProcesosSoftware`, documento de esta parte y commit `PENDIENTE_COMMIT`.

### Qué decir

> En esta parte continúo con los dos servicios que ya estaban actualizados a Java 21. Ahora voy a mostrar las pruebas de los métodos evaluables, sus resultados de cobertura, la revisión de seguridad y los tres pipelines: Front, API y base de datos.
>
> La primera entrega comprobó la actualización y una selección de pruebas. Esta vez explico qué pruebas se agregaron, qué hallazgos aparecieron y cómo comprobé las correcciones. Los números que voy a mostrar corresponden a estas ejecuciones y a este commit.

## Escena 2. Pruebas completas y comprobaciones — 0:40 a 2:00

### Qué mostrar

1. Archivos de pruebas de Front y API.
2. Un caso de éxito y un caso de error con sus aserciones.
3. Un ejemplo de comprobación de interacción con una dependencia simulada.
4. El comando real de construcción del pipeline; comprobar si todavía limita clases con `-Dtest`.
5. Resultados Surefire: `PENDIENTE_FRONT_PRUEBAS` y `PENDIENTE_API_PRUEBAS`.

### Qué decir

> Aquí están las pruebas que se ejecutaron en esta entrega. No reviso solamente que un método termine: compruebo su resultado, la respuesta esperada y lo que debe ocurrir cuando recibe datos incorrectos o una dependencia falla.
>
> En este caso de éxito, esta comprobación valida el resultado que debe entregar la operación. En este caso de error, verifico la respuesta y que no se continúe con una operación que no corresponde.
>
> Estos son los resultados de Surefire de cada servicio. El total es el que aparece en estos reportes. Si una prueba usa una dependencia simulada, lo indico; no la presento como una operación real de MariaDB.

**Ajustar antes de grabar:** explicar el alcance real del comando. Si sigue usando `-Dtest`, enumerar las clases incluidas y justificar que cubren el alcance declarado. No decir “suite completa” por el solo hecho de que el build sea exitoso.

## Escena 3. Cobertura de métodos y exclusiones — 2:00 a 3:15

### Qué mostrar

1. Reporte JaCoCo de Front, contador de métodos y desglose por clase.
2. Reporte JaCoCo de API, contador de métodos y desglose por clase.
3. Archivo que documenta qué métodos se consideran evaluables.
4. Configuración de exclusiones, si existen, con la justificación de cada una.

### Qué decir

> Este reporte muestra cuántos métodos recorrieron las pruebas. En Front el resultado es PENDIENTE_FRONT_COBERTURA y en API es PENDIENTE_API_COBERTURA.
>
> Aquí explico qué métodos entran en la evaluación y qué exclusiones existen. Una exclusión debe tener una razón concreta y quedar visible; no se usa para esconder una operación que falta probar.
>
> El porcentaje de cobertura y la calidad de las comprobaciones son dos cosas que reviso por separado. Por eso muestro también las aserciones de las pruebas, no solo el porcentaje.

**Elegir la frase de cierre que corresponda al reporte final:**

- Si se verificó 100%: “Con el alcance evaluable documentado, el contador de métodos quedó en cien por ciento. El documento también conserva el alcance global para que se vea qué se excluyó, si hubo exclusiones”.
- Si no se verificó 100%: “Este es el porcentaje alcanzado; no voy a presentarlo como cien por ciento. Los métodos que faltan y la razón están identificados en el documento”.

Eliminar la opción que no corresponda antes de entregar el guion final.

## Escena 4. Seguridad: alcance de los escaneos — 3:15 a 4:25

### Qué mostrar

1. Versiones y comandos reales de las herramientas usadas.
2. Configuración que excluye todos los directorios `target` y las evidencias generadas.
3. Confirmación de que los directorios `src` permanecen dentro del análisis.
4. Reportes separados de código, dependencias y secretos, si esos tres tipos se ejecutaron.
5. Commit, fecha y cantidad de archivos o componentes analizados.

### Qué decir

> La revisión de seguridad está identificada por herramienta, versión, fecha y commit. Aquí se ve el alcance: excluí los archivos generados de target y las evidencias, conservando el código fuente dentro del análisis.
>
> El análisis de código identifica patrones en la aplicación. El análisis de dependencias revisa las versiones de las bibliotecas y sus avisos conocidos. El análisis de secretos busca posibles claves o tokens guardados en archivos. Presento el resultado de cada herramienta que realmente ejecuté y sus límites.
>
> Terminar un escaneo no significa que el proyecto esté libre de problemas. Por eso el siguiente paso es revisar y clasificar sus hallazgos.

**Ajustar antes de grabar:** no nombrar un escaneo que no se ejecutó. Si la herramienta omitió archivos, no pudo resolver dependencias o no incluyó dependencias de prueba, declararlo junto al reporte.

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
> Este ejemplo se corrigió por esta razón: PENDIENTE_EXPLICACION_CORRECCION. La prueba comprueba PENDIENTE_COMPORTAMIENTO_VERIFICADO. Después de modificarlo, volví a ejecutar PENDIENTE_PRUEBA_O_ESCANEO y este es el resultado.
>
> Para descartar un hallazgo no basta decir que el sistema es una práctica. La justificación debe relacionarse con el archivo, la configuración y las condiciones que exige el problema. Si una dependencia sigue en una versión reportada como vulnerable, lo dejo indicado aunque no haya demostrado una ruta de explotación.

**No usar como resultado nuevo:** en la Parte 1, Sonar reportó para la API 15 bugs, 18 vulnerabilidades y 5 code smells, pendientes de revisión. Esta escena debe mostrar qué ocurrió con esos hallazgos en el análisis actualizado, sin asumir que desaparecieron.

## Escena 6. Tres pipelines en Jenkins — 5:50 a 7:10

### Qué mostrar

1. Lista de los tres trabajos y ejecuciones: `PENDIENTE_FRONT_BUILD`, `PENDIENTE_API_BUILD`, `PENDIENTE_DB_BUILD`.
2. Pipeline Front: checkout, Java y Maven, pruebas, cobertura, análisis y artefactos configurados.
3. Pipeline API: las mismas etapas que efectivamente tenga.
4. Resultado final de cada ejecución y sus reportes.

### Qué decir

> Aquí están los tres pipelines de la entrega. Front y API obtienen el código del repositorio y usan las versiones de Java y Maven que muestra la consola.
>
> En cada ejecución se puede seguir el resultado de las etapas y abrir las pruebas y los artefactos. Estos números identifican las ejecuciones que estoy presentando; no estoy mezclando un resultado anterior con reportes nuevos.
>
> Si muestro un análisis de Sonar, reviso por separado que el servidor haya procesado el análisis y cuál fue el resultado del Quality Gate. El estado de Jenkins no sustituye esas comprobaciones.

## Escena 7. Pipeline de base de datos y aislamiento — 7:10 a 8:15

### Qué mostrar

1. Jenkinsfile y scripts del pipeline de base de datos.
2. Nombre de la base o contenedor dedicado a CI: `PENDIENTE_DB_AISLADA`.
3. Aplicación del esquema, comprobaciones de tablas y prueba de escritura/lectura que realmente se haya ejecutado.
4. Limpieza o rollback y resultado de las comprobaciones.
5. Reporte del trabajo de base de datos.

### Qué decir

> Este tercer pipeline comprueba la base de datos en un entorno dedicado a las pruebas. La conexión no apunta a la base con los datos del proyecto que uso normalmente.
>
> El trabajo ejecuta PENDIENTE_PASOS_DB_REALES y comprueba PENDIENTE_ASERCIONES_DB. Al finalizar realiza PENDIENTE_LIMPIEZA_DB. Así puedo repetir la comprobación sin modificar los registros de la base existente.
>
> Aquí también documento cómo se preparó ese entorno. Si el contenedor lo creó previamente el operador, lo explico: el pipeline comprueba el esquema y las operaciones, pero no afirmo que haya creado automáticamente el contenedor.

## Escena 8. Documento, evidencias y cierre — 8:15 a 9:00

### Qué mostrar

Documento técnico, tabla de resultados, commits y paquete de entrega `PENDIENTE_ENTREGA`.

### Qué decir

> La entrega reúne las pruebas, los reportes de cobertura, los escaneos y su clasificación, las correcciones y los resultados de los tres pipelines. También identifica lo que siga pendiente, si existe.
>
> Estos archivos permiten revisar el mismo commit y las mismas ejecuciones que mostré en el video. El documento explica los cambios, los problemas que aparecieron y la forma en que comprobé cada solución.

## Comprobación antes de convertir este borrador en guion final

- Sustituir todos los marcadores `PENDIENTE` y eliminar instrucciones alternativas.
- Confirmar nombres, números, resultados y enlaces de los tres pipelines.
- Confirmar que reportes, commit y ejecuciones corresponden entre sí.
- Leer la cobertura de `METHOD` de JaCoCo; no intercambiarla con Coverage de Sonar.
- Declarar exclusiones y denominadores del porcentaje mostrado.
- Mostrar el alcance de seguridad sin `target` ni reportes generados, conservando `src`.
- Diferenciar hallazgos de código, dependencias y secretos; indicar los límites de cada herramienta.
- No mostrar valores de claves, tokens ni registros personales.
- No afirmar corrección o ausencia de vulnerabilidades sin evidencia.
- Explicar quién aprovisionó la base de CI y qué comprobó realmente el pipeline.

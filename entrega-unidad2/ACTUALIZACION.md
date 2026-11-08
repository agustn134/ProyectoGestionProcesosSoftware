# Actualización de los dos microservicios a Java 21

## Objetivo y cambios
Se actualizan Ejercicio5Cliente y Ejercicio5MyAPIGestor de Java 17 a Java 21.
Las copias en antes/ corresponden exactamente al estado de los POM locales al iniciar esta actividad, que ya contenían Surefire y Sonar. Las copias en despues/ corresponden a la actualización.

| Elemento | Antes | Después |
| --- | --- | --- |
| java.version | 17 | 21 |
| Maven Compiler Plugin | versión heredada | 3.13.0, release 21 |
| JaCoCo | ausente | 0.8.12, prepare-agent y report en verify |
| Surefire | 3.2.5 | se conserva 3.2.5 |
| Sonar Maven Plugin | 5.5.0.6356 | se conserva la versión solicitada |
| JDK declarado en los Jenkinsfile | JDK17 | JDK21 |
| Maven para Jenkins | Maven3 sin versión documentada aquí | configurar Maven3 con 3.9.9 |

Se conservan Spring Boot 3.2.5 en el cliente y 2.7.17 en la API. Spring Boot 2.7.17 documenta compatibilidad con Java 21; esta actividad no requiere migrar las entidades javax.persistence a Jakarta. El compilador genera bytecode Java 21 mediante release 21. Maven y Java tienen numeraciones independientes: Maven 3.9.9 se ejecuta con el JDK 21.

JaCoCo conecta su agente durante las pruebas y genera target/site/jacoco/index.html y jacoco.xml al terminar verify. Declarar Sonar en el POM no ejecuta por sí solo un análisis; se requiere configurar el servidor y token en Jenkins y activar ANALIZAR_SONAR.

## Compilación
Desde la carpeta de cada servicio, con JAVA_HOME apuntando al JDK 21:

    java -version
    mvn -version
    mvn -B -ntp clean verify

Los pipelines existentes ejecutan las seis pruebas MVC de cada servicio mediante -Dtest=ProyectosClienteTests o -Dtest=ProyectosControllerTests. Esta selección no demuestra una prueba integral de la base de datos. La prueba contextLoads de la API requiere MariaDB accesible y el esquema previamente importado.

## Jenkins
En Administrar Jenkins > Tools configurar:

- JDK: nombre exacto JDK21, JAVA_HOME /opt/java/openjdk en la imagen Jenkins JDK21; desactivar instalación automática.
- Maven: nombre exacto Maven3, instalador Apache Maven 3.9.9.

Los Jenkinsfile usan comandos sh y están preparados para un agente Linux. El runtime del controlador y el JDK de compilación son configuraciones distintas: verificar ambos. Conservar el volumen jenkins_home al cambiar la imagen evita perder trabajos y credenciales.

## Docker y base de datos
El contenedor del proyecto es admin-proyectos-mariadb, imagen mariadb:11.4-noble, puerto local 3297 hacia 3306. Coincide con jdbc:mariadb://localhost:3297/dbgestor de la API.

    docker start admin-proyectos-mariadb
    docker ps --filter name=admin-proyectos-mariadb

Los otros contenedores MariaDB encontrados son bold_satoshi, lucid_tu y wonderful_pascal; no es necesario iniciarlos para este proyecto. No borrar el directorio de datos ni importar nuevamente el SQL sobre la base existente. Ejecutar los servicios localmente: API en 8081 y cliente en 8080. Para ejecutar la API dentro de Docker sería necesario cambiar localhost por el nombre de servicio de la base.

## Publicación y entrega
Existe un remoto configurado: https://github.com/agustn134/ProyectoGestionProcesosSoftware
Esto no demuestra que los cambios locales ya estén publicados.
Los Jenkinsfile están adaptados al repositorio conjunto mediante checkout scm y rutas a cada carpeta. Crear dos trabajos Pipeline from SCM con la URL del repositorio y los Script Path Ejercicio5Cliente/Jenkinsfile y Ejercicio5MyAPIGestor/Jenkinsfile. La rama debe ser la que contenga la actualización.

Ligas previstas dentro del repositorio (verificar publicación y rama antes de entregar):
- https://github.com/agustn134/ProyectoGestionProcesosSoftware/tree/main/Ejercicio5Cliente
- https://github.com/agustn134/ProyectoGestionProcesosSoftware/tree/main/Ejercicio5MyAPIGestor

Entregar los cuatro XML de antes/ y despues/, este documento, las ligas verificadas y capturas de java -version, mvn -version, BUILD SUCCESS y reportes de cobertura. No subir target/, .tools/, .idea/, contraseñas ni tokens.

## Fuentes
- https://docs.spring.io/spring-boot/docs/2.7.17/reference/html/getting-started.html
- https://github.com/jacoco/jacoco/releases/tag/v0.8.12


## Validación realizada
La API se compiló con JDK 21.0.12 y Maven 3.9.9: BUILD SUCCESS, seis pruebas MVC, cero fallos, cero errores. Se generaron el JAR y el reporte JaCoCo. La compilación y ejecución de pruebas son locales en Windows; no equivalen a una ejecución del pipeline en Jenkins.

El cliente también terminó con BUILD SUCCESS: seis pruebas, cero fallos y cero errores; se generaron JAR y reportes JaCoCo. En total se aprobaron doce pruebas MVC con Java 21.

Publicación destinada al repositorio del estudiante: https://github.com/agustn134/ProyectoGestionProcesosSoftware. El código original se referencia en el README. Los archivos multimedia grandes se conservan mediante Git LFS: al clonar instalar Git LFS y ejecutar git lfs pull antes de compilar.

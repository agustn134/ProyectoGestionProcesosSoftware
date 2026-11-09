# Proyecto Gestión del Proceso de Desarrollo de Software

Dos servicios Spring Boot actualizados a Java 21: Ejercicio5Cliente (puerto 8080) y Ejercicio5MyAPIGestor (puerto 8081).

## Requisitos
JDK 21, Maven 3.9.9 y MariaDB. Configurar JAVA_HOME para el JDK 21 y comprobar java -version y mvn -version.

## Base de datos
En el equipo de esta actividad ya existe admin-proyectos-mariadb: iniciarlo con docker start admin-proyectos-mariadb. No iniciar otra base sobre el puerto 3297.

Para una instalación nueva: copiar .env.example a .env, cambiar las claves y ejecutar docker compose up -d. El volumen nuevo importa db/schema.sql sin datos personales. No usar esta opción para reemplazar la base existente. La API utiliza dbgestor, usuario gestor y puerto 3297; definir DB_PASSWORD en el entorno de la API con la clave elegida.

## Compilar y ejecutar
Desde la raíz:

    mvn -f Ejercicio5Cliente/pom.xml clean verify
    mvn -f Ejercicio5MyAPIGestor/pom.xml clean verify
    java -jar Ejercicio5MyAPIGestor/target/Ejercicio5MyAPIGestor-0.0.1-SNAPSHOT.jar

En otra terminal:

    java -jar Ejercicio5Cliente/target/Ejercicio5Cliente-0.0.1-SNAPSHOT.jar

Abrir http://localhost:8080. Las suites del cliente y la API se ejecutan sin MariaDB: el contexto de pruebas de la API utiliza H2. Para arrancar la API fuera de pruebas, iniciar MariaDB y definir sus credenciales. El módulo db-tests sí requiere la instancia MariaDB aislada de CI; ver db-tests/README.md.

## Jenkins y entrega
Ver ci/README.md y entrega-unidad2/ACTUALIZACION.md. Crear dos pipelines desde SCM con los Script Path Ejercicio5Cliente/Jenkinsfile y Ejercicio5MyAPIGestor/Jenkinsfile. Los reportes JaCoCo se generan en target/site/jacoco de cada servicio.

## Procedencia
Adaptación académica del proyecto admin-proyectos de Enrique1189: https://github.com/Enrique1189/admin-proyectos. Se conserva su estructura y se documenta aquí la actualización a Java 21 y la preparación de CI.

Los recursos multimedia grandes utilizan Git LFS. Instalar Git LFS antes de clonar; si ya se clonó, ejecutar git lfs install y git lfs pull antes de compilar.

## Práctica 3, Parte 1
La entrega específica, los cuatro POM identificados, el documento técnico, los reportes y el guion del video se encuentran en [practica-3/parte-1](practica-3/parte-1/README.md).

## Parte 2 y ramas
La implementación actual de esta rama es la Parte 2. Consultar [RAMAS.md](RAMAS.md) para main, develop, parte 1, parte 2 y upgrade; y [la entrega Parte 2](practica-3/parte-2/README.md) para pruebas completas, seguridad y los tres pipelines. Descargar [Practica3-Parte2.zip](Practica3-Parte2.zip) para abrir los reportes y el guion del video. Los POM de la Parte 1 son evidencia histórica, no los POM activos de esta rama.

La API utiliza BCrypt: las cuentas heredadas con contraseñas almacenadas en texto plano necesitan restablecimiento. No se migró ni modificó la base de trabajo. Definir DB_PASSWORD en el entorno de la API; ya no se usa una contraseña predeterminada.

# Proyecto Gestión del Proceso de Desarrollo de Software

Dos servicios Spring Boot actualizados a Java 21: Ejercicio5Cliente (puerto 8080) y Ejercicio5MyAPIGestor (puerto 8081).

## Requisitos
JDK 21, Maven 3.9.9 y MariaDB. Configurar JAVA_HOME para el JDK 21 y comprobar java -version y mvn -version.

## Base de datos
En el equipo de esta actividad ya existe admin-proyectos-mariadb: iniciarlo con docker start admin-proyectos-mariadb. No iniciar otra base sobre el puerto 3297.

Para una instalación nueva: copiar .env.example a .env, cambiar las claves y ejecutar docker compose up -d. El volumen nuevo importa db/schema.sql sin datos personales. No usar esta opción para reemplazar la base existente. La API utiliza dbgestor, usuario gestor y puerto 3297; definir DB_PASSWORD en el entorno de la API con la clave elegida.

## Compilar y ejecutar
Desde la raíz:

    mvn -f Ejercicio5Cliente/pom.xml clean verify -Dtest=ProyectosClienteTests
    mvn -f Ejercicio5MyAPIGestor/pom.xml clean verify -Dtest=ProyectosControllerTests
    java -jar Ejercicio5MyAPIGestor/target/Ejercicio5MyAPIGestor-0.0.1-SNAPSHOT.jar

En otra terminal:

    java -jar Ejercicio5Cliente/target/Ejercicio5Cliente-0.0.1-SNAPSHOT.jar

Abrir http://localhost:8080. Las pruebas MVC no requieren una base activa. La suite completa de la API requiere el esquema y las credenciales de MariaDB.

## Jenkins y entrega
Ver ci/README.md y entrega-unidad2/ACTUALIZACION.md. Crear dos pipelines desde SCM con los Script Path Ejercicio5Cliente/Jenkinsfile y Ejercicio5MyAPIGestor/Jenkinsfile. Los reportes JaCoCo se generan en target/site/jacoco de cada servicio.

## Procedencia
Adaptación académica del proyecto admin-proyectos de Enrique1189: https://github.com/Enrique1189/admin-proyectos. Se conserva su estructura y se documenta aquí la actualización a Java 21 y la preparación de CI.

Los recursos multimedia grandes utilizan Git LFS. Instalar Git LFS antes de clonar; si ya se clonó, ejecutar git lfs install y git lfs pull antes de compilar.

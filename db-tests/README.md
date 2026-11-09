# Validación MariaDB de CI

Módulo Maven de pruebas JDBC reales, sin código productivo. Requiere Java 21,
Maven 3.9.9 y una MariaDB de CI independiente de la base de la aplicación.
Usa JUnit 5.10.2, Surefire 3.2.5 y MariaDB Connector/J 3.5.11.

## Barrera antes de modificar el esquema

Las pruebas solo aceptan esta combinación:

- `CI_DB_URL=jdbc:mariadb://admin-proyectos-db-ci:3306/ci_gestor`
- `CI_DB_USER=ci_runner`
- `CI_DB_PASSWORD`: credencial proporcionada por el entorno; nunca por argumentos Maven.

La URL debe coincidir exactamente; se rechazan otros hosts, bases y parámetros.
La comprobación ocurre antes de conectar y se repite antes de ejecutar DDL.
Se comprueba además que el catálogo abierto sea `ci_gestor`.

**Las pruebas eliminan y recrean únicamente las seis tablas conocidas en esa base
CI.** No configurar estos valores para apuntar a producción ni dar permisos
globales o de root a `ci_runner`. El usuario debe tener permisos de creación,
eliminación y operaciones de datos solamente sobre `ci_gestor`.

El contenedor de CI se aprovisiona por separado, se conecta a la red de Jenkins,
no publica el puerto al host y no utiliza el volumen de la aplicación. El pipeline
no necesita un socket Docker ni afirma que haya creado ese contenedor.

## Ejecutar

Desde un agente en la misma red que la MariaDB de CI y con las tres variables
anteriores disponibles:

```bash
mvn -B -ntp -f db-tests/pom.xml clean verify
```

Fuera de ese entorno las pruebas **fallan con instrucciones**, en lugar de intentar
la base de la aplicación o de omitir silenciosamente las comprobaciones. Para
comprobar únicamente que las pruebas compilan, sin conectar a MariaDB:

```bash
mvn -B -ntp -f db-tests/pom.xml clean test -DskipTests
```

El segundo comando no es evidencia de pruebas de base de datos aprobadas.

## Qué comprueba

1. Rechazo de destinos ajenos a la base CI.
2. Limpieza e importación repetida de `db/schema.sql`, incluidos sus datos técnicos.
3. Las seis tablas esperadas, sin tablas ajenas.
4. Todas las columnas y sus tipos SQL/JDBC esperados.
5. Una única fila inicial de `hibernate_sequence`, con valor `1`.
6. Crear, consultar, modificar y eliminar proyectos con nombres que contienen
   apóstrofes, mediante `PreparedStatement`.
7. Rollback que elimina registros insertados exclusivamente para las pruebas.
8. Rechazo de un ID duplicado por la clave primaria, sin conservar los datos de prueba.

El importador está limitado al esquema SQL sencillo del repositorio; no es un
lector general de procedimientos ni de volcados arbitrarios. El esquema actual
contiene índices, pero no declara claves foráneas: estas pruebas no atribuyen una
validación de relaciones que el esquema no define.

Las operaciones transaccionales se revierten incluso cuando falla una aserción.
La estructura del esquema CI permanece disponible; el siguiente build la limpia
y recrea. El trabajo deshabilita ejecuciones concurrentes sobre esa base.

## Reportes y Jenkins

`db/Jenkinsfile` usa las herramientas `JDK21` y `Maven3`, lee el usuario y la
contraseña de la credencial Jenkins `ci-db`, y fija el host de CI. El script de
análisis del esquema es `ci/scan_source.py`, provisto por la entrega principal.

Surefire genera `db-tests/target/surefire-reports/`. El pipeline publica esos
resultados. Después de las pruebas, analiza el esquema mediante
`ci/scan_source.py` y ejecuta `ci/security_scan.sh db-tests` para generar el
inventario CycloneDX y los reportes OSV y Gitleaks del módulo. Los reportes de
seguridad quedan en `ci-reports/db-tests/`, fuera de `target`, y se archivan
junto con los resultados de pruebas. El estado de ese análisis se conserva:
no se convierten errores o hallazgos en un resultado limpio.

No se agrega cobertura ficticia: este módulo solamente contiene pruebas y no
clases productivas evaluables para JaCoCo. El criterio de cobertura de los
servicios se comprueba en los módulos Front y API.

Fuentes: [Connector/J 3.5.11](https://mariadb.com/docs/release-notes/connectors/java/3.5/3.5.11)
y [JUnit 5.10.2](https://docs.junit.org/5.10.2/user-guide/).

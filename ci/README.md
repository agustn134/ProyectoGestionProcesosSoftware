# Jenkins reproducible para la Parte 2

Desde la raíz del repositorio:

    docker build -t admin-proyectos-jenkins:parte2-java21 ci

La imagen conserva Jenkins 2.541.3, JDK 21 y las instalaciones JDK21 y Maven3 (3.9.9). Incluye Python 3, OSV-Scanner 2.7.0 y Gitleaks 8.30.1 para ejecutar los análisis sin montar el socket Docker en Jenkins.

Mantener el volumen jenkins_home, publicar únicamente 127.0.0.1:8090:8080 y conectar Jenkins a calidad-ci para acceder a SonarQube. No ejecutar dos controladores simultáneamente sobre el mismo volumen. Los secretos permanecen en Jenkins Credentials: sonar-token y ci-db.

## Base de CI
Crear la red calidad-practica3 y una MariaDB independiente mediante compose.ci.yaml, definiendo CI_DB_PASSWORD y CI_DB_ROOT_PASSWORD fuera de Git. Conectar también Jenkins a esa red. El contenedor no publica puertos y utiliza datos efímeros en tmpfs; no comparte el volumen ni las credenciales de la base de trabajo.

La credencial ci-db es Username with password: usuario ci_runner y contraseña correspondiente al contenedor CI. Su permiso se limita a ci_gestor. El módulo db-tests rechaza otra URL/usuario antes de reconstruir el esquema.

## Tres pipelines
- Frontend: Ejercicio5Cliente/Jenkinsfile.
- Backend: Ejercicio5MyAPIGestor/Jenkinsfile.
- Base de datos: db/Jenkinsfile.

Los tres trabajos se configuran desde SCM con la rama featured/parte-2. Los trabajos de la Parte 1 conservan main.

## Qué analiza cada herramienta
- SonarQube revisa código Java y pruebas; target, reportes de prácticas y ci-reports quedan fuera del análisis como código fuente. XML JaCoCo y bytecode de target se leen como insumos. sonar_gate.py espera procesamiento, verifica el commit y exige Quality Gate OK y cero vulnerabilidades reportadas.
- Maven/CycloneDX 2.9.1 resuelve el inventario de dependencias, incluidas las de prueba. OSV recibe solo ese SBOM; no recorre target ni POM históricos. No se declara limpio un inventario vacío ni un error de red.
- Gitleaks revisa el estado actual del módulo con todas sus fuentes y reglas predeterminadas. Redacta las coincidencias, excluye outputs generados y falla si encuentra un secreto. No representa un escaneo del historial Git.
- scan_source.py añade tres reglas explícitas para comparaciones null inseguras, contraseñas predeterminadas y SQL global. Complementa las herramientas anteriores; no es un SAST completo.

Los reportes quedan en ci-reports/<módulo>/ y en los artefactos de Jenkins. No se convierten errores en resultados limpios ni se suprimen vulnerabilidades para aprobar.

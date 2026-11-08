# Jenkins reproducible para la unidad 2

Construir desde la raíz: docker build -t admin-proyectos-jenkins:java21 ci
La imagen configura JDK21 (/opt/java/openjdk) y Maven3 con instalación automática de Maven 3.9.9. El script conserva otras herramientas; actualiza la definición llamada Maven3.
Usar el volumen existente jenkins_home y puerto 127.0.0.1:8090:8080. Nunca ejecutar dos controladores simultáneamente sobre ese volumen. Conectar a la red calidad-ci para acceder a SonarQube.

Migración local verificada: contenedor jenkins con esta imagen, volumen jenkins_home, red calidad-ci y puerto 127.0.0.1:8090. Jenkins reportó JDK 21.0.9 y confirmó la configuración de JDK21 y Maven3 3.9.9. El contenedor anterior quedó detenido como jenkins-java17-respaldo.

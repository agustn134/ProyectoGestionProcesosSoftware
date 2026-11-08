# Jenkins reproducible para la unidad 2

Construir desde la raíz: docker build -t admin-proyectos-jenkins:java21 ci
La imagen configura JDK21 (/opt/java/openjdk) y Maven3 con instalación automática de Maven 3.9.9. El script conserva otras herramientas; actualiza la definición llamada Maven3.
Usar el volumen existente jenkins_home y puerto 127.0.0.1:8090:8080. Nunca ejecutar dos controladores simultáneamente sobre ese volumen. Conectar a la red calidad-ci para acceder a SonarQube.

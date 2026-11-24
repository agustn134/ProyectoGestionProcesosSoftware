FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN if head -c 50 src/main/resources/static/Images/manual.mp4 | grep -q git-lfs; then curl --fail --location --retry 3 https://media.githubusercontent.com/media/agustn134/ProyectoGestionProcesosSoftware/featured/parte-2/Ejercicio5Cliente/src/main/resources/static/Images/manual.mp4 --output src/main/resources/static/Images/manual.mp4; fi
RUN if head -c 50 src/main/resources/static/Images/tutorial.mp4 | grep -q git-lfs; then curl --fail --location --retry 3 https://media.githubusercontent.com/media/agustn134/ProyectoGestionProcesosSoftware/featured/parte-2/Ejercicio5Cliente/src/main/resources/static/Images/tutorial.mp4 --output src/main/resources/static/Images/tutorial.mp4; fi
RUN mvn -B -ntp -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app app
COPY --from=build /build/target/Ejercicio5Cliente-0.0.1-SNAPSHOT.jar /app/app.jar
USER app
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65 -XX:+ExitOnOutOfMemoryError"
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/ProjetInfraIoT-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

ENTRYPOINT ["java", "-cp", "app.jar", "com.laverie.machine.MachineALaver"]
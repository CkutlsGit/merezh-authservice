FROM eclipse-temurin:21-alpine

WORKDIR /app

RUN addgroup -S authservice && adduser -S authservice -G authservice

COPY target/authservice-merezh-0.0.1-SNAPSHOT.jar /app/authservice-merezh.jar

RUN chown -R authservice:authservice /app

USER authservice

ENTRYPOINT ["java", "-jar", "authservice-merezh.jar"]
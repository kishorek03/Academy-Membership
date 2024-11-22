FROM openjdk:17-jdk-alpine
COPY target/Academy-0.0.1-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "Academy-0.0.1-SNAPSHOT.jar"]




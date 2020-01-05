FROM openjdk:12-jdk-alpine
MAINTAINER daan@hoogland.io
ARG JAR_FILE="target/docker/app.jar"
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
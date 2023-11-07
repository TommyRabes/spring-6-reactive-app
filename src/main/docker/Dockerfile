FROM openjdk:21-jdk-slim

ENV JAVA_OPTS " -Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

WORKDIR application

COPY target/spring-6-reactive-app-0.0.1-SNAPSHOT.jar ./

ENTRYPOINT ["java", "-jar", "spring-6-reactive-app-0.0.1-SNAPSHOT.jar", "--mongo.username=Tommy", "--mongo.password=123456"]
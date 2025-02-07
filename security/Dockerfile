FROM gitlab-hc.hashconnect.in:5050/devops/docker-images:alpine-java-21-jdk
ARG JAR_FILE=build/libs/admin-api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} admin-api-service.jar
ENTRYPOINT ["java","-jar","/admin-api-service.jar"]

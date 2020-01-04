FROM openjdk:alpine
ADD target/metadata-service.jar metadata-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Xms256m", "-Xmx512m","/metadata-service.jar"]
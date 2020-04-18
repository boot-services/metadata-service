# ===========   BUILD STAGE =====================
FROM maven AS build
WORKDIR /workspace/app

# build maven .m2 cache as layer for reuse
COPY pom.xml pom.xml
RUN mvn dependency:go-offline -B

# build application as fat executable JAR
COPY src src
RUN mvn package -DskipTests


# ===========  RUN STAGE =====================
FROM openjdk:alpine
COPY --from=build /workspace/app/target/metadata-service.jar metadata-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Xms256m", "-Xmx512m","metadata-service.jar"]
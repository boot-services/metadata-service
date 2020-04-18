# ===========   BUILD STAGE =====================
FROM maven AS build
WORKDIR /workspace/app

# build maven .m2 cache as layer for reuse
COPY pom.xml pom.xml
RUN mvn dependency:go-offline -B

# build application as fat executable JAR
COPY src src
RUN mvn package -DskipTests
# explod fat executable JAR for COPY in RUN stage
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)


# ===========  RUN STAGE =====================
FROM openjdk:alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
EXPOSE 8080
ENTRYPOINT ["java","-cp","app:app/lib/*","org.boot.services.metadata.Application"]
FROM alpine:3.12.0 as build
RUN apk --no-cache add openjdk11
RUN /usr/lib/jvm/default-jvm/bin/jlink \
    --compress=2 \
    --module-path /usr/lib/jvm/default-jvm/jmods \
    --add-modules java.base,java.logging,java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument \
    --output /jdk-minimal



FROM alpine:3.12.0
COPY --from=build /jdk-minimal /opt/jdk/

COPY target/dependency/BOOT-INF/lib /app/lib
COPY target/dependency/META-INF /app/META-INF
COPY target/dependency/BOOT-INF/classes /app

VOLUME /tmp
EXPOSE 8080

CMD /opt/jdk/bin/java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -cp app:app/lib/* org.boot.services.metadata.Application
FROM docker.io/library/gradle:8.10.1-jdk21-jammy AS builder

WORKDIR /application
COPY . .
RUN --mount=type=cache,target=/root/.gradle \
    gradle bootJar --no-daemon && \
    cd build && \
    java -Djarmode=tools -jar libs/application.jar extract --layers --launcher

FROM docker.io/library/eclipse-temurin:21.0.4_7-jre
WORKDIR /application
EXPOSE 8080
# Potential point of hard-to-find problems; as we hardcode layers found in layers.idx
COPY --from=builder /application/build/application/spring-boot-loader/ ./
COPY --from=builder /application/build/application/snapshot-dependencies/ ./
COPY --from=builder /application/build/application/dependencies/ ./
COPY --from=builder /application/build/application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

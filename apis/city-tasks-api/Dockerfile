#####################################################################################
############################# Stage 1: Builder Image ################################
#####################################################################################
FROM hiperium/city-image-builder:1.8.0 AS builder

COPY pom.xml                         pom.xml
COPY apis/city-tasks-api/pom.xml     apis/city-tasks-api/pom.xml
RUN mvn dependency:go-offline -B -f  apis/city-tasks-api/pom.xml
COPY apis/city-tasks-api/src         apis/city-tasks-api/src

RUN mvn clean package -DskipTests -f apis/city-tasks-api/pom.xml -Ddependency-check.skip=true && \
    mkdir -p apis/city-tasks-api/target/dependency                                            && \
    (cd apis/city-tasks-api/target/dependency; jar -xf ../*.jar)

#####################################################################################
############################ Stage 2: Application Image #############################
#####################################################################################
FROM amazoncorretto:17.0.7-al2023-headless
ARG DEPENDENCY=/workspace/apis/city-tasks-api/target/dependency

COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes  /app
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib      /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF          /app/META-INF

EXPOSE 8080
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.hiperium.city.tasks.api.TasksApplication"]

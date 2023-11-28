#####################################################################################
############################# Stage 1: Builder Image ################################
#####################################################################################
FROM hiperium/city-image-builder:1.8.0 AS builder

COPY pom.xml                            pom.xml
COPY apis/city-events-function/pom.xml  apis/city-events-function/pom.xml
RUN mvn dependency:go-offline -B -f     apis/city-events-function/pom.xml
COPY apis/city-events-function/src      apis/city-events-function/src
COPY apis/city-events-function/tools    apis/city-events-function/tools

RUN mvn -T 4C clean package -DskipTests -f apis/city-events-function/pom.xml -Ddependency-check.skip=true && \
    mkdir -p apis/city-events-function/target/extracted                                 && \
    (cd apis/city-events-function/target; unzip java-assembly.zip -d extracted)         && \
    (cd apis/city-events-function/target/dependencies; zip -r ../dependencies.zip .)

#####################################################################################
############################ Stage 2: Application Image #############################
#####################################################################################
FROM public.ecr.aws/lambda/java:17
ARG TARGET=/workspace/apis/city-events-function/target

COPY --from=builder ${TARGET}/extracted         ${LAMBDA_TASK_ROOT}
COPY --from=builder ${TARGET}/*.jar             /tmp/city/shared/city-events-function.jar
COPY --from=builder ${TARGET}/dependencies.zip  /tmp/city/shared/dependencies.zip

EXPOSE 8080
CMD [ "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest" ]

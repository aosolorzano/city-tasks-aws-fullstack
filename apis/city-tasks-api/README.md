## Hiperium City Tasks API

This project contains source code
and supports files for a containerized application
that you can deploy with the Copilot CLI. It includes the following files and folders.

- src/main - Code for the container application.
- src/test - Integration tests for the application code.
- src/main/resources - Spring Boot configuration files.

This application manages Quartz Jobs with the help of Spring Webflux and Spring Native.

---

## Running using Docker Compose and LocalStack.
If you want to use tha Spring Boot Native version of the Tasks API service, you need to update the `compose.yaml` file 
and change the `apis/city-tasks-api/Dockerfile` property value to `apis/city-tasks-api/Dockerfile` in the `tasks-api` service.

*IMPORTANT:* The GraalVM `native-image` compiler should be installed and configured on your machine. 
If you're experiencing JMV memory issues, execute the following commands to increase the JVM memory and execute Maven with more CPU cores:

```bash
export _JAVA_OPTIONS="-Xmx8g -Xms4g"
mvn -T 4C clean native:compile -Pnative -DskipTests -f apis/city-tasks-api/pom.xml -Ddependency-check.skip=true
```

Use the Hiperium Login command to authenticate with your AWS IAM Identity Center with the name of your IdP profile:
```bash
hiperium-login
```

Then, execute the main a shell script selecting option `1. Docker Compose.` to build and deploy the project locally using Docker Compose.
```bash
./run-scripts.sh
```

Open a new terminal tab and edit your `/etc/hosts` file adding a new entry point to access the API service using HTTPS:
```bash
vim /etc/hosts
```

Add the following line and save the file:
```bash
127.0.0.1 dev.hiperium.cloud
```

Open your Postman and import the collection `Hiperium.postman_collection.json` to test the API endpoints. 
First, add a new Quartz Job using the `POST /api/v1/jobs` endpoint and then, execute the `POST /api/v1/tasks` endpoint using a near execution date time.
Notice when the Task is executed by the Quartz Scheduler, you must see the logs in the docker-compose terminal window.

---

## Running Integration Tests against Native Image.
You can also run your existing tests suite in a native image.
This is an efficient way to validate the compatibility of your application:
```bash
mvn -T 2C test -PnativeTest -f apis/city-tasks-api/pom.xml
```

---

## Generate Lightweight Container with the Cloud Native Buildpacks
If you're already familiar with Spring Boot container images support, this is the easiest way to get started.
Docker should be installed and configured on your machine prior to creating the image.

To create the image, run the following goal:
```bash
$ mvn spring-boot:build-image -Pnative -DskipTests
```

Then, you can run the app like any other container:
```bash
$ docker run --rm city-tasks-api:1.8.0
```

---

## Generate Native Executable with the Native Build Tools
Use this option if you want to explore more options such as running your tests in a native image.
The GraalVM `native-image` compiler should be installed and configured on your machine.

**NOTE:** GraalVM 22.3+ is required.

To create the executable, run the following goal:
```bash
$ mvn native:compile -Pnative -DskipTests
```

Then, you can run the app as follows:
```bash
$ target/city-tasks-api
```

---

## Getting Device items from DynamoDB on LocalStack.
Execute the following command:
```bash
awslocal dynamodb scan --table-name Devices
```

---

## AWS Copilot CLI Helpful Commands.

* List all of your AWS Copilot applications.
```bash
copilot app ls
```

* Show information about the environments and services in your application.
```bash
copilot app show
```

* Show information about your environments.
```bash
copilot env ls
```

* List of all the services in an application.
```bash
copilot svc ls
```

* Show service status.
```bash
copilot svc status
```

* Show information about the service, including endpoints, capacity and related resources.
```bash
copilot svc show
```

* Show logs of a deployed service.
```bash
export AWS_PROFILE=tasks-dep-dev
copilot svc logs          \
    --app city-tasks      \
    --name api            \
    --env dev             \
    --since 1h            \
    --follow
```

* Start an interactive bash session with a task part of the service:
```bash
copilot svc exec        \
    --app city-tasks    \
    --name api          \
    --env dev
```

* Delete and clean-up all created resources.
```bash
copilot app delete --yes
```

---

## Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.1/maven-plugin/reference/html/#build-image)
* [GraalVM Native Image Support](https://docs.spring.io/spring-boot/docs/3.1.1/reference/html/native-image.html#native-image)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#web.reactive)
* [Testcontainers](https://www.testcontainers.org/)
* [Testcontainers Postgres Module Reference Guide](https://www.testcontainers.org/modules/databases/postgres/)
* [OAuth2 Authorization Server](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#web.security.oauth2.authorization-server)
* [OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/reactive/oauth2/resource-server/)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#data.sql.jpa-and-spring-data)
* [Flyway Migration](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#howto.data-initialization.migration-tool.flyway)
* [Quartz Scheduler](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#io.quartz)
* [Validation](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#io.validation)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/3.1.1/reference/htmlsingle/#actuator)

---

## Guides
The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

---

## Additional Links
These additional references should also help you:

* [Configure AOT settings in Build Plugin](https://docs.spring.io/spring-boot/docs/3.1.1/maven-plugin/reference/htmlsingle/#aot)

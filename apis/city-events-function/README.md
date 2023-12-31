## Hiperium City Tasks Lambda Function

This project contains source code and supports files for a serverless application that you can deploy with the SAM CLI. It includes the following files and folders.

- template.yaml—SAM template at the project's root directory.
- src/main - Code for the application's Lambda function.
- src/test - Unit tests for the application code. 
- src/test/resources/events - Invocation events that you can use to invoke the function in Integration Tests.

This application reacts to EventBridge custom events, demonstrating the power of Event-Driven Development.
The application uses several AWS resources, including Lambda functions and an EventBridge Rule. These resources are defined in the `template.yaml` file in the parent project. You can update the template to add AWS resources through the same deployment process that updates your application code.

---

## GraalVM
This Lambda Function uses GraalVM to create a native binary of your Java application.
By compiling Java to a native executable, the performance is increased and cold-start is reduced.

This Lambda Function contains build configurations for Maven build systems
(see Makefile in the project's root directory).

A docker image is required to compile for the Lambda execution environment (based on Amazon Linux 2).
This image can be built using the following command from the project's root directory:
```bash
docker build                                        \
  -t hiperium/city-image-builder:1.8.0              \
  -f tools/docker/city-builder/Dockerfile-native .
```

This image is used when building the SAM package with the following command:
```bash
sam build --config-env dev
```

---

## Deploy the Lambda Function application
The Serverless Application Model Command Line Interface (SAM CLI) is an extension of the AWS CLI that adds functionality for building and testing Lambda applications. It uses Docker to run your functions in an Amazon Linux environment that matches Lambda. It can also emulate your application's build environment and API.

To deploy your application for the first time, run the following in your shell:
```bash
$ sam deploy                    \
  --configurations-env 'dev'    \
  --disable-rollback            \
  --profile 'tasks-dev'
```

The command will package and deploy your application to AWS.

---

## Use the SAM CLI to build and test locally
After you build the application, the SAM CLI installs dependencies defined in `apis/city-events-function/pom.xml`,
creates a deployment package, and saves it in the `.aws-sam/build` folder in the project's root directory.

To test a single function, invoke it directly with a test event.
An event is a JSON document that represents the input that the function receives from the event source.
Test events are included in the `apis/city-events-function/src/test/resources/events` folder in this project.
Run functions locally and invoke them with the `sam local invoke` command:

```bash
$ sam local invoke CityEventsFunction      \
  --events apis/city-events-function/src/test/resources/events/lambda-event-valid-detail.json
```

---

## Getting Events items from DynamoDB on LocalStack.
Execute the following command to get the created event items from DynamoDB:
```bash
awslocal dynamodb scan --table-name Events
```

---

## Fetch, tail, and filter Lambda function logs
To simplify troubleshooting, SAM CLI has a command called `sam logs`.
This command lets you fetch logs generated by your deployed Lambda function from the command line.
In addition to printing the logs on the terminal,
this command has several nifty features to help you quickly find the bug.
`NOTE`: This command works for all AWS Lambda functions; not just the ones you deploy using SAM.

```bash
$ sam logs -n CityEventsFunction               \
    --stack-name 'city-events-function-dev'   \
    --tail                                          \
    --profile 'tasks-dev'
```

You can find more information and examples
about filtering Lambda function logs in the [SAM CLI Documentation](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-logging.html).

---

## Integration tests using Testcontainers and LocalStack.
Tests are defined in the `apis/city-events-function/src/test` folder.
Execute the following command to run the tests from the project's root directory:

```bash
$ mvn test -f apis/city-events-function/pom.xml
```

---

## SAM CLI and APIs (not currently implemented).
The SAM CLI can also emulate your application's API. Use the `sam local start-api` to run the API locally on port 3000.

```bash
$ sam local start-api
$ curl http://localhost:3000/
```

The SAM CLI reads the application template to determine the API's routes and the functions that they invoke. The `Events` property on each function's definition includes the route and method for each path.
```yaml
Events:
  CityTasksEventsApi:
    Type: Api
    Properties:
      Path: /logs
      Method: get
```

---

## Add a resource to your application
The application template uses AWS Serverless Application Model (AWS SAM) to define application resources. AWS SAM is an extension of AWS CloudFormation with a simpler syntax for configuring common serverless application resources such as functions, triggers, and APIs. For resources not included in [the SAM specification](https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md), you can use standard [AWS CloudFormation](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-template-resource-type-ref.html) resource types.


---

## Cleanup
To delete the Lambda Function you created, use the AWS CLI. Assuming you used your project name for the stack name, you can run the following:
```bash
sam delete --stack-name 'city-events-function-dev'
```

---

## Resources
See the [AWS SAM developer guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html) for an introduction to SAM specification, the SAM CLI, and serverless application concepts.

Next, you can use AWS Serverless Application Repository to deploy ready-to-use Apps and learn how authors developed their applications: [AWS Serverless Application Repository main page](https://aws.amazon.com/serverless/serverlessrepo/).

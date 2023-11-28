package com.hiperium.city.events.function.common;

import com.hiperium.city.events.function.utils.PropertiesUtil;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public abstract class AbstractContainerBaseTest {

    protected static final LocalStackContainer LOCALSTACK_CONTAINER;

    // Singleton containers.
    // See: https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers
    static {
        LOCALSTACK_CONTAINER = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                .withServices(LocalStackContainer.Service.DYNAMODB)
                .withCopyToContainer(MountableFile.forClasspathResource("infra-setup.sh"),
                        "/etc/localstack/init/ready.d/events-setup.sh")
                .withCopyToContainer(MountableFile.forClasspathResource("data-setup.json"),
                        "/var/lib/localstack/events-data.json");

        LOCALSTACK_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("aws.region", LOCALSTACK_CONTAINER::getRegion);
        registry.add("aws.accessKeyId", LOCALSTACK_CONTAINER::getAccessKey);
        registry.add("aws.secretAccessKey", LOCALSTACK_CONTAINER::getSecretKey);
        registry.add(PropertiesUtil.AWS_ENDPOINT_OVERRIDE_PROPERTY, () ->
                LOCALSTACK_CONTAINER.getEndpoint().toString());
    }
}

package com.hiperium.city.tasks.api.common;

import com.hiperium.city.tasks.api.utils.PropertiesUtil;
import com.hiperium.city.tasks.api.utils.TestsUtil;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.util.Objects;

public abstract class AbstractContainerBaseTest {

    protected static final String AUTHORIZATION = "Authorization";

    private static final KeycloakContainer KEYCLOAK_CONTAINER;
    private static final PostgreSQLContainer POSTGRES_CONTAINER;
    private static final LocalStackContainer LOCALSTACK_CONTAINER;

    // Singleton containers.
    // See: https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers
    static {
        KEYCLOAK_CONTAINER = new KeycloakContainer()
                .withRealmImportFile("keycloak-realm.json");

        POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:14.4")
                .withUsername("postgres")
                .withPassword("postgres123")
                .withDatabaseName("CityTasksDB");

        LOCALSTACK_CONTAINER = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                .withServices(LocalStackContainer.Service.DYNAMODB)
                .withCopyToContainer(MountableFile.forClasspathResource("infra-setup.sh"),
                        "/etc/localstack/init/ready.d/api-setup.sh")
                .withCopyToContainer(MountableFile.forClasspathResource("data-setup.json"),
                        "/var/lib/localstack/api-data.json");

        KEYCLOAK_CONTAINER.start();
        POSTGRES_CONTAINER.start();
        LOCALSTACK_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        // SPRING SECURITY OAUTH2 JWT
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> KEYCLOAK_CONTAINER.getAuthServerUrl() + TestsUtil.KEYCLOAK_REALM);

        // SPRING DATA JDBC CONNECTION
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> TestsUtil.POSTGRESQL_DRIVER);

        // SPRING QUARTZ JDBC CONNECTION
        registry.add("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.URL",
                POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.user",
                POSTGRES_CONTAINER::getUsername);
        registry.add("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.password",
                POSTGRES_CONTAINER::getPassword);
        registry.add("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.driver",
                () -> TestsUtil.POSTGRESQL_DRIVER);
        registry.add("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.provider",
                () -> TestsUtil.QUARTZ_DS_PROVIDER);

        // AWS REGION, CREDENTIALS, AND ENDPOINT-OVERRIDE
        registry.add("aws.region", LOCALSTACK_CONTAINER::getRegion);
        registry.add("aws.accessKeyId", LOCALSTACK_CONTAINER::getAccessKey);
        registry.add("aws.secretAccessKey", LOCALSTACK_CONTAINER::getSecretKey);
        registry.add(PropertiesUtil.AWS_ENDPOINT_OVERRIDE_PROPERTY, () ->
                LOCALSTACK_CONTAINER.getEndpoint().toString());
    }

    protected String getBearerAccessToken() {
        Keycloak keycloakClient = KEYCLOAK_CONTAINER.getKeycloakAdminClient();
        AccessTokenResponse accessTokenResponse = keycloakClient.tokenManager().getAccessToken();
        return "Bearer " + Objects.requireNonNull(accessTokenResponse).getToken();
    }
}

package com.hiperium.city.tasks.api.utils;

import com.hiperium.city.tasks.api.models.Device;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestsUtil {

    public static final String KEYCLOAK_REALM = "realms/master";
    public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
    public static final String QUARTZ_DS_PROVIDER = "hikaricp";

    public static void waitForTableToBecomeActive(final DynamoDbClient dynamoDbClient) {
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))         // maximum wait time
                .pollInterval(Duration.ofSeconds(1))    // check every second
                .until(() -> {
                    DescribeTableRequest request = DescribeTableRequest.builder()
                            .tableName(Device.TABLE_NAME)
                            .build();
                    TableStatus tableStatus = dynamoDbClient.describeTable(request).table().tableStatus();
                    return TableStatus.ACTIVE.equals(tableStatus);
                });
    }
}

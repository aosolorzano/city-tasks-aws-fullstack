package com.hiperium.city.events.function.utils;

import com.hiperium.city.events.function.models.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestsUtil {

    public static void waitForTableToBecomeActive(final DynamoDbClient dynamoDbClient) {
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))         // maximum wait time
                .pollInterval(Duration.ofSeconds(1))    // check every second
                .until(() -> {
                    DescribeTableRequest request = DescribeTableRequest.builder()
                            .tableName(Event.TABLE_NAME)
                            .build();
                    TableStatus tableStatus = dynamoDbClient.describeTable(request).table().tableStatus();
                    return TableStatus.ACTIVE.equals(tableStatus);
                });
    }
}

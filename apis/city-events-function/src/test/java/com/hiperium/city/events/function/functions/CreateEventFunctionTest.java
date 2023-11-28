package com.hiperium.city.events.function.functions;

import com.hiperium.city.events.function.common.AbstractContainerBaseTest;
import com.hiperium.city.events.function.models.EventBridgeCustomEvent;
import com.hiperium.city.events.function.models.EventsResponse;
import com.hiperium.city.events.function.utils.FunctionsUtil;
import com.hiperium.city.events.function.utils.TestsUtil;
import jakarta.validation.ValidationException;
import org.assertj.core.api.Condition;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@FunctionalSpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class CreateEventFunctionTest extends AbstractContainerBaseTest {

    @Autowired
    private FunctionCatalog catalog;

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @Test
    @Order(1)
    @DisplayName("Wait for DynamoDB to be ready")
    void givenDynamoDbTable_whenCreated_mustNotThrownError() {
        TestsUtil.waitForTableToBecomeActive(this.dynamoDbClient);
        assertTrue(true);
    }

    @Test
    @Order(2)
    @DisplayName("Valid event")
    void givenValidEvent_whenInvokeLambdaFunction_thenExecuteSuccessfully() throws IOException {
        Function<EventBridgeCustomEvent, EventsResponse> function = this.getFunction();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/lambda-event-valid-detail.json")) {
            assert inputStream != null;
            EventBridgeCustomEvent event = FunctionsUtil.unmarshal(inputStream.readAllBytes(), EventBridgeCustomEvent.class);
            assertThat(function.apply(event)).has(new Condition<>(response -> response.getStatusCode() == 201, "Status code must be 201"));
        }
    }

    @Order(3)
    @ParameterizedTest
    @DisplayName("Invalid events")
    @ValueSource(strings = {
            "events/lambda-event-invalid-detail.json",
            "events/lambda-event-without-detail.json"})
    void givenInvalidEvents_whenInvokeLambdaFunction_thenThrowsException(String jsonFilePath) throws IOException {
        Function<EventBridgeCustomEvent, EventsResponse> function = this.getFunction();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonFilePath)) {
            assert inputStream != null;
            EventBridgeCustomEvent event = FunctionsUtil.unmarshal(inputStream.readAllBytes(), EventBridgeCustomEvent.class);
            assertThrows(ValidationException.class, () -> function.apply(event));
        }
    }

    @NotNull
    private Function<EventBridgeCustomEvent, EventsResponse> getFunction() {
        Function<EventBridgeCustomEvent, EventsResponse> function = this.catalog.lookup(Function.class,
                CreateEventFunction.class.getCanonicalName());
        assertNotNull(function, "Function not found.");
        return function;
    }
}

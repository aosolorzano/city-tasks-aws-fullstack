package com.hiperium.city.events.function.utils;

import com.hiperium.city.events.function.models.EventBridgeCustomEvent;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FunctionUtilTest {

    @Test
    void givenInvalidEvent_whenUnmarshal_thenThrowsException() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("events/lambda-event-invalid-operation.json")) {
            assert inputStream != null;
            Exception exception = assertThrows(IllegalArgumentException.class, () ->
                    FunctionsUtil.unmarshal(inputStream.readAllBytes(), EventBridgeCustomEvent.class)
            );
            String expectedMessage = "Error unmarshalling the <EventBridgeCustomEvent> object:";
            assertTrue(exception.getMessage().contains(expectedMessage));
        }

    }
}

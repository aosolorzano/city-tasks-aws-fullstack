package com.hiperium.city.events.function.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiperium.city.events.function.models.EventsResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FunctionsUtil {

    private static final Map<String, String> HEADERS = new HashMap<>();

    static {
        HEADERS.put("Content-Type", "application/json");
        HEADERS.put("X-Custom-Header", "application/json");
    }

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static <T> T unmarshal(byte[] jsonBytes, Class<T> type) {
        try {
            return MAPPER.readValue(jsonBytes, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error unmarshalling the <" + type.getSimpleName() + "> object: "
                    + e.getMessage());
        }
    }

    public static EventsResponse getSuccessResponse() {
        return EventsResponse.builder()
                .statusCode(201)
                .headers(HEADERS)
                .body("{}")
                .build();
    }
}

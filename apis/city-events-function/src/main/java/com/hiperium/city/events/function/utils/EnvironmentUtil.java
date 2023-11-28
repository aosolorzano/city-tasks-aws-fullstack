package com.hiperium.city.events.function.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvironmentUtil {

    public static final String TIME_ZONE_ID_ENV_VARIABLE = "CITY_TIME_ZONE_ID";
    public static final String AWS_ENDPOINT_OVERRIDE_ENV_VARIABLE = "AWS_ENDPOINT_OVERRIDE";

    public static String getTimeZoneId() {
        String timeZoneId = System.getenv(TIME_ZONE_ID_ENV_VARIABLE);
        if (Objects.isNull(timeZoneId) || timeZoneId.isBlank()) {
            log.warn("{} variable was not found. Using defaults.", TIME_ZONE_ID_ENV_VARIABLE);
        }
        return timeZoneId;
    }

    public static String getAwsEndpointOverrideURL() {
        String endpointOverrideURL = System.getenv(AWS_ENDPOINT_OVERRIDE_ENV_VARIABLE);
        if (Objects.nonNull(endpointOverrideURL) && !endpointOverrideURL.isBlank()) {
            log.warn("AWS Endpoint Override URL detected. This will override the default AWS endpoint URL. " +
                    "Set the '{}' variable to null or empty if you want to use the default AWS endpoint.",
                    AWS_ENDPOINT_OVERRIDE_ENV_VARIABLE);
        } else {
            endpointOverrideURL = System.getenv("AWS_ENDPOINT_URL");
            if (Objects.nonNull(endpointOverrideURL) && !endpointOverrideURL.isEmpty() && !endpointOverrideURL.endsWith(".amazonaws.com")) {
                log.warn("AWS Endpoint Override URL detected. This will override the default AWS endpoint URL. " +
                        "Remove the 'AWS_ENDPOINT_URL' variable if you want to use the default AWS endpoint.");
            }
        }
        return endpointOverrideURL;
    }
}

package com.hiperium.city.events.function.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventsResponse {

    private int statusCode;
    private Map<String, String> headers;
    private String body;
}

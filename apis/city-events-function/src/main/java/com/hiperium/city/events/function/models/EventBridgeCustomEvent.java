package com.hiperium.city.events.function.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@Data
public class EventBridgeCustomEvent {

    @NotEmpty
    private String version;

    @NotEmpty
    private String id;

    @NotEmpty
    @ToString.Exclude
    private String account;

    @NotEmpty
    private String source;

    @NotEmpty
    private String region;

    @NotNull
    private Instant time;

    private List<String> resources;

    @JsonProperty("detail-type")
    private String detailType;

    @NotNull
    private TaskEventDetail detail;
}

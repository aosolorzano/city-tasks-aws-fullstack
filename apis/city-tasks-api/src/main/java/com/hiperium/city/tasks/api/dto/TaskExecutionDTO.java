package com.hiperium.city.tasks.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecutionDTO {

    @JsonProperty("taskId")
    private Long taskId;

    @JsonProperty("deviceId")
    private String deviceId;

    @JsonProperty("deviceOperation")
    private EnumDeviceOperation deviceOperation;
}

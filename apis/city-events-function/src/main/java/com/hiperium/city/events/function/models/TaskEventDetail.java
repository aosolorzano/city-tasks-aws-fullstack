package com.hiperium.city.events.function.models;

import com.hiperium.city.events.function.utils.enums.EnumDeviceOperation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskEventDetail {

    @NotNull
    @Min(value = 1)
    private Long taskId;

    @NotEmpty
    private String deviceId;

    @NotNull
    private EnumDeviceOperation deviceOperation;
}

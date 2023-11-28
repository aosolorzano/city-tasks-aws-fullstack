package com.hiperium.city.tasks.api.dto;

import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumTaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    @Schema(description = "Required for update and delete operations.", example = "8585")
    private Long id;

    @NotEmpty(message = "validation.task.name.NotEmpty.message")
    private String name;

    private String description;

    @Schema(description = "Active by default when creating the Task.", example = "ACT")
    private EnumTaskStatus status;

    @Schema(example = "123")
    @NotEmpty(message = "validation.device.id.NotEmpty.message")
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "validation.device.action.NotEmpty.message")
    @Schema(description = "Operation performed against the Device when Task is triggered.", example = "ACTIVATE")
    private EnumDeviceOperation deviceOperation;

    @Schema(description = "Time of the day in 24/hours format when the Task will be triggered.", example = "20")
    @Min(value = 0, message = "validation.task.hour.Min.message")
    @Max(value = 23, message = "validation.task.hour.Max.message")
    private int hour;

    @Schema(description = "Minute of the day when the Task will be triggered.", example = "30")
    @Min(value = 0, message = "validation.task.minute.Min.message")
    @Max(value = 59, message = "validation.task.minute.Max.message")
    private int minute;

    @Schema(description = "Days of the week when the Task will be triggered.", example = "MON,TUE,SAT")
    @NotEmpty(message = "validation.task.execution.days.NotEmpty.message")
    private String executionDays;

    @Schema(description = "The last DateTime until which the Task will be triggered.", example = "2021-08-01T00:00:00.000Z")
    @Future(message = "validation.task.execute.until.Future.message")
    private ZonedDateTime executeUntil;
}

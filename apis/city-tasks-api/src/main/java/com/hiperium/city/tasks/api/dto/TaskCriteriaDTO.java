package com.hiperium.city.tasks.api.dto;

import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumTaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCriteriaDTO {

    @Schema(example = "8585")
    @Min(value = 0, message = "validation.criteria.id.Min.message")
    private Long id;

    private String name;

    @Schema(example = "ACT")
    private EnumTaskStatus status;

    @Schema(example = "123")
    private String deviceId;

    @Schema(description = "Operation performed against the Device when Task is triggered.", example = "ACTIVATE")
    private EnumDeviceOperation deviceOperation;

    @Min(value = 0, message = "validation.task.hour.Min.message")
    @Max(value = 23, message = "validation.task.hour.Max.message")
    @Schema(description = "Time of the day in 24/hours format when the Task will be triggered.", example = "20")
    private Integer hour;

    @Min(value = 0, message = "validation.task.minute.Min.message")
    @Max(value = 59, message = "validation.task.minute.Max.message")
    @Schema(description = "Minute of the day when the Task will be triggered.", example = "30")
    private Integer minute;

    @Length(min = 3, max = 3, message = "validation.task.executionDay.length.message")
    @Schema(description = "Day of the week when the Task will be triggered.", example = "SUN")
    private String executionDay;
}

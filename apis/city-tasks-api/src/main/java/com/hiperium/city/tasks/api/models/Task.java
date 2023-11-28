package com.hiperium.city.tasks.api.models;

import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumTaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "HIP_CTY_TASKS")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HIP_CTY_TASKS_SEQ")
    @SequenceGenerator(name = "HIP_CTY_TASKS_SEQ", sequenceName = "HIP_CTY_TASKS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 3, nullable = false)
    private EnumTaskStatus status;

    @Column(name = "job_id", length = 30, nullable = false)
    private String jobId;

    @Column(name = "device_id", length = 30, nullable = false)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_operation", length = 10, nullable = false)
    private EnumDeviceOperation deviceOperation;

    @Column(name = "device_execution_command", length = 90)
    private String deviceExecutionCommand;

    @Column(name = "task_hour", nullable = false)
    private Integer hour;

    @Column(name = "task_minute", nullable = false)
    private Integer minute;

    @Column(name = "execution_days", length = 30, nullable = false)
    private String executionDays;

    @Column(name = "execute_until")
    private ZonedDateTime executeUntil;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}

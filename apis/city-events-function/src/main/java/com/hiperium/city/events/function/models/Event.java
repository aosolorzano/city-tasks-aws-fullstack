package com.hiperium.city.events.function.models;

import lombok.Data;

@Data
public class Event {

    public static final String TABLE_NAME = "Events";
    public static final String ID_FIELD = "id";
    public static final String DEVICE_ID_FIELD = "deviceId";
    public static final String TASK_ID_FIELD = "taskId";
    public static final String OPERATION_FIELD = "operation";
    public static final String EXECUTION_DATE_FIELD = "executionDate";
    public static final String EXECUTION_INSTANT_FIELD = "executionInstant";

    private String executionDate;
    private Long executionInstant;
}

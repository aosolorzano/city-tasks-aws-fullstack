package com.hiperium.city.tasks.api.utils.enums;

public enum EnumResourceError {

    TASK_NOT_FOUND("RSC-001", "task.not.found.message"),
    DEVICE_NOT_FOUND("RSC-002", "device.not.found.message"),
    TRIGGER_NOT_FOUND("RSC-003", "trigger.not.found.message");

    private final String code;
    private final String message;

    EnumResourceError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

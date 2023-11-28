package com.hiperium.city.tasks.api.utils.enums;

public enum EnumSchedulerError {

    SCHEDULE_JOB_ERROR("SCH-001", "schedule.job.error.message"),
    RESCHEDULE_JOB_ERROR("SCH-002", "reschedule.job.error.message"),
    UNSCHEDULE_JOB_ERROR("SCH-003", "unschedule.job.error.message"),
    GET_CURRENT_TRIGGER_ERROR("SCH-004", "get.current.trigger.error.message");

    private final String code;
    private final String message;

    EnumSchedulerError(String code, String message) {
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

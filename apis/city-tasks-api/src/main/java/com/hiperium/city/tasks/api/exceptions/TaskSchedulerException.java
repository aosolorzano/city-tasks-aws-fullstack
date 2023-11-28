package com.hiperium.city.tasks.api.exceptions;

import com.hiperium.city.tasks.api.utils.enums.EnumSchedulerError;

public class TaskSchedulerException extends ApplicationException {

    public TaskSchedulerException(org.quartz.SchedulerException e, EnumSchedulerError errorEnum, Object... args) {
        super(e, errorEnum.getCode(), errorEnum.getMessage(), args);
    }
}

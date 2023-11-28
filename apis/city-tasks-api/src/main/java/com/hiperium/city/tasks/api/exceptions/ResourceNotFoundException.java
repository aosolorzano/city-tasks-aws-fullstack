package com.hiperium.city.tasks.api.exceptions;

import com.hiperium.city.tasks.api.utils.enums.EnumResourceError;

public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(EnumResourceError errorEnum, Object... args) {
        super(errorEnum.getCode(), errorEnum.getMessage(), args);
    }
}

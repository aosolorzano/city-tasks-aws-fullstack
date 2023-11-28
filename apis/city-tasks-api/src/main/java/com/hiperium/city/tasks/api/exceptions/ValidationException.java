package com.hiperium.city.tasks.api.exceptions;


import com.hiperium.city.tasks.api.utils.enums.EnumValidationError;

public class ValidationException extends ApplicationException {

    public ValidationException(String message, Object... args) {
        super(EnumValidationError.FIELD_VALIDATION_ERROR.getCode(), message, args);
    }

    public ValidationException(EnumValidationError validationError, Object... args) {
        super(validationError.getCode(), validationError.getMessage(), args);
    }
}

package com.hiperium.city.tasks.api.utils.enums;

public enum EnumValidationError {

    FIELD_VALIDATION_ERROR("TSK-FLD-VAL", null), // USED FOR BEAN VALIDATION EXCEPTIONS ONLY.
    NO_CRITERIA_FOUND("TSK-CRT-001", "validation.task.criteria.params.NotNull.message");

    private final String code;
    private final String message;

    EnumValidationError(String code, String message) {
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

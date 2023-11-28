package com.hiperium.city.events.function.utils;

import com.hiperium.city.events.function.models.EventBridgeCustomEvent;
import com.hiperium.city.events.function.models.TaskEventDetail;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BeansValidationUtil {

    public static void validateBean(EventBridgeCustomEvent customEvent) {
        try (ValidatorFactory factory = buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<EventBridgeCustomEvent>> violations = validator.validate(customEvent);
            if (!violations.isEmpty()) {
                violations.stream()
                        .findFirst()
                        .ifPresent(BeansValidationUtil::throwValidationTaskDtoException);
            }
        }
    }

    public static void validateBean(TaskEventDetail taskEventDetail) {
        try (ValidatorFactory factory = buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<TaskEventDetail>> violations = validator.validate(taskEventDetail);
            if (!violations.isEmpty()) {
                violations.stream()
                        .findFirst()
                        .ifPresent(BeansValidationUtil::throwValidationTaskCriteriaDtoException);
            }
        }
    }

    private static void throwValidationTaskDtoException(ConstraintViolation<EventBridgeCustomEvent> constraintViolation) {
        throw new ValidationException(constraintViolation.getMessage());
    }

    private static void throwValidationTaskCriteriaDtoException(ConstraintViolation<TaskEventDetail> constraintViolation) {
        throw new ValidationException(constraintViolation.getMessage());
    }
}

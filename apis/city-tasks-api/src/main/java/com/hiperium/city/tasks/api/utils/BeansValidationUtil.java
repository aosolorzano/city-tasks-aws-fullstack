package com.hiperium.city.tasks.api.utils;

import com.hiperium.city.tasks.api.dto.TaskCriteriaDTO;
import com.hiperium.city.tasks.api.dto.TaskDTO;
import com.hiperium.city.tasks.api.exceptions.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BeansValidationUtil {

    public static void validateBean(TaskDTO dto) {
        try (ValidatorFactory factory = buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<TaskDTO>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                violations.stream()
                        .findFirst()
                        .ifPresent(BeansValidationUtil::throwValidationTaskDtoException);
            }
        }
    }

    public static void validateBean(TaskCriteriaDTO dto) {
        try (ValidatorFactory factory = buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<TaskCriteriaDTO>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                violations.stream()
                        .findFirst()
                        .ifPresent(BeansValidationUtil::throwValidationTaskCriteriaDtoException);
            }
        }
    }

    private static void throwValidationTaskDtoException(ConstraintViolation<TaskDTO> constraintViolation) {
        throw new ValidationException(constraintViolation.getMessage());
    }

    private static void throwValidationTaskCriteriaDtoException(ConstraintViolation<TaskCriteriaDTO> constraintViolation) {
        throw new ValidationException(constraintViolation.getMessage());
    }
}

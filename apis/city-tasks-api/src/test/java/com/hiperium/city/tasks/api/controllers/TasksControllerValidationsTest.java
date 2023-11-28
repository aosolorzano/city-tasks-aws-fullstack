package com.hiperium.city.tasks.api.controllers;

import com.hiperium.city.tasks.api.common.AbstractContainerBaseTest;
import com.hiperium.city.tasks.api.dto.ErrorDetailsDTO;
import com.hiperium.city.tasks.api.dto.TaskCriteriaDTO;
import com.hiperium.city.tasks.api.utils.enums.EnumLanguageCode;
import com.hiperium.city.tasks.api.utils.enums.EnumValidationError;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.hiperium.city.tasks.api.utils.PathsUtil.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@AutoConfigureWebTestClient
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TasksControllerValidationsTest extends AbstractContainerBaseTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Find Task with negative ID")
    void givenTaskWithNegativeId_whenFind_thenReturnValidationError() {
        TaskCriteriaDTO taskCriteriaDto = new TaskCriteriaDTO();
        taskCriteriaDto.setId(-1L);
        this.getValidationErrorResponse(taskCriteriaDto, EnumLanguageCode.EN)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Task ID must be greater than 0.");
                });
    }

    @Test
    @DisplayName("Find Task with negative ID - Spanish")
    void givenTaskWithNegativeId_whenFind_thenReturnValidationErrorInSpanish() {
        TaskCriteriaDTO taskCriteriaDto = new TaskCriteriaDTO();
        taskCriteriaDto.setId(-2L);
        this.getValidationErrorResponse(taskCriteriaDto, EnumLanguageCode.ES)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("El ID de la tarea debe ser mayor a 0.");
                });
    }

    @Test
    @DisplayName("Find Task with hour greater than 23")
    void givenTaskWithHourGreaterThan23_whenFind_thenReturnValidationError() {
        TaskCriteriaDTO taskCriteriaDto = new TaskCriteriaDTO();
        taskCriteriaDto.setHour(24);
        this.getValidationErrorResponse(taskCriteriaDto, EnumLanguageCode.EN)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Task hour must be less than or equal to 23.");
                });
    }

    @Test
    @DisplayName("Find Task with hour greater than 23 - Spanish")
    void givenTaskWithHourGreaterThan23_whenFind_thenReturnValidationErrorInSpanish() {
        TaskCriteriaDTO taskCriteriaDto = new TaskCriteriaDTO();
        taskCriteriaDto.setHour(25);
        this.getValidationErrorResponse(taskCriteriaDto, EnumLanguageCode.ES)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("La hora de la tarea debe ser menor o igual a 23.");
                });
    }

    @Test
    @DisplayName("Find Task with minute less than 0")
    void givenTaskWithMinuteLessThan0_whenFind_thenReturnValidationError() {
        TaskCriteriaDTO taskCriteriaDto = new TaskCriteriaDTO();
        taskCriteriaDto.setMinute(-1);
        this.getValidationErrorResponse(taskCriteriaDto, EnumLanguageCode.EN)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Task minute must be greater than or equal to 0.");
                });
    }

    @Test
    @DisplayName("Find Task with minute less than 0 - Spanish")
    void givenTaskWithMinuteLessThan0_whenFind_thenReturnValidationErrorInSpanish() {
        TaskCriteriaDTO taskCriteriaDto = new TaskCriteriaDTO();
        taskCriteriaDto.setMinute(-2);
        this.getValidationErrorResponse(taskCriteriaDto, EnumLanguageCode.ES)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("El minuto de la tarea debe ser mayor o igual a 0.");
                });
    }

    @Test
    @DisplayName("Find Task with execution days more than 3 characters")
    void givenTaskWithExecutionDayMoreThan3Characters_whenFind_thenReturnValidationError() {
        TaskCriteriaDTO taskCriteriaDto = new TaskCriteriaDTO();
        taskCriteriaDto.setExecutionDay("FRIDAY");
        this.getValidationErrorResponse(taskCriteriaDto, EnumLanguageCode.EN)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("Task execution day must have 3 characters length.");
                });
    }

    @Test
    @DisplayName("Find Task with executions day more than 3 characters - Spanish")
    void givenTaskWithExecutionDayMoreThan3Characters_whenFind_thenReturnValidationErrorInSpanish() {
        TaskCriteriaDTO taskCriteriaDto = new TaskCriteriaDTO();
        taskCriteriaDto.setExecutionDay("SUNDAY");
        this.getValidationErrorResponse(taskCriteriaDto, EnumLanguageCode.ES)
                .value(errorDetailsDTO -> {
                    Assertions.assertThat(errorDetailsDTO.getErrorCode())
                            .isEqualTo(EnumValidationError.FIELD_VALIDATION_ERROR.getCode());
                    Assertions.assertThat(errorDetailsDTO.getErrorMessage())
                            .isEqualTo("El día de ejecución de la tarea debe tener 3 caracteres.");
                });
    }

    @NotNull
    private WebTestClient.BodySpec<ErrorDetailsDTO, ?> getValidationErrorResponse(TaskCriteriaDTO taskCriteriaDto,
                                                                                  EnumLanguageCode languageEnum) {
        return this.webTestClient
                .post()
                .uri(TASKS_V1_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, languageEnum.getCode())
                .header(AUTHORIZATION, super.getBearerAccessToken())
                .bodyValue(taskCriteriaDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorDetailsDTO.class);
    }
}

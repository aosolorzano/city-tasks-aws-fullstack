package com.hiperium.city.tasks.api.controllers;

import com.hiperium.city.tasks.api.common.AbstractContainerBaseTest;
import com.hiperium.city.tasks.api.dto.ErrorDetailsDTO;
import com.hiperium.city.tasks.api.dto.TaskDTO;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumResourceError;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.hiperium.city.tasks.api.utils.PathsUtil.TASK_V1_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest extends AbstractContainerBaseTest {

    @Autowired
    private WebTestClient webTestClient;

    private static TaskDTO taskDTO;

    @BeforeAll
    public static void init() {
        taskDTO = TasksUtil.getTaskDtoTemplate();
    }

    @Test
    @Order(1)
    @DisplayName("Create Task")
    void givenTaskObject_whenSaveTask_thenReturnSavedTask() {
        this.webTestClient
                .post()
                .uri(TASK_V1_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, super.getBearerAccessToken())
                .bodyValue(taskDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TaskDTO.class)
                .value(createdTask -> {
                    assertThat(createdTask.getId()).isNotNull().isPositive();
                    assertThat(createdTask.getName()).isEqualTo(taskDTO.getName());
                    assertThat(createdTask.getDescription()).isEqualTo(taskDTO.getDescription());
                    assertThat(createdTask.getHour()).isEqualTo(taskDTO.getHour());
                    assertThat(createdTask.getMinute()).isEqualTo(taskDTO.getMinute());
                    assertThat(createdTask.getExecutionDays()).isEqualTo(taskDTO.getExecutionDays());
                    assertThat(createdTask.getDeviceId()).isEqualTo(taskDTO.getDeviceId());
                    assertThat(createdTask.getDeviceOperation()).isEqualTo(taskDTO.getDeviceOperation());
                    taskDTO.setId(createdTask.getId());
                });
    }

    @Test
    @Order(2)
    @DisplayName("Find created Task")
    void givenNotExistingTasksId_whenFindTaskById_thenReturnError404() {
        this.webTestClient
                .get()
                .uri(TASK_V1_PATH.concat("/{id}"), taskDTO.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, super.getBearerAccessToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskDTO.class)
                .value(taskResult -> {
                    assertThat(taskResult.getId()).isEqualTo(taskDTO.getId());
                    assertThat(taskResult.getName()).isEqualTo(taskDTO.getName());
                    assertThat(taskResult.getDescription()).isEqualTo(taskDTO.getDescription());
                    assertThat(taskResult.getHour()).isEqualTo(taskDTO.getHour());
                    assertThat(taskResult.getMinute()).isEqualTo(taskDTO.getMinute());
                    assertThat(taskResult.getExecutionDays()).isEqualTo(taskDTO.getExecutionDays());
                    assertThat(taskResult.getDeviceId()).isEqualTo(taskDTO.getDeviceId());
                    assertThat(taskResult.getDeviceOperation()).isEqualTo(taskDTO.getDeviceOperation());
                });
    }

    @Test
    @Order(3)
    @DisplayName("Update Task")
    void givenModifiedTask_whenUpdateTask_thenReturnUpdatedTask() {
        taskDTO.setName("Test class updated");
        taskDTO.setDescription("Task description updated.");
        taskDTO.setHour(13);
        taskDTO.setMinute(30);
        taskDTO.setExecutionDays("MON,TUE,WED,THU,FRI,SAT,SUN");
        taskDTO.setDeviceOperation(EnumDeviceOperation.DEACTIVATE);
        this.webTestClient
                .put()
                .uri(TASK_V1_PATH.concat("/{id}"), taskDTO.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, super.getBearerAccessToken())
                .bodyValue(taskDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskDTO.class)
                .value(updatedTask -> {
                    assertThat(updatedTask.getId()).isEqualTo(taskDTO.getId());
                    assertThat(updatedTask.getName()).isEqualTo(taskDTO.getName());
                    assertThat(updatedTask.getDescription()).isEqualTo(taskDTO.getDescription());
                    assertThat(updatedTask.getHour()).isEqualTo(taskDTO.getHour());
                    assertThat(updatedTask.getMinute()).isEqualTo(taskDTO.getMinute());
                    assertThat(updatedTask.getExecutionDays()).isEqualTo(taskDTO.getExecutionDays());
                    assertThat(updatedTask.getDeviceOperation()).isEqualTo(taskDTO.getDeviceOperation());
                });
    }

    @Test
    @Order(4)
    @DisplayName("Delete Task")
    void givenTaskObject_whenDeleteTask_thenReturnResponse200() {
        this.webTestClient
                .delete()
                .uri(TASK_V1_PATH.concat("/{id}"), taskDTO.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, super.getBearerAccessToken())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(5)
    @DisplayName("Delete Task that was deleted")
    void givenTaskId_whenDeleteTaskById_thenReturnError404() {
        this.webTestClient
                .delete()
                .uri(TASK_V1_PATH.concat("/{id}"), taskDTO.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, super.getBearerAccessToken())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorDetailsDTO.class)
                .value(errorDetailsDTO -> Assertions.assertThat(errorDetailsDTO.getErrorCode())
                        .isEqualTo(EnumResourceError.TASK_NOT_FOUND.getCode()));
    }
}

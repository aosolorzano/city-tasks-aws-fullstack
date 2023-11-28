package com.hiperium.city.tasks.api.controllers;

import com.hiperium.city.tasks.api.common.AbstractContainerBaseTest;
import com.hiperium.city.tasks.api.dto.TaskCriteriaDTO;
import com.hiperium.city.tasks.api.dto.TaskDTO;
import com.hiperium.city.tasks.api.models.Task;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.hiperium.city.tasks.api.utils.PathsUtil.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-queries.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TasksControllerTest extends AbstractContainerBaseTest {

    private static final long TASK_ID = 101L;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @Order(1)
    @DisplayName("Find all Tasks")
    void givenTasksList_whenFindAllTasks_thenReturnTasksList() {
        this.webTestClient
                .get()
                .uri(TASKS_V1_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, super.getBearerAccessToken())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(TaskDTO.class)
                .value(taskList -> Assertions.assertThat(taskList).isNotEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("Find Task by ID")
    void givenTaskId_whenFindTaskById_thenReturnTaskFound() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .id(TASK_ID)
                .build();
        this.webTestClient
                .post()
                .uri(TASKS_V1_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, super.getBearerAccessToken())
                .bodyValue(criteriaDto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Task.class)
                .value(taskList -> Assertions.assertThat(taskList).isNotEmpty());
    }

    @Test
    @DisplayName("Find Task that does not exist")
    void givenNotExistingTasksId_whenFindTaskById_thenReturnError404() {
        TaskCriteriaDTO taskCriteriaDto = TaskCriteriaDTO.builder()
                .id(10001L)
                .build();
        this.webTestClient
                .post()
                .uri(TASKS_V1_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, super.getBearerAccessToken())
                .bodyValue(taskCriteriaDto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(TaskDTO.class)
                .value(taskList -> Assertions.assertThat(taskList).isEmpty());
    }
}

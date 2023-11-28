package com.hiperium.city.tasks.api.services;

import com.hiperium.city.tasks.api.common.AbstractContainerBaseTest;
import com.hiperium.city.tasks.api.dto.TaskCriteriaDTO;
import com.hiperium.city.tasks.api.dto.TaskDTO;
import com.hiperium.city.tasks.api.utils.enums.EnumTaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestPropertySource(locations = "classpath:application-queries.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TasksServiceTest extends AbstractContainerBaseTest {

    private static final String DEVICE_1 = "123";
    private static final String DEVICE_2 = "456";
    private static final long TASK_ID = 101L;

    @Autowired
    private TasksService tasksService;

    @Test
    @DisplayName("Find all Tasks")
    void givenTaskList_whenFindAll_thenReturnAllTasks() {
        Flux<TaskDTO> taskFluxResult = this.tasksService.findAll();
        StepVerifier.create(taskFluxResult)
                .expectNextCount(4L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find Task by ID")
    void givenTaskObject_whenFindById_thenReturnTask() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .id(TASK_ID)
                .build();
        Flux<TaskDTO> taskFluxResult = this.tasksService.find(criteriaDto);
        StepVerifier.create(taskFluxResult)
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find Task with not existing ID")
    void givenTaskObject_whenFindById_thenReturnException() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .id(1000L)
                .build();
        Flux<TaskDTO> taskFluxResult = this.tasksService.find(criteriaDto);
        StepVerifier.create(taskFluxResult)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find Task by Name")
    void givenTaskObject_whenFindByName_thenReturnTask() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .name(String.valueOf(TASK_ID))
                .build();
        Flux<TaskDTO> taskFluxResult = this.tasksService.find(criteriaDto);
        StepVerifier.create(taskFluxResult)
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find active Tasks")
    void givenTaskObject_whenFindActive_thenReturnTaskList() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .status(EnumTaskStatus.ACT)
                .build();
        Flux<TaskDTO> taskFluxResult = this.tasksService.find(criteriaDto);
        StepVerifier.create(taskFluxResult)
                .expectNextCount(3L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find inactive Tasks")
    void givenTaskObject_whenFindInactive_thenReturnTaskList() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .status(EnumTaskStatus.INA)
                .build();
        Flux<TaskDTO> taskFluxResult = this.tasksService.find(criteriaDto);
        StepVerifier.create(taskFluxResult)
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find Tasks By Device ID 456")
    void givenTaskObject_whenFindByDeviceId_thenReturnTaskList() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .deviceId(DEVICE_2)
                .build();
        Flux<TaskDTO> taskFluxResult = this.tasksService.find(criteriaDto);
        StepVerifier.create(taskFluxResult)
                .expectNextCount(2L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find active Tasks By Device ID 456")
    void givenTaskObject_whenFindActiveByDeviceId_thenReturnTaskList() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .status(EnumTaskStatus.ACT)
                .deviceId(DEVICE_2)
                .build();
        Flux<TaskDTO> taskFluxResult = this.tasksService.find(criteriaDto);
        StepVerifier.create(taskFluxResult)
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find inactive Tasks By Device ID 123")
    void givenTaskObject_whenFindInactiveByDeviceId_thenReturnTaskList() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .status(EnumTaskStatus.ACT)
                .deviceId(DEVICE_1)
                .build();
        Flux<TaskDTO> taskFluxResult = this.tasksService.find(criteriaDto);
        StepVerifier.create(taskFluxResult)
                .expectNextCount(2L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find Tasks scheduled on Fridays")
    void givenTaskObject_whenFindByDay_thenReturnTaskList() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .executionDay("FRI")
                .build();
        Flux<TaskDTO> taskFluxResult = this.tasksService.find(criteriaDto);
        StepVerifier.create(taskFluxResult)
                .expectNextCount(3L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find active Tasks scheduled on Fridays")
    void givenTaskObject_whenFindActiveByDay_thenReturnTaskList() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .status(EnumTaskStatus.ACT)
                .executionDay("FRI")
                .build();
        Flux<TaskDTO> taskFluxResult = this.tasksService.find(criteriaDto);
        StepVerifier.create(taskFluxResult)
                .expectNextCount(2L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find active Tasks scheduled on Fridays for Device ID 123")
    void givenTaskObject_whenFindActiveByDayAndDeviceId_thenReturnTaskList() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .status(EnumTaskStatus.ACT)
                .deviceId(DEVICE_1)
                .executionDay("FRI")
                .build();
        Flux<TaskDTO> taskFluxResult = this.tasksService.find(criteriaDto);
        StepVerifier.create(taskFluxResult)
                .expectNextCount(2L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find Tasks scheduled at 12 AM")
    void givenTaskObject_whenFindByHour_thenReturnTaskList() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .hour(12)
                .build();
        Flux<TaskDTO> taskFluxResult = this.tasksService.find(criteriaDto);
        StepVerifier.create(taskFluxResult)
                .expectNextCount(2L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find active Tasks scheduled at 12 AM")
    void givenTaskObject_whenFindActiveByHour_thenReturnTaskList() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .status(EnumTaskStatus.ACT)
                .hour(12)
                .build();
        Flux<TaskDTO> taskFluxResult = this.tasksService.find(criteriaDto);
        StepVerifier.create(taskFluxResult)
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Find Tasks scheduled with minute 30")
    void givenTaskObject_whenFindByMinute_thenReturnTaskList() {
        TaskCriteriaDTO criteriaDto = TaskCriteriaDTO.builder()
                .minute(30)
                .build();
        Flux<TaskDTO> taskFluxResult = this.tasksService.find(criteriaDto);
        StepVerifier.create(taskFluxResult)
                .expectNextCount(1L)
                .verifyComplete();
    }
}

package com.hiperium.city.tasks.api.services;

import com.hiperium.city.tasks.api.common.AbstractContainerBaseTest;
import com.hiperium.city.tasks.api.exceptions.ResourceNotFoundException;
import com.hiperium.city.tasks.api.models.Task;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumTaskStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskServiceTest extends AbstractContainerBaseTest {

    private static final String DEVICE_ID = "123";

    private static Task task;

    @Autowired
    private TaskService taskService;

    @BeforeAll
    public static void init() {
        task = TasksUtil.getTaskTemplate();
        task.setDeviceId(DEVICE_ID);
    }

    @Test
    @Order(1)
    @DisplayName("Create Task")
    void givenTaskObject_whenSave_thenReturnSavedTask() {
        Mono<Task> taskMonoResult = this.taskService.create(task);
        StepVerifier.create(taskMonoResult)
                .assertNext(savedTask -> {
                    Assertions.assertThat(savedTask.getId()).isPositive();
                    Assertions.assertThat(savedTask.getName()).isEqualTo("Test class");
                    Assertions.assertThat(savedTask.getDescription()).isEqualTo("Task description.");
                    Assertions.assertThat(savedTask.getStatus()).isEqualTo(EnumTaskStatus.ACT);
                    Assertions.assertThat(savedTask.getHour()).isEqualTo(12);
                    Assertions.assertThat(savedTask.getMinute()).isZero();
                    Assertions.assertThat(savedTask.getExecutionDays()).isEqualTo("MON,WED,SUN");
                    Assertions.assertThat(savedTask.getDeviceId()).isEqualTo(DEVICE_ID);
                    Assertions.assertThat(savedTask.getDeviceOperation()).isEqualTo(EnumDeviceOperation.ACTIVATE);
                    BeanUtils.copyProperties(savedTask, task);
                })
                .verifyComplete();
    }

    @Test
    @Order(2)
    @DisplayName("Update Task")
    void givenTaskObject_whenUpdate_thenReturnUpdatedTask() {
        task.setName("Test class updated");
        task.setDescription("Task description updated.");
        task.setHour(13);
        task.setMinute(30);
        task.setExecutionDays("TUE,THU,SAT");
        task.setDeviceOperation(EnumDeviceOperation.DEACTIVATE);
        Mono<Task> taskMonoResult = this.taskService.update(task.getId(), task);
        StepVerifier.create(taskMonoResult)
                .assertNext(updatedTask -> {
                    Assertions.assertThat(updatedTask.getId()).isEqualTo(task.getId());
                    Assertions.assertThat(updatedTask.getName()).isEqualTo("Test class updated");
                    Assertions.assertThat(updatedTask.getDescription()).isEqualTo("Task description updated.");
                    Assertions.assertThat(updatedTask.getHour()).isEqualTo(13);
                    Assertions.assertThat(updatedTask.getMinute()).isEqualTo(30);
                    Assertions.assertThat(updatedTask.getExecutionDays()).isEqualTo("TUE,THU,SAT");
                    Assertions.assertThat(updatedTask.getDeviceOperation()).isEqualTo(EnumDeviceOperation.DEACTIVATE);
                })
                .verifyComplete();
    }

    @Test
    @Order(3)
    @DisplayName("Update Task that does not exist")
    void givenTaskObject_whenUpdate_thenReturnTaskException() {
        Mono<Task> taskMonoResult = this.taskService.update(10000L, task);
        StepVerifier.create(taskMonoResult)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }


    @Test
    @Order(4)
    @DisplayName("Delete Task")
    void givenTaskId_whenDelete_thenDeleteTaskObject() {
        Mono<Void> taskMonoResult = this.taskService.delete(task.getId());
        StepVerifier.create(taskMonoResult)
                .verifyComplete();
    }

    @Test
    @Order(5)
    @DisplayName("Delete Task that does not exist")
    void givenNotExistingTask_whenDelete_thenReturnException() {
        Mono<Void> taskMonoResult = this.taskService.delete(task.getId());
        StepVerifier.create(taskMonoResult)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}

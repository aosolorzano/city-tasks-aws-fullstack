package com.hiperium.city.tasks.api.repositories;

import com.hiperium.city.tasks.api.common.AbstractContainerBaseTest;
import com.hiperium.city.tasks.api.models.Task;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskRepositoryTest extends AbstractContainerBaseTest {

    private static final String DEVICE_ID = "123";

    @Autowired
    private TaskRepository taskRepository;

    private static Task task;

    @BeforeAll
    public static void init() {
        task = TasksUtil.getTaskTemplate();
        task.setDeviceId(DEVICE_ID);
        task.setJobId(TasksUtil.generateJobId());
        task.setCreatedAt(ZonedDateTime.now());
        task.setUpdatedAt(ZonedDateTime.now());
    }

    @Test
    @Order(1)
    @DisplayName("Create Task")
    void givenTaskObject_whenCreateTask_thenReturnCreatedTaskObject() {
        Task savedTask = this.taskRepository.save(task);
        assertThat(savedTask).isNotNull();
        task.setId(savedTask.getId());
    }

    @Test
    @Order(2)
    @DisplayName("Find Task by ID")
    void givenTaskId_whenFindByTaskId_thenReturnTaskObject() {
        Task savedTask = this.taskRepository.findById(task.getId()).orElse(null);
        if (Objects.isNull(savedTask)) {
            Assertions.fail("Task not found with ID: " + task.getId());
            return;
        }
        assertThat(savedTask.getId()).isEqualTo(task.getId());
    }

    @Test
    @Order(3)
    @DisplayName("Find Task by Job ID")
    void givenJobId_whenFindByJobId_thenReturnTaskObject() {
        Task savedTask = this.taskRepository.findByJobId(task.getJobId());
        assertThat(savedTask).isNotNull();
    }

    @Test
    @Order(4)
    @DisplayName("Update Task name")
    void givenTaskObject_whenUpdate_thenReturnUpdatedTask() {
        task.setName("Updated task");
        Task updatedTask = this.taskRepository.save(task);
        assertThat(updatedTask.getName()).isEqualTo(task.getName());
    }

    @Test
    @Order(5)
    @DisplayName("Find all Tasks")
    void givenTaskList_whenFindAll_thenReturnTaskList() {
        Iterable<Task> taskIterable = this.taskRepository.findAll();
        assertThat(taskIterable).isNotEmpty();
    }

    @Test
    @Order(6)
    @DisplayName("Delete Task")
    void givenTaskId_whenDelete_thenDeleteTaskObject() {
        this.taskRepository.deleteById(task.getId());
        Optional<Task> taskOptional = this.taskRepository.findById(task.getId());
        assertThat(taskOptional).isEmpty();
    }
}

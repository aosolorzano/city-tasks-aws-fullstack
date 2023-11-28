package com.hiperium.city.tasks.api.controllers;

import com.hiperium.city.tasks.api.dto.TaskDTO;
import com.hiperium.city.tasks.api.exceptions.ValidationException;
import com.hiperium.city.tasks.api.mappers.TaskMapper;
import com.hiperium.city.tasks.api.models.Task;
import com.hiperium.city.tasks.api.services.TaskService;
import com.hiperium.city.tasks.api.utils.BeansValidationUtil;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

import static com.hiperium.city.tasks.api.utils.PathsUtil.TASK_V1_PATH;

@Slf4j
@RestController
@RequestMapping(TASK_V1_PATH)
@Tag(name = "Task", description = "Task management API.")
public class TaskController {

    private final TaskService taskService;

    @Setter(onMethod = @__({@Autowired}))
    private TaskMapper taskMapper;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a Task by ID.",
            description = "Find a Task by ID and return the Task.")
    public Mono<ResponseEntity<TaskDTO>> findById(@PathVariable("id") Long id) {
        log.debug("findById() - START: {}", id);
        return this.taskService.findById(id)
                .map(taskResponse -> this.taskMapper.toTaskDTO(taskResponse))
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Create a new Task.",
            description = "Create a new Task and return the created Task.")
    public Mono<ResponseEntity<TaskDTO>> create(ServerHttpRequest request,
                                                @RequestBody @Valid TaskDTO taskDTO) {
        log.debug("create() - START: {}", taskDTO);
        if (Objects.nonNull(taskDTO.getId())) {
            throw new ValidationException("Task ID must be null.");
        }
        BeansValidationUtil.validateBean(taskDTO);
        Task task = this.taskMapper.toTask(taskDTO);
        return this.taskService.create(task)
                .map(taskResponse -> this.taskMapper.toTaskDTO(taskResponse))
                .map(taskResponseDTO -> {
                    URI location = request.getURI().resolve("/" + taskResponseDTO.getId());
                    return ResponseEntity.created(location).body(taskResponseDTO);
                });
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Task.",
            description = "Update a Task and return the updated Task.")
    public Mono<ResponseEntity<TaskDTO>> update(@PathVariable("id") Long id,
                                                @RequestBody @Valid TaskDTO taskDTO) {
        log.debug("update() - START: {}", taskDTO);
        if (!Objects.equals(taskDTO.getId(), id)) {
            throw new ValidationException("Task ID must be the same as the path variable.");
        }
        BeansValidationUtil.validateBean(taskDTO);
        Task task = this.taskMapper.toTask(taskDTO);
        return this.taskService.update(id, task)
                .map(taskResponse -> this.taskMapper.toTaskDTO(taskResponse))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a Task.",
            description = "Delete a Task and return the deleted Task.")
    public Mono<Void> delete(@PathVariable("id") Long id) {
        log.debug("delete() - START: {}", id);
        return this.taskService.delete(id);
    }

    @Hidden
    @GetMapping("/template")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO getTaskOperationTemplate() {
        return TasksUtil.getTaskDtoTemplate();
    }
}

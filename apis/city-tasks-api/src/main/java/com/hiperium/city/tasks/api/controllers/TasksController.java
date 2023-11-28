package com.hiperium.city.tasks.api.controllers;

import com.hiperium.city.tasks.api.dto.TaskCriteriaDTO;
import com.hiperium.city.tasks.api.dto.TaskDTO;
import com.hiperium.city.tasks.api.services.TasksService;
import com.hiperium.city.tasks.api.utils.BeansValidationUtil;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static com.hiperium.city.tasks.api.utils.PathsUtil.*;

@Slf4j
@RestController
@RequestMapping(TASKS_V1_PATH)
@Tag(name = "Tasks", description = "Tasks APIs for searching and filtering.")
public class TasksController {

    private final TasksService tasksService;

    public TasksController(TasksService tasksService) {
        this.tasksService = tasksService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Gets all created Tasks.",
               description = "Each Task has associated a Quartz Job ID.")
    public Flux<TaskDTO> findAll() {
        log.debug("findAll() - START");
        return this.tasksService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Find Tasks using filters.",
            description = "Find created Tasks using some criteria for filtering.")
    public Flux<TaskDTO> find(@RequestBody TaskCriteriaDTO criteriaDto) {
        log.debug("find() - START: {}", criteriaDto);
        BeansValidationUtil.validateBean(criteriaDto);
        return this.tasksService.find(criteriaDto);
    }

    @Hidden
    @GetMapping("/template")
    @ResponseStatus(HttpStatus.OK)
    public TaskCriteriaDTO getTemplateBody() {
        return TasksUtil.getTaskCriteriaTemplate();
    }
}

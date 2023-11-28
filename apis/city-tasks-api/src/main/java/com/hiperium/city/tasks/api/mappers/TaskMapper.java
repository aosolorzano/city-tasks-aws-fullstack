package com.hiperium.city.tasks.api.mappers;

import com.hiperium.city.tasks.api.dto.TaskDTO;
import com.hiperium.city.tasks.api.models.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "jobId", ignore = true)
    @Mapping(target = "deviceExecutionCommand", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toTask(TaskDTO taskDTO);

    TaskDTO toTaskDTO(Task task);
}

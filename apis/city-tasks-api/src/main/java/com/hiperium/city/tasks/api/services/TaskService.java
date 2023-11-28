package com.hiperium.city.tasks.api.services;

import com.hiperium.city.tasks.api.exceptions.ResourceNotFoundException;
import com.hiperium.city.tasks.api.models.Task;
import com.hiperium.city.tasks.api.repositories.TaskRepository;
import com.hiperium.city.tasks.api.schedulers.CityTaskScheduler;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import com.hiperium.city.tasks.api.utils.enums.EnumResourceError;
import com.hiperium.city.tasks.api.utils.enums.EnumTaskStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.ZonedDateTime;
import java.util.Objects;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final CityTaskScheduler cityTaskScheduler;

    public TaskService(TaskRepository taskRepository, CityTaskScheduler cityTaskScheduler) {
        this.taskRepository = taskRepository;
        this.cityTaskScheduler = cityTaskScheduler;
    }

    public Mono<Task> findById(final Long taskId) {
        return Mono.fromSupplier(() -> this.taskRepository.findById(taskId))
                .filter(Objects::nonNull)
                .map(taskOptional -> taskOptional
                        .orElseThrow(() -> new ResourceNotFoundException(EnumResourceError.TASK_NOT_FOUND, taskId)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Task> create(final Task task) {
        return Mono.just(task)
                .doOnNext(TaskService::setCreateDefaultValues)
                .doOnNext(this.taskRepository::save)
                .doOnNext(this.cityTaskScheduler::scheduleJob);
    }

    public Mono<Task> update(final Long modifiedTaskId, final Task modifiedTask) {
        return this.findById(modifiedTaskId)
                .doOnNext(actualTask -> this.updateDefaultValues(modifiedTask, actualTask))
                .doOnNext(actualTask -> BeanUtils.copyProperties(modifiedTask, actualTask))
                .doOnNext(this.taskRepository::save)
                .doOnNext(this::rescheduleTaskJob);

    }

    public Mono<Void> delete(final Long taskId) {
        return this.findById(taskId)
                .doOnNext(this.cityTaskScheduler::unscheduleJob)
                .doOnNext(this.taskRepository::delete)
                .then();
    }

    private static void setCreateDefaultValues(Task scheduledTask) {
        scheduledTask.setJobId(TasksUtil.generateJobId());
        scheduledTask.setStatus(EnumTaskStatus.ACT);
        scheduledTask.setCreatedAt(ZonedDateTime.now());
        scheduledTask.setUpdatedAt(ZonedDateTime.now());
        if (Objects.isNull(scheduledTask.getExecuteUntil())) {
            scheduledTask.setExecuteUntil(ZonedDateTime.now()
                    .plusYears(1).withMonth(12).withDayOfMonth(31)
                    .withHour(23).withMinute(59).withSecond(59));
        }
    }

    private void updateDefaultValues(Task modifiedTask, Task actualTask) {
        modifiedTask.setJobId(actualTask.getJobId());
        modifiedTask.setStatus(actualTask.getStatus());
        modifiedTask.setCreatedAt(actualTask.getCreatedAt());
        modifiedTask.setUpdatedAt(ZonedDateTime.now());
    }

    private void rescheduleTaskJob(Task actualTask) {
        if (actualTask.getStatus().equals(EnumTaskStatus.ACT)) {
            this.cityTaskScheduler.rescheduleJob(actualTask);
        }
    }
}

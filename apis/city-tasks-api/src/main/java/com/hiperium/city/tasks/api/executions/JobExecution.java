package com.hiperium.city.tasks.api.executions;

import com.hiperium.city.tasks.api.repositories.DeviceRepository;
import com.hiperium.city.tasks.api.repositories.TaskRepository;
import com.hiperium.city.tasks.api.services.EventBridgeService;
import com.hiperium.city.tasks.api.utils.SchedulersUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JobExecution implements Job {

    private final TaskRepository taskRepository;
    private final DeviceRepository deviceRepository;
    private final EventBridgeService eventBridgeService;


    public JobExecution(TaskRepository taskRepository, DeviceRepository deviceRepository,
                        EventBridgeService eventBridgeService) {
        this.taskRepository = taskRepository;
        this.deviceRepository = deviceRepository;
        this.eventBridgeService = eventBridgeService;
    }

    @Override
    public void execute(final JobExecutionContext context) {
        log.debug("execute() - START");
        final String jobId = context.getJobDetail().getJobDataMap().getString(SchedulersUtil.TASK_JOB_ID_DATA_KEY);
        Mono.just(jobId)
                .map(this.taskRepository::findByJobId)
                .doOnNext(this.deviceRepository::updateStatusByTaskOperation)
                .doOnNext(this.eventBridgeService::triggerEvent)
                .subscribe(
                        result -> log.info("Job executed: {}", jobId),
                        error ->  log.error("Error executing job '{}'. Error: {}", jobId, error.getMessage())
                );
    }
}

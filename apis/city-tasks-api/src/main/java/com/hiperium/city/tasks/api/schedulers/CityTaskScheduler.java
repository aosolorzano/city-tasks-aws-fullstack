package com.hiperium.city.tasks.api.schedulers;

import com.hiperium.city.tasks.api.exceptions.ResourceNotFoundException;
import com.hiperium.city.tasks.api.exceptions.TaskSchedulerException;
import com.hiperium.city.tasks.api.models.Task;
import com.hiperium.city.tasks.api.utils.PropertiesUtil;
import com.hiperium.city.tasks.api.utils.SchedulersUtil;
import com.hiperium.city.tasks.api.utils.enums.EnumResourceError;
import com.hiperium.city.tasks.api.utils.enums.EnumSchedulerError;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class CityTaskScheduler {

    private final Scheduler scheduler;

    @Value("${" + PropertiesUtil.TIME_ZONE_ID_PROPERTY + "}")
    private String timeZone;

    public CityTaskScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleJob(final Task task) {
        log.debug("scheduleJob() - START: {}", task);
        JobDetail job = SchedulersUtil.createJobDetailFromTask(task);
        Trigger trigger = SchedulersUtil.createCronTriggerFromTask(task, this.timeZone);
        try {
            this.scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e, EnumSchedulerError.SCHEDULE_JOB_ERROR, task.getName());
        }
    }

    public void rescheduleJob(final Task task) {
        log.debug("rescheduleJob() - START: {}", task);
        Trigger currentTrigger = this.getCurrentTrigger(task);
        Trigger newTrigger = SchedulersUtil.createCronTriggerFromTask(task, this.timeZone);
        try {
            this.scheduler.rescheduleJob(currentTrigger.getKey(), newTrigger);
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e, EnumSchedulerError.RESCHEDULE_JOB_ERROR, task.getId());
        }
    }

    public void unscheduleJob(final Task task) {
        log.debug("unscheduleJob() - START: {}", task);
        Trigger currentTrigger = this.getCurrentTrigger(task);
        try {
            this.scheduler.unscheduleJob(currentTrigger.getKey());
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e, EnumSchedulerError.UNSCHEDULE_JOB_ERROR, task.getId());
        }
    }

    private Trigger getCurrentTrigger(final Task task) {
        Trigger trigger = null;
        try {
            for (JobKey jobKey : this.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(SchedulersUtil.TASK_GROUP_NAME))) {
                if (jobKey.getName().equals(task.getJobId())) {
                    TriggerKey triggerKey = TriggerKey.triggerKey(task.getJobId(), SchedulersUtil.TASK_GROUP_NAME);
                    trigger = this.scheduler.getTrigger(triggerKey);
                }
            }
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e, EnumSchedulerError.GET_CURRENT_TRIGGER_ERROR, task.getId());
        }
        if (Objects.isNull(trigger)) {
            throw new ResourceNotFoundException(EnumResourceError.TRIGGER_NOT_FOUND, task.getId());
        }
        return trigger;
    }
}

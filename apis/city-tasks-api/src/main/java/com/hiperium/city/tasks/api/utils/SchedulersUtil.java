package com.hiperium.city.tasks.api.utils;

import com.hiperium.city.tasks.api.executions.JobExecution;
import com.hiperium.city.tasks.api.models.Task;
import com.hiperium.city.tasks.api.utils.enums.EnumDays;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.quartz.*;

import java.time.ZoneId;
import java.util.*;
import java.util.Calendar;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SchedulersUtil {

    public static final String TASK_GROUP_NAME = "Task#Group";
    public static final String TASK_JOB_ID_DATA_KEY = "taskJobId";

    public static JobDetail createJobDetailFromTask(Task task) {
        return JobBuilder.newJob(JobExecution.class)
                .withIdentity(task.getJobId(), TASK_GROUP_NAME)
                .usingJobData(TASK_JOB_ID_DATA_KEY, task.getJobId())
                .build();
    }

    public static CronTrigger createCronTriggerFromTask(Task task, String timeZone) {
        TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(task.getJobId(), TASK_GROUP_NAME)
                .startNow()
                .withSchedule(CronScheduleBuilder
                        .atHourAndMinuteOnGivenDaysOfWeek(
                                task.getHour(),
                                task.getMinute(),
                                getIntValuesFromExecutionDays(task.getExecutionDays()))
                        .inTimeZone(TimeZone.getTimeZone(ZoneId.of(timeZone))));
        if (Objects.nonNull(task.getExecuteUntil())) {
            Calendar executeUntilCalendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of(timeZone)));
            executeUntilCalendar.set(Calendar.YEAR, task.getExecuteUntil().getYear());
            executeUntilCalendar.set(Calendar.MONTH, task.getExecuteUntil().getMonthValue() - 1);
            executeUntilCalendar.set(Calendar.DAY_OF_MONTH, task.getExecuteUntil().getDayOfMonth());
            executeUntilCalendar.set(Calendar.HOUR_OF_DAY, 23);
            executeUntilCalendar.set(Calendar.MINUTE, 59);
            executeUntilCalendar.set(Calendar.SECOND, 59);
            // TODO: Fix the error: "java.lang.IllegalArgumentException: End time cannot be before start time"
            triggerBuilder.endAt(executeUntilCalendar.getTime());
        }
        return triggerBuilder.build();
    }

    public static Integer[] getIntValuesFromExecutionDays(String taskExecutionDays) {
        List<Integer> intsDaysOfWeek = new ArrayList<>();
        for (String dayOfWeek : taskExecutionDays.split(",")) {
            EnumDays enumDays = EnumDays.getEnumFromString(dayOfWeek);
            switch (enumDays) {
                case MON -> intsDaysOfWeek.add(DateBuilder.MONDAY);
                case TUE -> intsDaysOfWeek.add(DateBuilder.TUESDAY);
                case WED -> intsDaysOfWeek.add(DateBuilder.WEDNESDAY);
                case THU -> intsDaysOfWeek.add(DateBuilder.THURSDAY);
                case FRI -> intsDaysOfWeek.add(DateBuilder.FRIDAY);
                case SAT -> intsDaysOfWeek.add(DateBuilder.SATURDAY);
                case SUN -> intsDaysOfWeek.add(DateBuilder.SUNDAY);
                default ->
                        throw new IllegalArgumentException("The day of the week does not match with the accepted ones: " + enumDays);
            }
        }
        return intsDaysOfWeek.toArray(Integer[]::new);
    }
}

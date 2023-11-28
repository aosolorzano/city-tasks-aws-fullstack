package com.hiperium.city.tasks.api.utils;

import com.hiperium.city.tasks.api.dto.TaskCriteriaDTO;
import com.hiperium.city.tasks.api.dto.TaskDTO;
import com.hiperium.city.tasks.api.models.Task;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumTaskStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TasksUtil {

    private static final List<String> DAYS_LIST = Arrays.asList("SUN","MON","TUE","WED","THU","FRI","SAT");
    private static final char[] HEX_ARRAY = "HiperiumTasksService".toCharArray();
    private static final int JOB_ID_LENGTH = 20;

    public static String generateJobId() {
        MessageDigest salt;
        try {
            salt = MessageDigest.getInstance("SHA-256");
            salt.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
        String uuid = bytesToHex(salt.digest());
        return uuid.substring(0, JOB_ID_LENGTH);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static Task getTaskTemplate() {
        return Task.builder()
                .name("Test class")
                .description("Task description.")
                .status(EnumTaskStatus.ACT)
                .hour(12)
                .minute(0)
                .executionDays("MON,WED,SUN")
                .executeUntil(ZonedDateTime.now().plusMonths(1))
                .deviceOperation(EnumDeviceOperation.ACTIVATE)
                .build();
    }

    public static TaskCriteriaDTO getTaskCriteriaTemplate() {
        return TaskCriteriaDTO.builder()
                .name("Task")
                .status(EnumTaskStatus.ACT)
                .deviceId("1")
                .deviceOperation(EnumDeviceOperation.ACTIVATE)
                .hour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                .minute(Calendar.getInstance().get(Calendar.MINUTE))
                .executionDay(DAYS_LIST.get(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1))
                .build();
    }

    public static TaskDTO getTaskDtoTemplate() {
        return TaskDTO.builder()
                .name("Task name")
                .description("Task description")
                .status(EnumTaskStatus.ACT)
                .deviceId("123")
                .deviceOperation(EnumDeviceOperation.ACTIVATE)
                .hour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                .minute(Calendar.getInstance().get(Calendar.MINUTE))
                .executionDays(DAYS_LIST.get(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1))
                .executeUntil(ZonedDateTime.now().plusYears(1))
                .build();
    }
}

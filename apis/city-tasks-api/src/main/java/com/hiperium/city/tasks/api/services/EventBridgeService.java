package com.hiperium.city.tasks.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiperium.city.tasks.api.dto.TaskExecutionDTO;
import com.hiperium.city.tasks.api.models.Task;
import com.hiperium.city.tasks.api.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Service
public class EventBridgeService {

    public static final String EVENT_SOURCE_NAME = "com.hiperium.city.tasks.api";
    public static final String EVENT_SOURCE_TYPE = "TaskExecutionEvent";

    private final EventBridgeClient eventBridgeClient;

    @Value("${" + PropertiesUtil.TIME_ZONE_ID_PROPERTY + "}")
    private String zoneId;

    public EventBridgeService(EventBridgeClient eventBridgeClient) {
        this.eventBridgeClient = eventBridgeClient;
    }

    public void triggerEvent(final Task task) {
        log.debug("triggerCustomEvent() - START");
        ObjectMapper objectMapper = new ObjectMapper();
        TaskExecutionDTO taskExecutionDTO = TaskExecutionDTO.builder()
                .taskId(task.getId())
                .deviceId(task.getDeviceId())
                .deviceOperation(task.getDeviceOperation())
                .build();
        PutEventsRequestEntry entry = this.createRequestEntry(objectMapper, taskExecutionDTO);
        PutEventsRequest eventRequest = PutEventsRequest.builder()
                .entries(entry)
                .build();
        log.debug("Event Request: {}", eventRequest);

        this.eventBridgeClient.putEvents(eventRequest);
    }

    private PutEventsRequestEntry createRequestEntry(ObjectMapper objectMapper, TaskExecutionDTO taskExecutionDto) {
        PutEventsRequestEntry entry;
        ZonedDateTime actualZonedDateTime = ZonedDateTime.now(ZoneId.of(this.zoneId));
        try {
            entry = PutEventsRequestEntry.builder()
                    .source(EVENT_SOURCE_NAME)
                    .detailType(EVENT_SOURCE_TYPE)
                    .detail(objectMapper.writeValueAsString(taskExecutionDto))
                    .time(actualZonedDateTime.toInstant())
                    .build();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return entry;
    }
}

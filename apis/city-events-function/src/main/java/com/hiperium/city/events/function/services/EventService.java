package com.hiperium.city.events.function.services;

import com.hiperium.city.events.function.mappers.EventMapper;
import com.hiperium.city.events.function.models.Event;
import com.hiperium.city.events.function.models.EventBridgeCustomEvent;
import com.hiperium.city.events.function.utils.BeansValidationUtil;
import com.hiperium.city.events.function.utils.PropertiesUtil;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;
import java.util.UUID;

@Service
public class EventService {

    private final DynamoDbClient dynamoDbClient;

    @Setter(onMethod_ = @Autowired)
    private EventMapper eventMapper;

    @Value("${" + PropertiesUtil.TIME_ZONE_ID_PROPERTY + "}")
    private String zoneId;

    public EventService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void createEvent(EventBridgeCustomEvent eventBridgeCustomEvent) {
        BeansValidationUtil.validateBean(eventBridgeCustomEvent.getDetail());

        Event event = this.eventMapper.fromCustomEvent(eventBridgeCustomEvent, this.zoneId);
        var putItemRequest = PutItemRequest.builder()
                .item(
                        Map.of(Event.ID_FIELD, AttributeValue.fromS(UUID.randomUUID().toString()),
                                Event.DEVICE_ID_FIELD, AttributeValue.fromS(eventBridgeCustomEvent.getDetail().getDeviceId()),
                                Event.TASK_ID_FIELD, AttributeValue.fromN(eventBridgeCustomEvent.getDetail().getTaskId().toString()),
                                Event.OPERATION_FIELD, AttributeValue.fromS(eventBridgeCustomEvent.getDetail().getDeviceOperation().name()),
                                Event.EXECUTION_DATE_FIELD, AttributeValue.fromS(event.getExecutionDate()),
                                Event.EXECUTION_INSTANT_FIELD, AttributeValue.fromN(event.getExecutionInstant().toString())
                        )
                )
                .tableName(Event.TABLE_NAME)
                .build();
        this.dynamoDbClient.putItem(putItemRequest);
    }
}

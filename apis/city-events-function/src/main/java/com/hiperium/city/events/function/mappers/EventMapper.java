package com.hiperium.city.events.function.mappers;

import com.hiperium.city.events.function.models.Event;
import com.hiperium.city.events.function.models.EventBridgeCustomEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "executionDate", expression = "java(getExecutionDate(customEvent, zoneId))")
    @Mapping(target = "executionInstant", expression = "java(getExecutionInstant(customEvent))")
    Event fromCustomEvent(EventBridgeCustomEvent customEvent, String zoneId);

    default String getExecutionDate(EventBridgeCustomEvent customEvent, String zoneId) {
        return customEvent.getTime().atZone(ZoneId.of(zoneId)).toString();
    }

    default Long getExecutionInstant(EventBridgeCustomEvent customEvent) {
        return customEvent.getTime().toEpochMilli();
    }
}

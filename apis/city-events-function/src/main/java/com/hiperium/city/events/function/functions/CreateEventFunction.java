package com.hiperium.city.events.function.functions;

import com.hiperium.city.events.function.models.EventBridgeCustomEvent;
import com.hiperium.city.events.function.models.EventsResponse;
import com.hiperium.city.events.function.services.EventService;
import com.hiperium.city.events.function.utils.BeansValidationUtil;
import com.hiperium.city.events.function.utils.FunctionsUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component
public class CreateEventFunction implements Function<EventBridgeCustomEvent, EventsResponse> {

    private final EventService eventService;

    public CreateEventFunction(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public EventsResponse apply(EventBridgeCustomEvent event) {
        log.debug("handleRequest(): {}", event);
        BeansValidationUtil.validateBean(event);
        this.eventService.createEvent(event);
        return FunctionsUtil.getSuccessResponse();
    }
}

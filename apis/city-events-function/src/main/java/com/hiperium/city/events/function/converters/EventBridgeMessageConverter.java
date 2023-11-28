package com.hiperium.city.events.function.converters;

import com.hiperium.city.events.function.models.EventBridgeCustomEvent;
import com.hiperium.city.events.function.utils.FunctionsUtil;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.util.MimeType;

public class EventBridgeMessageConverter extends AbstractMessageConverter {

    public EventBridgeMessageConverter() {
        super(new MimeType("application", "json"));
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return (EventBridgeCustomEvent.class.equals(clazz));
    }

    @Override
    public Object convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
        Object payload = message.getPayload();
        return (payload instanceof EventBridgeCustomEvent ? payload :
                FunctionsUtil.unmarshal((byte[]) payload, EventBridgeCustomEvent.class));
    }
}

package com.hiperium.city.tasks.api.utils;

import com.hiperium.city.tasks.api.models.Device;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DevicesUtil {

    public static GetItemRequest getDeviceItemRequest(final String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(Device.DEVICE_ID_COL, AttributeValue.builder().s(id).build());
        return GetItemRequest.builder()
                .tableName(Device.TABLE_NAME)
                .key(key)
                .attributesToGet(Device.DEVICE_ID_COL, Device.DEVICE_NAME_COL, Device.DEVICE_DESC_COL, Device.DEVICE_STATUS_COL)
                .build();
    }

    public static PutItemRequest updateDeviceStatusRequest(final Device device) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(Device.DEVICE_ID_COL, AttributeValue.builder().s(device.getId()).build());
        // Do not remove the "name" and "description" attributes, otherwise they will be updated to null.
        item.put(Device.DEVICE_NAME_COL, AttributeValue.builder().s(device.getName()).build());
        item.put(Device.DEVICE_DESC_COL, AttributeValue.builder().s(device.getDescription()).build());
        item.put(Device.DEVICE_STATUS_COL, AttributeValue.builder().s(String.valueOf(device.getStatus())).build());
        return PutItemRequest.builder()
                .tableName(Device.TABLE_NAME)
                .item(item)
                .build();
    }

    public static Device getFromItemResponse(final GetItemResponse itemResponse) {
        final Map<String, AttributeValue> item = itemResponse.item();
        return Device.builder()
                .id(item.get(Device.DEVICE_ID_COL).s())
                .name(item.get(Device.DEVICE_NAME_COL).s())
                .description(item.get(Device.DEVICE_DESC_COL).s())
                .status(EnumDeviceStatus.valueOf(item.get(Device.DEVICE_STATUS_COL).s()))
                .build();
    }
}

package com.hiperium.city.tasks.api.repositories;

import com.hiperium.city.tasks.api.exceptions.ResourceNotFoundException;
import com.hiperium.city.tasks.api.models.Device;
import com.hiperium.city.tasks.api.models.Task;
import com.hiperium.city.tasks.api.utils.DevicesUtil;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceStatus;
import com.hiperium.city.tasks.api.utils.enums.EnumResourceError;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@Repository
public class DeviceRepository {

    private final DynamoDbClient dynamoDbClient;

    public DeviceRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public Mono<Device> findById(final String id) {
        GetItemRequest getItemRequest = DevicesUtil.getDeviceItemRequest(id);
        return Mono.fromSupplier(() -> this.dynamoDbClient.getItem(getItemRequest))
                .doOnNext(itemResponse -> verifyResponse(id, itemResponse))
                .map(DevicesUtil::getFromItemResponse);
    }

    public Mono<Device> updateStatusByTaskOperation(final Task task) {
        return this.findById(task.getDeviceId())
                .doOnNext(device -> setStatusByTaskOperation(task, device))
                .doOnNext(this::updateDeviceItemStatus);
    }

    private void updateDeviceItemStatus(Device device) {
        PutItemRequest putItemRequest = DevicesUtil.updateDeviceStatusRequest(device);
        this.dynamoDbClient.putItem(putItemRequest);
    }

    private static void setStatusByTaskOperation(final Task task, Device device) {
        if (EnumDeviceOperation.ACTIVATE.equals(task.getDeviceOperation())) {
            device.setStatus(EnumDeviceStatus.ON);
        } else {
            device.setStatus(EnumDeviceStatus.OFF);
        }
    }

    private static void verifyResponse(String id, GetItemResponse itemResponse) {
        if(!itemResponse.hasItem()) throw new ResourceNotFoundException(EnumResourceError.DEVICE_NOT_FOUND, id);
    }
}

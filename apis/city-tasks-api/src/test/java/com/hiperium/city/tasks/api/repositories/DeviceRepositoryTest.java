package com.hiperium.city.tasks.api.repositories;

import com.hiperium.city.tasks.api.common.AbstractContainerBaseTest;
import com.hiperium.city.tasks.api.exceptions.ResourceNotFoundException;
import com.hiperium.city.tasks.api.models.Device;
import com.hiperium.city.tasks.api.models.Task;
import com.hiperium.city.tasks.api.utils.TasksUtil;
import com.hiperium.city.tasks.api.utils.TestsUtil;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@Slf4j
@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceRepositoryTest extends AbstractContainerBaseTest {

    public static final String DEVICE_ID = "123";

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DynamoDbClient dynamoDbClient;

    private static Task task;

    private static Device deviceTest;

    @BeforeAll
    public static void beforeAll() {
        deviceTest = new Device();
        task = TasksUtil.getTaskTemplate();
        task.setDeviceId(DEVICE_ID);
    }

    @Test
    @Order(1)
    @DisplayName("Wait for DynamoDB to be ready")
    void givenDynamoDbTable_whenCreated_mustNotThrownError() {
        TestsUtil.waitForTableToBecomeActive(this.dynamoDbClient);
        Assertions.assertTrue(true);
    }

    @Test
    @Order(2)
    @DisplayName("Find Device by ID")
    void givenDeviceId_whenFindById_mustReturnDeviceItem() {
        Mono<Device> deviceMonoResponse = this.deviceRepository.findById(DEVICE_ID);
        StepVerifier.create(deviceMonoResponse)
                .assertNext(deviceResponse -> {
                    assertThat(deviceResponse).isNotNull();
                    assertThat(deviceResponse.getId()).isEqualTo(DEVICE_ID);
                    assertThat(deviceResponse.getName()).isEqualTo("Device 123");
                    assertThat(deviceResponse.getDescription()).isEqualTo("Device 123 description");
                    BeanUtils.copyProperties(deviceResponse, deviceTest);
                })
                .verifyComplete();
    }

    @Test
    @Order(3)
    @DisplayName("Toggle Device Status - 1")
    void givenDeviceItem_whenStateChangeByTask_mustReturnTask() {
        setTaskOperationToOppositeDeviceStatus();
        EnumDeviceStatus expectedStatus = task.getDeviceOperation().equals(EnumDeviceOperation.ACTIVATE) ?
                EnumDeviceStatus.ON : EnumDeviceStatus.OFF;
        Mono<Device> updatedDeviceStatusResponse = this.deviceRepository.updateStatusByTaskOperation(task);
        StepVerifier.create(updatedDeviceStatusResponse)
                .assertNext(deviceResponse -> {
                    assertThat(deviceResponse).isNotNull();
                    assertThat(deviceResponse.getId()).isEqualTo(task.getDeviceId());
                    assertThat(deviceResponse.getStatus()).isEqualTo(expectedStatus);
                    BeanUtils.copyProperties(deviceResponse, deviceTest);
                })
                .verifyComplete();
    }

    @Test
    @Order(4)
    @DisplayName("Find Updated Device - 1")
    void givenDeviceStatusUpdated_whenFindById_mustReturnUpdateDeviceStatusOne() {
        Mono<Device> deviceUpdatedStatusResponse = this.deviceRepository.findById(DEVICE_ID);
        StepVerifier.create(deviceUpdatedStatusResponse)
                .assertNext(deviceResponse -> {
                    assertThat(deviceResponse).isNotNull();
                    assertThat(deviceResponse.getId()).isEqualTo(DEVICE_ID);
                    assertThat(deviceResponse.getName()).isEqualTo("Device 123");
                    assertThat(deviceResponse.getDescription()).isEqualTo("Device 123 description");
                    assertThat(deviceResponse.getStatus()).isEqualTo(deviceTest.getStatus());
                })
                .verifyComplete();
    }

    @Test
    @Order(5)
    @DisplayName("Toggle Device Status - 2")
    void givenDeviceItem_whenTaskTurnedOn_mustUpdateDeviceStatus() {
        setTaskOperationToOppositeDeviceStatus();
        EnumDeviceStatus expectedStatus = task.getDeviceOperation().equals(EnumDeviceOperation.ACTIVATE) ?
                EnumDeviceStatus.ON : EnumDeviceStatus.OFF;
        Mono<Device> updatedDeviceStatusResponse = this.deviceRepository.updateStatusByTaskOperation(task);
        StepVerifier.create(updatedDeviceStatusResponse)
                .assertNext(deviceResponse -> {
                    assertThat(deviceResponse).isNotNull();
                    assertThat(deviceResponse.getId()).isEqualTo(task.getDeviceId());
                    assertThat(deviceResponse.getStatus()).isEqualTo(expectedStatus);
                    BeanUtils.copyProperties(deviceResponse, deviceTest);
                })
                .verifyComplete();
    }

    @Test
    @Order(6)
    @DisplayName("Find Updated Device - 2")
    void givenDeviceStatusUpdated_whenFindById_mustReturnUpdateDeviceStatusTwo() {
        Mono<Device> deviceResponse = this.deviceRepository.findById(DEVICE_ID);
        StepVerifier.create(deviceResponse)
                .assertNext(device -> {
                    assertThat(device).isNotNull();
                    assertThat(device.getId()).isEqualTo(DEVICE_ID);
                    assertThat(device.getName()).isEqualTo("Device 123");
                    assertThat(device.getDescription()).isEqualTo("Device 123 description");
                    assertThat(device.getStatus()).isEqualTo(deviceTest.getStatus());
                })
                .verifyComplete();
    }

    @Test
    @Order(7)
    @DisplayName("Find not existing Device ID")
    void givenDeviceId_whenFindById_mustThrowException() {
        Mono<Device> deviceMonoResponse = this.deviceRepository.findById("10000");
        StepVerifier.create(deviceMonoResponse)
                .expectErrorMatches(throwable -> throwable instanceof ResourceNotFoundException)
                .verify();
    }

    @Test
    @Order(8)
    @DisplayName("Update not existing Device ID")
    void givenDeviceItem_whenUpdate_mustThrowException() {
        task.setDeviceId("10000");
        Mono<Device> deviceMonoResponse = this.deviceRepository.updateStatusByTaskOperation(task);
        StepVerifier.create(deviceMonoResponse)
                .expectErrorMatches(throwable -> throwable instanceof ResourceNotFoundException)
                .verify();
    }

    private static void setTaskOperationToOppositeDeviceStatus() {
        if (deviceTest.getStatus().equals(EnumDeviceStatus.ON)) {
            log.info("Device is ON, so we will turn it to OFF.");
            task.setDeviceOperation(EnumDeviceOperation.DEACTIVATE);
        } else {
            log.info("Device is OFF, so we will turn it to ON.");
            task.setDeviceOperation(EnumDeviceOperation.ACTIVATE);
        }
    }
}

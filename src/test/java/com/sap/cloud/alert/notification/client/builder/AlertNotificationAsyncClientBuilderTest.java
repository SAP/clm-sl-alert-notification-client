package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.IAlertNotificationClient;
import com.sap.cloud.alert.notification.client.ICustomerResourceEventBuffer;
import com.sap.cloud.alert.notification.client.internal.AlertNotificationAsyncClient;
import com.sap.cloud.alert.notification.client.internal.InMemoryCustomerResourceEventBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class AlertNotificationAsyncClientBuilderTest {

    public static final int TEST_MIN_THREADS_COUNT = 1;
    public static final int TEST_MAX_THREADS_COUNT = 2;
    public static final Duration TEST_IDLE_THREADS_LIFESPAN = Duration.ofSeconds(1L);

    private ICustomerResourceEventBuffer testEventBuffer;
    private AlertNotificationAsyncClientBuilder classUnderTest;
    private IAlertNotificationClient testAlertNotificationClient;

    @BeforeEach
    public void setUp() {
        testAlertNotificationClient = mock(IAlertNotificationClient.class);
        testEventBuffer = new InMemoryCustomerResourceEventBuffer(1);
        classUnderTest = new AlertNotificationAsyncClientBuilder(testAlertNotificationClient);
    }

    @Test
    public void whenBuildIsCalled_thenCorrectClientIsCreated() {
        AlertNotificationAsyncClient createdClient = classUnderTest
                .withEventBuffer(testEventBuffer)
                .withThreadsCount(TEST_MIN_THREADS_COUNT, TEST_MAX_THREADS_COUNT)
                .withIdleThreadsLifespan(TEST_IDLE_THREADS_LIFESPAN)
                .build();

        assertEquals(testEventBuffer, createdClient.getEventBuffer());
        assertEquals(testAlertNotificationClient, createdClient.getAlertNotificationClient());
    }

    @Test
    public void givenThatInvalidThreadsCountIntervalIsGiven_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            classUnderTest
                    .withEventBuffer(testEventBuffer)
                    .withThreadsCount(TEST_MAX_THREADS_COUNT, TEST_MIN_THREADS_COUNT)
                    .withIdleThreadsLifespan(TEST_IDLE_THREADS_LIFESPAN)
                    .build();
        });
    }
}

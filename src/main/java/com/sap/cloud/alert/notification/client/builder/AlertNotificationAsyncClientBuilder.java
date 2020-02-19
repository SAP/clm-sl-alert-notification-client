package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.IAlertNotificationClient;
import com.sap.cloud.alert.notification.client.ICustomerResourceEventBuffer;
import com.sap.cloud.alert.notification.client.exceptions.BufferOverflowException;
import com.sap.cloud.alert.notification.client.internal.AlertNotificationAsyncClient;
import com.sap.cloud.alert.notification.client.internal.InMemoryCustomerResourceEventBuffer;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public class AlertNotificationAsyncClientBuilder {

    public static final int DEFAULT_ORDERED_EVENT_SENDERS_COUNT = 0;
    public static final int DEFAULT_MIN_THREADS_COUNT = 10;
    public static final int DEFAULT_MAX_THREADS_COUNT = 20;
    public static final int DEFAULT_EVENT_BUFFER_CAPACITY = 100;
    public static final long DEFAULT_IDLE_THREADS_LIFESPAN_SECONDS = 30L;

    private int orderedEventSendersCount = DEFAULT_ORDERED_EVENT_SENDERS_COUNT;
    private int minThreadsCount;
    private int maxThreadsCount;
    private long idleThreadsLifespanInSeconds;
    private ICustomerResourceEventBuffer eventBuffer;
    private IAlertNotificationClient alertNotificationClient;

    public AlertNotificationAsyncClientBuilder(IAlertNotificationClient alertNotificationClient) {
        this.minThreadsCount = DEFAULT_MIN_THREADS_COUNT;
        this.maxThreadsCount = DEFAULT_MAX_THREADS_COUNT;
        this.alertNotificationClient = alertNotificationClient;
        this.idleThreadsLifespanInSeconds = DEFAULT_IDLE_THREADS_LIFESPAN_SECONDS;
        this.eventBuffer = new InMemoryCustomerResourceEventBuffer(DEFAULT_EVENT_BUFFER_CAPACITY);
    }

    public AlertNotificationAsyncClientBuilder withThreadsCount(int minThreadsCount, int maxThreadsCount) {
        this.minThreadsCount = minThreadsCount;
        this.maxThreadsCount = maxThreadsCount;

        return this;
    }

    public AlertNotificationAsyncClientBuilder withOrderedEventSendersCount(int orderedEventSendersCount) {
        this.orderedEventSendersCount = orderedEventSendersCount;

        return this;
    }

    public AlertNotificationAsyncClientBuilder withIdleThreadsLifespan(Duration duration) {
        this.idleThreadsLifespanInSeconds = duration.getSeconds();

        return this;
    }

    public AlertNotificationAsyncClientBuilder withEventBuffer(ICustomerResourceEventBuffer eventBuffer) {
        this.eventBuffer = eventBuffer;

        return this;
    }

    public AlertNotificationAsyncClient build() {
        assertValidThreadCountRange(minThreadsCount, maxThreadsCount, orderedEventSendersCount);

        return new AlertNotificationAsyncClient(createExecutorService(), requireNonNull(eventBuffer), requireNonNull(alertNotificationClient),
                orderedEventSendersCount);
    }

    private ExecutorService createExecutorService() {
        return new ThreadPoolExecutor(
                minThreadsCount,
                maxThreadsCount,
                idleThreadsLifespanInSeconds,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(eventBuffer.getCapacity()),
                (r, executor) -> {
                    throw new BufferOverflowException();
                });
    }

    private static void assertValidThreadCountRange(int minThreadsCount, int maxThreadsCount, int orderedEventSendersCount) {
        if (minThreadsCount < 1 || minThreadsCount > maxThreadsCount || orderedEventSendersCount < 0) {
            throw new IllegalArgumentException();
        }
    }
}

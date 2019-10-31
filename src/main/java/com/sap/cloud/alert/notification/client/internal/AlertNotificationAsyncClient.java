package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.IAlertNotificationAsyncClient;
import com.sap.cloud.alert.notification.client.IAlertNotificationClient;
import com.sap.cloud.alert.notification.client.ICustomerResourceEventBuffer;
import com.sap.cloud.alert.notification.client.QueryParameter;
import com.sap.cloud.alert.notification.client.model.CustomerResourceEvent;
import com.sap.cloud.alert.notification.client.model.PagedResponse;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.Objects.requireNonNull;

public class AlertNotificationAsyncClient implements IAlertNotificationAsyncClient {

    private final ExecutorService executorService;
    private final ICustomerResourceEventBuffer eventBuffer;
    private final IAlertNotificationClient alertNotificationClient;

    public AlertNotificationAsyncClient(
            ExecutorService executorService,
            ICustomerResourceEventBuffer eventBuffer,
            IAlertNotificationClient alertNotificationClient
    ) {
        this.eventBuffer = requireNonNull(eventBuffer);
        this.executorService = requireNonNull(executorService);
        this.alertNotificationClient = requireNonNull(alertNotificationClient);
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public ICustomerResourceEventBuffer getEventBuffer() {
        return eventBuffer;
    }

    public IAlertNotificationClient getAlertNotificationClient() {
        return alertNotificationClient;
    }

    @Override
    public Future<CustomerResourceEvent> sendEvent(CustomerResourceEvent event) {
        UUID eventUUID = eventBuffer.write(event);

        return executorService.submit(() -> alertNotificationClient.sendEvent(eventBuffer.read(eventUUID)));
    }

    @Override
    public Future<PagedResponse> getMatchedEvents(Map<QueryParameter, String> queryParameters) {
        return executorService.submit(() -> alertNotificationClient.getMatchedEvents(queryParameters));
    }

    @Override
    public Future<PagedResponse> getMatchedEvent(String eventId, Map<QueryParameter, String> queryParameters) {
        return executorService.submit(() -> alertNotificationClient.getMatchedEvent(eventId, queryParameters));
    }

    @Override
    public Future<PagedResponse> getUndeliveredEvents(Map<QueryParameter, String> queryParameters) {
        return executorService.submit(() -> alertNotificationClient.getUndeliveredEvents(queryParameters));
    }

    @Override
    public Future<PagedResponse> getUndeliveredEvent(String eventId, Map<QueryParameter, String> queryParameters) {
        return executorService.submit(() -> alertNotificationClient.getUndeliveredEvent(eventId, queryParameters));
    }

    @Override
    public void shutdown() {
        executorService.shutdownNow();
    }
}

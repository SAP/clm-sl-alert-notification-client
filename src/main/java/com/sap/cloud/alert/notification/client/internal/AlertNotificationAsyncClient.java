package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.IAlertNotificationAsyncClient;
import com.sap.cloud.alert.notification.client.IAlertNotificationClient;
import com.sap.cloud.alert.notification.client.ICustomerResourceEventBuffer;
import com.sap.cloud.alert.notification.client.QueryParameter;
import com.sap.cloud.alert.notification.client.model.CustomerResourceEvent;
import com.sap.cloud.alert.notification.client.model.PagedResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static com.sap.cloud.alert.notification.client.model.PredefinedEventTag.SOURCE_EVENT_ID;
import static java.lang.Integer.valueOf;
import static java.lang.Math.abs;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class AlertNotificationAsyncClient implements IAlertNotificationAsyncClient {

    private final int orderedEventSendersCount;
    private final ExecutorService executorService;
    private final Map<Integer, ExecutorService> orderedEventsExecutors;
    private final ICustomerResourceEventBuffer eventBuffer;
    private final IAlertNotificationClient alertNotificationClient;

    public AlertNotificationAsyncClient(
            ExecutorService executorService,
            ICustomerResourceEventBuffer eventBuffer,
            IAlertNotificationClient alertNotificationClient
    ) {
        this(executorService, eventBuffer, alertNotificationClient, 0);
    }

    public AlertNotificationAsyncClient(
            ExecutorService executorService,
            ICustomerResourceEventBuffer eventBuffer,
            IAlertNotificationClient alertNotificationClient,
            int orderedEventSendersCount
    ) {
        this.eventBuffer = requireNonNull(eventBuffer);
        this.executorService = requireNonNull(executorService);
        this.alertNotificationClient = requireNonNull(alertNotificationClient);
        this.orderedEventSendersCount = orderedEventSendersCount;
        this.orderedEventsExecutors = new ConcurrentHashMap<>(orderedEventSendersCount);
        IntStream.range(0, orderedEventSendersCount).boxed()
                .forEach(index -> orderedEventsExecutors.put(index, Executors.newSingleThreadExecutor()));
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public Collection<ExecutorService> getOrderedEventExecutorServices() {
        return unmodifiableCollection(new ArrayList<>(orderedEventsExecutors.values()));
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

        return getEventSenderExecutor(event.getTags().get(SOURCE_EVENT_ID))
                .submit(() -> alertNotificationClient.sendEvent(eventBuffer.read(eventUUID)));
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
        orderedEventsExecutors.values().forEach(ExecutorService::shutdownNow);
    }

    private ExecutorService getEventSenderExecutor(String sourceEventId){
        return orderedEventSendersCount > 0 && nonNull(sourceEventId) && !sourceEventId.isEmpty() ?
                orderedEventsExecutors.get(valueOf(abs(sourceEventId.hashCode() % orderedEventSendersCount))) :
                executorService;
    }
}

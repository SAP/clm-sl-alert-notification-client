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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static com.sap.cloud.alert.notification.client.model.PredefinedEventTag.SOURCE_EVENT_ID;
import static java.lang.Integer.valueOf;
import static java.lang.Math.abs;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.requireNonNull;
import static org.apache.http.util.TextUtils.isBlank;

public class AlertNotificationAsyncClient implements IAlertNotificationAsyncClient {

    private final int orderedEventSendersCount;
    private final ExecutorService executorService;
    private final Map<Integer, ExecutorService> orderedEventsExecutors;
    private final ICustomerResourceEventBuffer eventBuffer;
    private final IAlertNotificationClient alertNotificationClient;

    public AlertNotificationAsyncClient(ExecutorService executorService, ICustomerResourceEventBuffer eventBuffer, IAlertNotificationClient alertNotificationClient) {
        this(executorService, eventBuffer, alertNotificationClient, 0);
    }

    public AlertNotificationAsyncClient(ExecutorService executorService, ICustomerResourceEventBuffer eventBuffer, IAlertNotificationClient alertNotificationClient, int orderedEventSendersCount) {
        this.eventBuffer = requireNonNull(eventBuffer);
        this.executorService = requireNonNull(executorService);
        this.alertNotificationClient = requireNonNull(alertNotificationClient);
        this.orderedEventSendersCount = orderedEventSendersCount;
        this.orderedEventsExecutors = new ConcurrentHashMap<>(orderedEventSendersCount);
        IntStream.range(0, orderedEventSendersCount).boxed().forEach(index -> orderedEventsExecutors.put(index, Executors.newSingleThreadExecutor()));
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
    public CompletableFuture<CustomerResourceEvent> sendEvent(CustomerResourceEvent event) {
        UUID eventUUID = eventBuffer.write(event);

        return CompletableFuture.supplyAsync( //
                () -> alertNotificationClient.sendEvent(eventBuffer.read(eventUUID)), //
                getEventSenderExecutor(event.getTags().get(SOURCE_EVENT_ID)) //
        );
    }

    @Override
    public CompletableFuture<PagedResponse> getMatchedEvents(Map<QueryParameter, String> queryParameters) {
        return CompletableFuture.supplyAsync( //
                () -> alertNotificationClient.getMatchedEvents(queryParameters), //
                executorService //
        );
    }

    @Override
    public CompletableFuture<PagedResponse> getMatchedEvent(String eventId, Map<QueryParameter, String> queryParameters) {
        return CompletableFuture.supplyAsync( //
                () -> alertNotificationClient.getMatchedEvent(eventId, queryParameters), //
                executorService //
        );
    }

    @Override
    public CompletableFuture<PagedResponse> getUndeliveredEvents(Map<QueryParameter, String> queryParameters) {
        return CompletableFuture.supplyAsync( //
                () -> alertNotificationClient.getUndeliveredEvents(queryParameters), //
                executorService //
        );
    }

    @Override
    public CompletableFuture<PagedResponse> getUndeliveredEvent(String eventId, Map<QueryParameter, String> queryParameters) {
        return CompletableFuture.supplyAsync( //
                () -> alertNotificationClient.getUndeliveredEvent(eventId, queryParameters), //
                executorService //
        );
    }

    @Override
    public void shutdown() {
        executorService.shutdownNow();
        orderedEventsExecutors.values().forEach(ExecutorService::shutdownNow);
    }

    private ExecutorService getEventSenderExecutor(String sourceEventId) {
        return orderedEventSendersCount > 0 && !isBlank(sourceEventId) ? orderedEventsExecutors.get(valueOf(abs(sourceEventId.hashCode() % orderedEventSendersCount))) : executorService;
    }
}

package com.sap.cloud.alert.notification.client;

import com.sap.cloud.alert.notification.client.exceptions.BufferOverflowException;
import com.sap.cloud.alert.notification.client.model.CustomerResourceEvent;
import com.sap.cloud.alert.notification.client.model.PagedResponse;

import java.util.Map;
import java.util.concurrent.Future;

public interface IAlertNotificationAsyncClient {

    /**
     * Posts an event for async processing.
     *
     * @param event
     * @throws BufferOverflowException if the event cannot be accepted for execution
     */
    Future<CustomerResourceEvent> sendEvent(CustomerResourceEvent event) throws BufferOverflowException;

    /**
     * Gets events that are matched by client's subscription.
     *
     * @param queryParameters
     * @throws BufferOverflowException if the event cannot be accepted for execution
     */
    Future<PagedResponse> getMatchedEvents(Map<QueryParameter, String> queryParameters) throws BufferOverflowException;

    /**
     * Gets event that is matched by client's subscription.
     *
     * @param eventId
     * @param queryParameters
     * @throws BufferOverflowException if the event cannot be accepted for execution
     */
    Future<PagedResponse> getMatchedEvent(String eventId, Map<QueryParameter, String> queryParameters) throws BufferOverflowException;

    /**
     * Gets events undelivered to some targets.
     *
     * @param queryParameters
     * @throws BufferOverflowException if the event cannot be accepted for execution
     */
    Future<PagedResponse> getUndeliveredEvents(Map<QueryParameter, String> queryParameters) throws BufferOverflowException;

    /**
     * Gets event undelivered to some targets.
     *
     * @param eventId
     * @param queryParameters
     * @throws BufferOverflowException if the event cannot be accepted for execution
     */
    Future<PagedResponse> getUndeliveredEvent(String eventId, Map<QueryParameter, String> queryParameters) throws BufferOverflowException;

    void shutdown();
}

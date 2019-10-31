package com.sap.cloud.alert.notification.client;

import com.sap.cloud.alert.notification.client.exceptions.AuthorizationException;
import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import com.sap.cloud.alert.notification.client.exceptions.ServerResponseException;
import com.sap.cloud.alert.notification.client.model.CustomerResourceEvent;
import com.sap.cloud.alert.notification.client.model.PagedResponse;

import java.util.Map;

public interface IAlertNotificationClient {

    /**
     * Posts an event for processing.
     *
     * @param event the event to be sent
     * @throws ClientRequestException  on failure to contact Alert Notification Service
     * @throws AuthorizationException  on authorization error returned from Alert Notification Service
     * @throws ServerResponseException on error returned from the Alert Notification Service
     */
    CustomerResourceEvent sendEvent(CustomerResourceEvent event) throws ClientRequestException, ServerResponseException;

    /**
     * Gets events that are matched by client's subscription.
     *
     * @param queryParameters
     * @throws ClientRequestException  on failure to contact Alert Notification Service
     * @throws AuthorizationException  on authorization error returned from Alert Notification Service
     * @throws ServerResponseException on error returned from the Alert Notification Service
     */
    PagedResponse getMatchedEvents(Map<QueryParameter, String> queryParameters) throws ClientRequestException, ServerResponseException;

    /**
     * Gets event that is matched by client's subscription.
     *
     * @param eventId
     * @param queryParameters
     * @throws ClientRequestException  on failure to contact Alert Notification Service
     * @throws AuthorizationException  on authorization error returned from Alert Notification Service
     * @throws ServerResponseException on error returned from the Alert Notification Service
     */
    PagedResponse getMatchedEvent(String eventId, Map<QueryParameter, String> queryParameters) throws ClientRequestException, ServerResponseException;

    /**
     * Gets events undelivered to some targets.
     *
     * @param queryParameters
     * @throws ClientRequestException  on failure to contact Alert Notification Service
     * @throws AuthorizationException  on authorization error returned from Alert Notification Service
     * @throws ServerResponseException on error returned from the Alert Notification Service
     */
    PagedResponse getUndeliveredEvents(Map<QueryParameter, String> queryParameters) throws ClientRequestException, ServerResponseException;

    /**
     * Gets event undelivered to some targets.
     *
     * @param eventId
     * @param queryParameters
     * @throws ClientRequestException  on failure to contact Alert Notification Service
     * @throws AuthorizationException  on authorization error returned from Alert Notification Service
     * @throws ServerResponseException on error returned from the Alert Notification Service
     */
    PagedResponse getUndeliveredEvent(String eventId, Map<QueryParameter, String> queryParameters) throws ClientRequestException, ServerResponseException;
}

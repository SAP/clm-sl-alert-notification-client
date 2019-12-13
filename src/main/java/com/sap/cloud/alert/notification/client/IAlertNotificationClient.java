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
     * @param event to be sent to SAP Cloud Platform Alert Notification
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     * @return the posted event enhanced with an unique ID that could be used for tracing
     */
    CustomerResourceEvent sendEvent(CustomerResourceEvent event) throws ClientRequestException, ServerResponseException;

    /**
     * Gets events that are matched by client's subscription.
     *
     * @param queryParameters for filtering of all available matched events
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     * @return found results in pages
     */
    PagedResponse getMatchedEvents(Map<QueryParameter, String> queryParameters) throws ClientRequestException, ServerResponseException;

    /**
     * Gets event that is matched by client's subscription.
     *
     * @param eventId is the ID that was received in the response body when event was sent to SAP Cloud Platform Alert Notification
     * @param queryParameters for filtering of all available events (those could be more than one with the same ID due to multiple matched subscriptions)
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     * @return found results in pages
     */
    PagedResponse getMatchedEvent(String eventId, Map<QueryParameter, String> queryParameters) throws ClientRequestException, ServerResponseException;

    /**
     * Gets events undelivered to some targets.
     *
     * @param queryParameters for filtering of all available undelivered events
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     * @return found results in pages
     */
    PagedResponse getUndeliveredEvents(Map<QueryParameter, String> queryParameters) throws ClientRequestException, ServerResponseException;

    /**
     * Gets event undelivered to some targets.
     *
     * @param eventId is the ID that was received in the response body when event was sent to SAP Cloud Platform Alert Notification
     * @param queryParameters for filtering of all available events (those could be more than one with the same ID due to multiple matched subscriptions)
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     * @return found results in pages
     */
    PagedResponse getUndeliveredEvent(String eventId, Map<QueryParameter, String> queryParameters) throws ClientRequestException, ServerResponseException;
}

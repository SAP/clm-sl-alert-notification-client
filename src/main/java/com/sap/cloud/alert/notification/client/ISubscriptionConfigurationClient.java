package com.sap.cloud.alert.notification.client;

import com.sap.cloud.alert.notification.client.exceptions.AuthorizationException;
import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import com.sap.cloud.alert.notification.client.exceptions.ServerResponseException;
import com.sap.cloud.alert.notification.client.model.configuration.ConfigurationQueryParameter;
import com.sap.cloud.alert.notification.client.model.configuration.ConfigurationResponse;
import com.sap.cloud.alert.notification.client.model.configuration.Subscription;

import java.util.Map;

public interface ISubscriptionConfigurationClient {

    /**
     * Gets all subscriptions of the client.
     *
     * @param queryParameters for specifying page number and page size
     * @throws ClientRequestException  on failure to connect to SAP Alert Notification service for SAP BTP
     * @throws AuthorizationException  on authorization error returned from SAP Alert Notification service for SAP BTP
     * @throws ServerResponseException on error returned from SAP Alert Notification service for SAP BTP
     * @return found results in pages
     */
    ConfigurationResponse<Subscription> getSubscriptions(Map<ConfigurationQueryParameter, String> queryParameters) throws ClientRequestException, ServerResponseException;

    /**
     * Creates a subscription.
     *
     * @param subscription to be created
     * @throws ClientRequestException  on failure to connect to SAP Alert Notification service for SAP BTP
     * @throws AuthorizationException  on authorization error returned from SAP Alert Notification service for SAP BTP
     * @throws ServerResponseException on error returned from SAP Alert Notification service for SAP BTP
     * @return the created subscription
     */
    Subscription createSubscription(Subscription subscription) throws ClientRequestException, ServerResponseException;

    /**
     * Gets a subscription.
     *
     * @param subscriptionName the name of the subscription to be retrieved
     * @throws ClientRequestException  on failure to connect to SAP Alert Notification service for SAP BTP
     * @throws AuthorizationException  on authorization error returned from SAP Alert Notification service for SAP BTP
     * @throws ServerResponseException on error returned from SAP Alert Notification service for SAP BTP
     * @return the subscription identified by its name
     */
    Subscription getSubscription(String subscriptionName) throws ClientRequestException, ServerResponseException;

    /**
     * Updates a subscription.
     *
     * @param subscription to be updated
     * @throws ClientRequestException  on failure to connect to SAP Alert Notification service for SAP BTP
     * @throws AuthorizationException  on authorization error returned from SAP Alert Notification service for SAP BTP
     * @throws ServerResponseException on error returned from SAP Alert Notification service for SAP BTP
     * @return the updated subscription
     */
    Subscription updateSubscription(Subscription subscription) throws ClientRequestException, ServerResponseException;

    /**
     * Deletes a subscription.
     *
     * @param subscriptionName the name of the subscription to be deleted
     * @throws ClientRequestException  on failure to connect to SAP Alert Notification service for SAP BTP
     * @throws AuthorizationException  on authorization error returned from SAP Alert Notification service for SAP BTP
     * @throws ServerResponseException on error returned from SAP Alert Notification service for SAP BTP
     */
    void deleteSubscription(String subscriptionName) throws ClientRequestException, ServerResponseException;

}

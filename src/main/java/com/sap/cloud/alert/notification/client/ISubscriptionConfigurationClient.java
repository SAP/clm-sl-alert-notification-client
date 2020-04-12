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
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     * @return found results in pages
     */
    ConfigurationResponse<Subscription> getSubscriptions(Map<ConfigurationQueryParameter, String> queryParameters) throws ClientRequestException, ServerResponseException;

    /**
     * Creates a subscription.
     *
     * @param subscription to be created
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     * @return the created subscription
     */
    Subscription createSubscription(Subscription subscription) throws ClientRequestException, ServerResponseException;

    /**
     * Gets a subscription.
     *
     * @param subscriptionName the name of the subscription to be retrieved
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     * @return the subscription identified by its name
     */
    Subscription getSubscription(String subscriptionName) throws ClientRequestException, ServerResponseException;

    /**
     * Updates a subscription.
     *
     * @param subscription to be updated
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     * @return the updated subscription
     */
    Subscription updateSubscription(Subscription subscription) throws ClientRequestException, ServerResponseException;

    /**
     * Deletes a subscription.
     *
     * @param subscriptionName the name of the subscription to be deleted
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     */
    void deleteSubscription(String subscriptionName) throws ClientRequestException, ServerResponseException;

}

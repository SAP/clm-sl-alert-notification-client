package com.sap.cloud.alert.notification.client;

import com.sap.cloud.alert.notification.client.exceptions.AuthorizationException;
import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import com.sap.cloud.alert.notification.client.exceptions.ServerResponseException;
import com.sap.cloud.alert.notification.client.model.configuration.Condition;
import com.sap.cloud.alert.notification.client.model.configuration.ConfigurationQueryParameter;
import com.sap.cloud.alert.notification.client.model.configuration.ConfigurationResponse;

import java.util.Map;

public interface IConditionConfigurationClient {

    /**
     * Gets all conditions of the client.
     *
     * @param queryParameters for specifying page number and page size
     * @throws ClientRequestException  on failure to connect to SAP Alert Notification service for SAP BTP
     * @throws AuthorizationException  on authorization error returned from SAP Alert Notification service for SAP BTP
     * @throws ServerResponseException on error returned from SAP Alert Notification service for SAP BTP
     * @return found results in pages
     */
    ConfigurationResponse<Condition> getConditions(Map<ConfigurationQueryParameter, String> queryParameters) throws ClientRequestException, ServerResponseException;

    /**
     * Creates a condition.
     *
     * @param condition to be created
     * @throws ClientRequestException  on failure to connect to SAP Alert Notification service for SAP BTP
     * @throws AuthorizationException  on authorization error returned from SAP Alert Notification service for SAP BTP
     * @throws ServerResponseException on error returned from SAP Alert Notification service for SAP BTP
     * @return the created condition
     */
    Condition createCondition(Condition condition) throws ClientRequestException, ServerResponseException;

    /**
     * Gets a condition.
     *
     * @param conditionName the name of the condition to be retrieved
     * @throws ClientRequestException  on failure to connect to SAP Alert Notification service for SAP BTP
     * @throws AuthorizationException  on authorization error returned from SAP Alert Notification service for SAP BTP
     * @throws ServerResponseException on error returned from SAP Alert Notification service for SAP BTP
     * @return the condition identified by its name
     */
    Condition getCondition(String conditionName) throws ClientRequestException, ServerResponseException;

    /**
     * Updates a condition.
     *
     * @param condition to be updated
     * @throws ClientRequestException  on failure to connect to SAP Alert Notification service for SAP BTP
     * @throws AuthorizationException  on authorization error returned from SAP Alert Notification service for SAP BTP
     * @throws ServerResponseException on error returned from SAP Alert Notification service for SAP BTP
     * @return the updated condition
     */
    Condition updateCondition(Condition condition) throws ClientRequestException, ServerResponseException;

    /**
     * Deletes a condition.
     *
     * @param conditionName the name of the condition to be deleted
     * @throws ClientRequestException  on failure to connect to SAP Alert Notification service for SAP BTP
     * @throws AuthorizationException  on authorization error returned from SAP Alert Notification service for SAP BTP
     * @throws ServerResponseException on error returned from SAP Alert Notification service for SAP BTP
     */
    void deleteCondition(String conditionName) throws ClientRequestException, ServerResponseException;

}

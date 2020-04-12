package com.sap.cloud.alert.notification.client;

import com.sap.cloud.alert.notification.client.exceptions.AuthorizationException;
import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import com.sap.cloud.alert.notification.client.exceptions.ServerResponseException;
import com.sap.cloud.alert.notification.client.model.configuration.Action;
import com.sap.cloud.alert.notification.client.model.configuration.ConfigurationQueryParameter;
import com.sap.cloud.alert.notification.client.model.configuration.ConfigurationResponse;

import java.util.Map;

public interface IActionConfigurationClient {

    /**
     * Gets all actions of the client.
     *
     * @param queryParameters for specifying page number and page size
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     * @return found results in pages
     */
    ConfigurationResponse<Action> getActions(Map<ConfigurationQueryParameter, String> queryParameters) throws ClientRequestException, ServerResponseException;

    /**
     * Creates an action.
     *
     * @param action to be created
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     * @return the created action
     */
    Action createAction(Action action) throws ClientRequestException, ServerResponseException;

    /**
     * Gets an action.
     *
     * @param actionName the name of the action to be retrieved
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     * @return the action identified by its name
     */
    Action getAction(String actionName) throws ClientRequestException, ServerResponseException;

    /**
     * Updates an action.
     *
     * @param action to be updated
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     * @return the updated action
     */
    Action updateAction(Action action) throws ClientRequestException, ServerResponseException;

    /**
     * Deletes an action.
     *
     * @param actionName the name of the action to be deleted
     * @throws ClientRequestException  on failure to connect to SAP Cloud Platform Alert Notification
     * @throws AuthorizationException  on authorization error returned from SAP Cloud Platform Alert Notification
     * @throws ServerResponseException on error returned from SAP Cloud Platform Alert Notification
     */
    void deleteAction(String actionName) throws ClientRequestException, ServerResponseException;

}

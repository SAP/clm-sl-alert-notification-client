package com.sap.cloud.alert.notification.client.model;

/**
 * Contains predefined {@code String} values that can be used as keys for the {@code tags} within {@link CustomerResourceEvent} instances.
 * Tags with keys matching the ones specified here are used for special purposes by <b>SAP Alert Notification service for SAP BTP</b>.
 * Check <i>Producer API</i> of <b>SAP Alert Notification service for SAP BTP</b> in <a href="https://api.sap.com/package/AlertNotification?section=Artifacts">SAP API Business Hub</a> for details.
 *
 * @see CustomerResourceEvent
 * @see <a href="https://api.sap.com/package/AlertNotification?section=Artifacts">SAP Alert Notification service for SAP BTP in SAP API Business Hub</a>
 */
public interface PredefinedEventTag {
    String STATUS = "ans:status";
    String DETAILS_LINK = "ans:detailsLink";
    String CORRELATION_ID = "ans:correlationId";
    String SOURCE_EVENT_ID = "ans:sourceEventId";
    String RECOMMENDED_ACTION_LINK = "ans:recommendedActionLink";
}

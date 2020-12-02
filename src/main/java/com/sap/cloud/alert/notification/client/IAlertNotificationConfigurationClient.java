package com.sap.cloud.alert.notification.client;

import com.sap.cloud.alert.notification.client.model.configuration.Configuration;

public interface IAlertNotificationConfigurationClient extends IActionConfigurationClient, IConditionConfigurationClient, ISubscriptionConfigurationClient {

    Configuration importConfiguration(Configuration newConfiguration);

    Configuration exportConfiguration();
}

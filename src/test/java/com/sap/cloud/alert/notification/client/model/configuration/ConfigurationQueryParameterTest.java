package com.sap.cloud.alert.notification.client.model.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigurationQueryParameterTest {

    @Test
    public void whenGetKeyIsCalled_thenCorrectValueIsReturned() {
        assertEquals(5, ConfigurationQueryParameter.values().length);

        assertEquals("page", ConfigurationQueryParameter.PAGE.getKey());
        assertEquals("pageSize", ConfigurationQueryParameter.PAGE_SIZE.getKey());
        assertEquals("actionName", ConfigurationQueryParameter.ACTION_NAME.getKey());
        assertEquals("conditionName", ConfigurationQueryParameter.CONDITION_NAME.getKey());
        assertEquals("subscriptionName", ConfigurationQueryParameter.SUBSCRIPTION_NAME.getKey());
    }
}

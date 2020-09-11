package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.model.configuration.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static com.sap.cloud.alert.notification.client.model.configuration.State.ENABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubscriptionBuilderTest {

    private SubscriptionBuilder classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new SubscriptionBuilder();
    }

    @Test
    public void whenBuildIsCalled_thenCorrectSubscriptionIsReturned() {
        Subscription testSubscription = classUnderTest //
                .withName(TEST_NAME) //
                .withState(TEST_STATE) //
                .withSnoozeTimestamp(TEST_TIMESTAMP) //
                .withDescription(TEST_DESCRIPTION) //
                .withLabels(TEST_LABELS) //
                .withActions(TEST_ACTIONS) //
                .withConditions(TEST_CONDITIONS) //
                .build();

        Subscription expectedSubscription = new Subscription( //
                TEST_NAME, //
                TEST_STATE, //
                TEST_TIMESTAMP, //
                TEST_DESCRIPTION, //
                TEST_LABELS, //
                TEST_ACTIONS, //
                TEST_CONDITIONS //
        );

        assertEquals(expectedSubscription, testSubscription);
    }

    @Test
    public void givenThatNoStateIsSpecified_whenBuildIsCalled_thenCorrectSubscriptionIsReturned() {
        Subscription testSubscription = classUnderTest //
                .withName(TEST_NAME) //
                .withSnoozeTimestamp(TEST_TIMESTAMP) //
                .withDescription(TEST_DESCRIPTION) //
                .withLabels(TEST_LABELS) //
                .withActions(TEST_ACTIONS) //
                .withConditions(TEST_CONDITIONS) //
                .build();

        Subscription expectedSubscription = new Subscription( //
                TEST_NAME, //
                ENABLED, //
                TEST_TIMESTAMP, //
                TEST_DESCRIPTION, //
                TEST_LABELS, //
                TEST_ACTIONS, //
                TEST_CONDITIONS //
        );

        assertEquals(expectedSubscription, testSubscription);
    }

    @Test
    public void givenThatNullNameIsSpecified_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> classUnderTest //
                .withName(null) //
                .withState(TEST_STATE) //
                .withSnoozeTimestamp(TEST_TIMESTAMP) //
                .withDescription(TEST_DESCRIPTION) //
                .withLabels(TEST_LABELS) //
                .withActions(TEST_ACTIONS) //
                .withConditions(TEST_CONDITIONS) //
                .build()
        );
    }

    @Test
    public void givenThatNullStateIsSpecified_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> classUnderTest //
                .withName(TEST_NAME) //
                .withState(null) //
                .withSnoozeTimestamp(TEST_TIMESTAMP) //
                .withDescription(TEST_DESCRIPTION) //
                .withLabels(TEST_LABELS) //
                .withActions(TEST_ACTIONS) //
                .withConditions(TEST_CONDITIONS) //
                .build()
        );
    }
}

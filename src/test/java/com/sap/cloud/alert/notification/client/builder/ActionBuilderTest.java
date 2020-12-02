package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.model.configuration.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static com.sap.cloud.alert.notification.client.model.configuration.State.ENABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ActionBuilderTest {

    private ActionBuilder classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new ActionBuilder();
    }

    @Test
    public void whenBuildIsCalled_thenCorrectActionIsReturned() {
        Action testAction = classUnderTest //
                .withType(TEST_TYPE) //
                .withName(TEST_NAME) //
                .withState(TEST_STATE) //
                .withDescription(TEST_DESCRIPTION) //
                .withLabels(TEST_LABELS) //
                .withFallbackTime(TEST_FALLBACK_TIME) //
                .withFallbackAction(TEST_FALLBACK_ACTION) //
                .withProperties(TEST_PROPERTIES) //
                .build();

        Action expectedAction = new Action( //
                TEST_TYPE, //
                TEST_NAME, //
                TEST_STATE, //
                TEST_DESCRIPTION, //
                TEST_LABELS, //
                TEST_FALLBACK_TIME, //
                TEST_FALLBACK_ACTION, //
                TEST_PROPERTIES //
        );

        assertEquals(expectedAction, testAction);
    }

    @Test
    public void givenThatNoStateIsSpecified_whenBuildIsCalled_thenCorrectActionIsReturned() {
        Action testAction = classUnderTest //
                .withType(TEST_TYPE) //
                .withName(TEST_NAME) //
                .withDescription(TEST_DESCRIPTION) //
                .withLabels(TEST_LABELS) //
                .withFallbackTime(TEST_FALLBACK_TIME) //
                .withFallbackAction(TEST_FALLBACK_ACTION) //
                .withProperties(TEST_PROPERTIES) //
                .build();

        Action expectedAction = new Action( //
                TEST_TYPE, //
                TEST_NAME, //
                ENABLED, //
                TEST_DESCRIPTION, //
                TEST_LABELS, //
                TEST_FALLBACK_TIME, //
                TEST_FALLBACK_ACTION, //
                TEST_PROPERTIES //
        );

        assertEquals(expectedAction, testAction);
    }

    @Test
    public void givenThatNullTypeIsSpecified_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> classUnderTest //
                .withType(null) //
                .withName(TEST_NAME) //
                .withState(TEST_STATE) //
                .withDescription(TEST_DESCRIPTION) //
                .withLabels(TEST_LABELS) //
                .withFallbackTime(TEST_FALLBACK_TIME) //
                .withFallbackAction(TEST_FALLBACK_ACTION) //
                .withProperties(TEST_PROPERTIES) //
                .build()
        );
    }

    @Test
    public void givenThatNullNameIsSpecified_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> classUnderTest //
                .withType(TEST_TYPE) //
                .withName(null) //
                .withState(TEST_STATE) //
                .withDescription(TEST_DESCRIPTION) //
                .withLabels(TEST_LABELS) //
                .withFallbackTime(TEST_FALLBACK_TIME) //
                .withFallbackAction(TEST_FALLBACK_ACTION) //
                .withProperties(TEST_PROPERTIES) //
                .build()
        );
    }

    @Test
    public void givenThatNullStateIsSpecified_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> classUnderTest //
                .withType(TEST_TYPE) //
                .withName(TEST_NAME) //
                .withState(null) //
                .withDescription(TEST_DESCRIPTION) //
                .withLabels(TEST_LABELS) //
                .withFallbackTime(TEST_FALLBACK_TIME) //
                .withFallbackAction(TEST_FALLBACK_ACTION) //
                .withProperties(TEST_PROPERTIES) //
                .build()
        );
    }
}

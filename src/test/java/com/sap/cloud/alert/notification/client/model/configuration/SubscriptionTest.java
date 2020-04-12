package com.sap.cloud.alert.notification.client.model.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static java.lang.String.format;
import static org.apache.commons.collections4.SetUtils.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionTest {

    private static final String TEST_SUBSCRIPTION_AS_JSON_STRING = "{"
            + format("\"id\":\"%s\",", TEST_ID)
            + format("\"name\":\"%s\",", TEST_NAME)
            + format("\"state\":\"%s\",", TEST_STATE.name())
            + format("\"snoozeTimestamp\":%d,", TEST_TIMESTAMP)
            + format("\"description\":\"%s\",", TEST_DESCRIPTION)
            + format("\"labels\":%s,", toJsonString(TEST_LABELS))
            + format("\"actions\":%s,", toJsonString(TEST_ACTIONS))
            + format("\"conditions\":%s,", toJsonString(TEST_CONDITIONS))
            + format("\"timeCreated\":%d,", TEST_TIME_CREATED)
            + format("\"lastModified\":%d", TEST_LAST_MODIFIED)
            + "}";

    private Subscription classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new Subscription(
                TEST_ID,
                TEST_NAME,
                TEST_STATE,
                TEST_TIMESTAMP,
                TEST_DESCRIPTION,
                TEST_LABELS,
                TEST_ACTIONS,
                TEST_CONDITIONS,
                TEST_TIME_CREATED,
                TEST_LAST_MODIFIED
        );
    }

    @Test
    public void whenSubscriptionIsSerializedToJson_thenCorrectJsonIsBuilt() {
        assertEquals(TEST_SUBSCRIPTION_AS_JSON_STRING, toJsonString(classUnderTest));
    }

    @Test
    public void wheSubscriptionIsDeserializedFromJson_thenCorrectInstanceIsBuilt() {
        assertEquals(classUnderTest, fromJsonString(TEST_SUBSCRIPTION_AS_JSON_STRING, Subscription.class));
    }

    @Test
    public void whenGetIdIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_ID, classUnderTest.getId());
    }

    @Test
    public void whenGetNameIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_NAME, classUnderTest.getName());
    }

    @Test
    public void whenGetStateIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_STATE, classUnderTest.getState());
    }

    @Test
    public void whenGetSnoozeTimestampIsCalled_thenCorrectValueIsReturned(){
        assertEquals(TEST_TIMESTAMP, classUnderTest.getSnoozeTimestamp());
    }

    @Test
    public void whenGetDescriptionIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_DESCRIPTION, classUnderTest.getDescription());
    }

    @Test
    public void whenGetLabelsIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_LABELS, classUnderTest.getLabels());
    }

    @Test
    public void whenGetActionsIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_ACTIONS, classUnderTest.getActions());
    }

    @Test
    public void whenGetConditionsIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_CONDITIONS, classUnderTest.getConditions());
    }

    @Test
    public void whenGetTimeCreatedIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_TIME_CREATED, classUnderTest.getTimeCreated());
    }

    @Test
    public void whenGetLastModifiedIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_LAST_MODIFIED, classUnderTest.getLastModified());
    }

    @Test
    public void givenThatLabelsAreNull_whenConstructorIsInvoked_thenLabelsAreSetToAnEmptyCollection() {
        classUnderTest = new Subscription( //
                TEST_ID, //
                TEST_NAME, //
                TEST_STATE, //
                TEST_TIMESTAMP, //
                TEST_DESCRIPTION, //
                null, //
                TEST_ACTIONS, //
                TEST_CONDITIONS, //
                TEST_TIME_CREATED, //
                TEST_LAST_MODIFIED //
        );

        assertEquals(emptySet(), classUnderTest.getLabels());
    }

    @Test
    public void givenThatActionsAreNull_whenConstructorIsInvoked_thenActionsAreSetToAnEmptyCollection() {
        classUnderTest = new Subscription( //
                TEST_ID, //
                TEST_NAME, //
                TEST_STATE, //
                TEST_TIMESTAMP, //
                TEST_DESCRIPTION, //
                TEST_LABELS, //
                null, //
                TEST_CONDITIONS, //
                TEST_TIME_CREATED, //
                TEST_LAST_MODIFIED //
        );

        assertEquals(emptySet(), classUnderTest.getActions());
    }

    @Test
    public void givenThatConditionsAreNull_whenConstructorIsInvoked_thenConditionsAreSetToAnEmptyCollection() {
        classUnderTest = new Subscription( //
                TEST_ID, //
                TEST_NAME, //
                TEST_STATE, //
                TEST_TIMESTAMP, //
                TEST_DESCRIPTION, //
                TEST_LABELS, //
                TEST_ACTIONS, //
                null, //
                TEST_TIME_CREATED, //
                TEST_LAST_MODIFIED //
        );

        assertEquals(emptySet(), classUnderTest.getConditions());
    }
}

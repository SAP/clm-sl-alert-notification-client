package com.sap.cloud.alert.notification.client.model.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static org.apache.commons.collections4.SetUtils.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionTest {

    private static final String TEST_ACTION_AS_JSON_STRING = "{" 
            + format("\"id\":\"%s\",", TEST_ID) 
            + format("\"type\":\"%s\",", TEST_TYPE) 
            + format("\"name\":\"%s\",", TEST_NAME) 
            + format("\"state\":\"%s\",", TEST_STATE.name()) 
            + format("\"description\":\"%s\",", TEST_DESCRIPTION) 
            + format("\"labels\":%s,", toJsonString(TEST_LABELS))
            + format("\"fallbackTime\":%d,", TEST_FALLBACK_TIME) 
            + format("\"fallbackAction\":\"%s\",", TEST_FALLBACK_ACTION) 
            + format("\"properties\":%s,", toJsonString(TEST_PROPERTIES)) 
            + format("\"timeCreated\":%d,", TEST_TIME_CREATED) 
            + format("\"lastModified\":%d", TEST_LAST_MODIFIED) 
            + "}";

    private Action classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new Action( 
                TEST_ID, 
                TEST_TYPE, 
                TEST_NAME, 
                TEST_STATE, 
                TEST_DESCRIPTION, 
                TEST_LABELS, 
                TEST_FALLBACK_TIME, 
                TEST_FALLBACK_ACTION, 
                TEST_PROPERTIES, 
                TEST_TIME_CREATED, 
                TEST_LAST_MODIFIED 
        );
    }

    @Test
    public void whenActionIsSerializedToJson_thenCorrectJsonIsBuilt() {
        assertEquals(TEST_ACTION_AS_JSON_STRING, toJsonString(classUnderTest));
    }

    @Test
    public void whenActionIsDeserializedFromJson_thenCorrectInstanceIsBuilt() {
        assertEquals(classUnderTest, fromJsonString(TEST_ACTION_AS_JSON_STRING, Action.class));
    }

    @Test
    public void whenGetIdIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_ID, classUnderTest.getId());
    }

    @Test
    public void whenGetTypeKeyIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_TYPE, classUnderTest.getType());
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
    public void whenGetDescriptionIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_DESCRIPTION, classUnderTest.getDescription());
    }

    @Test
    public void whenGetLabelsIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_LABELS, classUnderTest.getLabels());
    }

    @Test
    public void whenGetFallbackTimeIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_FALLBACK_TIME, classUnderTest.getFallbackTime());
    }

    @Test
    public void whenGetFallbackActionIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_FALLBACK_ACTION, classUnderTest.getFallbackAction());
    }

    @Test
    public void whenGetPropertiesIsCalled_thenCorrectValueIsReturned() {
        assertEquals(TEST_PROPERTIES, classUnderTest.getProperties());
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
        classUnderTest = new Action( 
                TEST_ID, 
                TEST_TYPE, 
                TEST_NAME, 
                TEST_STATE, 
                TEST_DESCRIPTION, 
                null, 
                TEST_FALLBACK_TIME, 
                TEST_FALLBACK_ACTION, 
                TEST_PROPERTIES, 
                TEST_TIME_CREATED, 
                TEST_LAST_MODIFIED 
        );

        assertEquals(emptySet(), classUnderTest.getLabels());
    }

    @Test
    public void givenThatPropertiesAreNull_whenConstructorIsInvoked_thenPropertiesAreSetToAnEmptyMap() {
        classUnderTest = new Action( 
                TEST_ID, 
                TEST_TYPE, 
                TEST_NAME, 
                TEST_STATE, 
                TEST_DESCRIPTION, 
                TEST_LABELS, 
                TEST_FALLBACK_TIME, 
                TEST_FALLBACK_ACTION, 
                null, 
                TEST_TIME_CREATED, 
                TEST_LAST_MODIFIED 
        );

        assertEquals(emptyMap(), classUnderTest.getProperties());
    }
}

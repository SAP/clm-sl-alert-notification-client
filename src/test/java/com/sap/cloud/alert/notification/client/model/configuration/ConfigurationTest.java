package com.sap.cloud.alert.notification.client.model.configuration;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigurationTest {

    private static final Class<Configuration> CLASS = Configuration.class;

    // @formatter:off
    private static final String TEST_CONFIGURATION_AS_JSON = "{" //
        + "\"actions\":[{"
        + format("\"id\":\"%s\",", TEST_ID) //
        + format("\"type\":\"%s\",", TEST_TYPE) //
        + format("\"name\":\"%s\",", TEST_ACTION_NAME) //
        + format("\"state\":\"%s\",", TEST_STATE.name()) //
        + format("\"description\":\"%s\",", TEST_DESCRIPTION) //
        + format("\"labels\":%s,", toJsonString(TEST_LABELS)) //
        + format("\"discardAfter\":%d,", TEST_DISCARD_AFTER) //
        + format("\"fallbackTime\":%d,", TEST_FALLBACK_TIME) //
        + format("\"fallbackAction\":\"%s\",", TEST_FALLBACK_ACTION) //
        + format("\"enableDeliveryStatus\":%s,", TEST_ENABLE_DELIVERY_STATUS) //
        + format("\"properties\":%s,", toJsonString(TEST_PROPERTIES)) //
        + format("\"timeCreated\":%d,", TEST_TIME_CREATED) //
        + format("\"lastModified\":%d", TEST_LAST_MODIFIED) //
        + "}],"
        + "\"conditions\":[{"
        + format("\"id\":\"%s\",", TEST_ID) //
        + format("\"name\":\"%s\",", TEST_CONDITION_NAME) //
        + format("\"description\":\"%s\",", TEST_DESCRIPTION) //
        + format("\"propertyKey\":\"%s\",", TEST_PROPERTY_KEY) //
        + format("\"predicate\":\"%s\",", TEST_PREDICATE.name()) //
        + format("\"propertyValue\":\"%s\",", TEST_PROPERTY_VALUE) //
        + format("\"mandatory\":%s,", TEST_MANDATORY) //
        + format("\"labels\":%s,", toJsonString(TEST_LABELS)) //
        + format("\"timeCreated\":%d,", TEST_TIME_CREATED) //
        + format("\"lastModified\":%d", TEST_LAST_MODIFIED) //
        + "}],"
        + "\"subscriptions\":[{"
        + format("\"id\":\"%s\",", TEST_ID) //
        + format("\"name\":\"%s\",", TEST_SUBSCRIPTION_NAME) //
        + format("\"state\":\"%s\",", TEST_STATE.name()) //
        + format("\"snoozeTimestamp\":%d,", TEST_TIMESTAMP) //
        + format("\"description\":\"%s\",", TEST_DESCRIPTION) //
        + format("\"labels\":%s,", toJsonString(TEST_LABELS)) //
        + format("\"actions\":%s,", toJsonString(TEST_ACTIONS)) //
        + format("\"conditions\":%s,", toJsonString(TEST_CONDITIONS)) //
        + format("\"timeCreated\":%d,", TEST_TIME_CREATED) //
        + format("\"lastModified\":%d", TEST_LAST_MODIFIED) //
        + "}]"
        + "}";
    // @formatter:on

    private Action action;
    private Condition condition;
    private Subscription subscription;
    private Configuration classUnderTest;

    @BeforeEach
    public void setUp() {
        action = new Action(TEST_ID, TEST_TYPE, TEST_ACTION_NAME, TEST_STATE, TEST_DESCRIPTION, TEST_LABELS, TEST_DISCARD_AFTER, TEST_FALLBACK_TIME, TEST_FALLBACK_ACTION, TEST_ENABLE_DELIVERY_STATUS,
                TEST_PROPERTIES, TEST_TIME_CREATED, TEST_LAST_MODIFIED);
        condition = new Condition(TEST_ID, TEST_CONDITION_NAME, TEST_DESCRIPTION, TEST_PROPERTY_KEY, TEST_PREDICATE, TEST_PROPERTY_VALUE, TEST_MANDATORY, TEST_LABELS, TEST_TIME_CREATED, TEST_LAST_MODIFIED);
        subscription = new Subscription(TEST_ID, TEST_SUBSCRIPTION_NAME, TEST_STATE, TEST_TIMESTAMP, TEST_DESCRIPTION, TEST_LABELS, TEST_ACTIONS, TEST_CONDITIONS, TEST_TIME_CREATED,
                TEST_LAST_MODIFIED);
        classUnderTest = new Configuration(singletonList(action), singletonList(condition), singletonList(subscription));
    }

    @Test
    public void verifyThatClassIsCorrectlyAnnotated() throws NoSuchMethodException {
        assertNotNull(CLASS.getDeclaredMethod("equals", Object.class));
        assertNotNull(CLASS.getDeclaredMethod("hashCode"));
        assertNotNull(CLASS.getDeclaredMethod("toString"));
    }

    @Test
    public void verifyThatConstructorIsCorrectlyAnnotated() throws NoSuchMethodException {
        Constructor<?> constructor = CLASS.getConstructor(Collection.class, Collection.class, Collection.class);

        assertNotNull(constructor.getAnnotation(JsonCreator.class));
        assertEquals(asList("actions", "conditions", "subscriptions"), //
                stream(constructor.getParameters()).map(parameter -> parameter.getAnnotation(JsonProperty.class).value()).collect(toList()));
    }

    @Test
    public void verifyThatGetterAreCorrectlyAnnotated() throws NoSuchMethodException {
        assertEquals("conditions", CLASS.getDeclaredMethod("getConditions").getAnnotation(JsonProperty.class).value());
        assertEquals("actions", CLASS.getDeclaredMethod("getActions").getAnnotation(JsonProperty.class).value());
        assertEquals("subscriptions", CLASS.getDeclaredMethod("getSubscriptions").getAnnotation(JsonProperty.class).value());
    }

    @Test
    public void whenSubscriptionIsSerializedToJson_thenCorrectJsonIsBuilt() {
        assertEquals(TEST_CONFIGURATION_AS_JSON, toJsonString(classUnderTest));
    }

    @Test
    public void wheSubscriptionIsDeserializedFromJson_thenCorrectInstanceIsBuilt() {
        assertEquals(classUnderTest, fromJsonString(TEST_CONFIGURATION_AS_JSON, Configuration.class));
    }

    @Test
    public void whenGettersAreCalled_thenCorrectResultIsReturned() {
        assertEquals(new HashSet<>(singletonList(condition)), classUnderTest.getConditions());
        assertEquals(new HashSet<>(singletonList(action)), classUnderTest.getActions());
        assertEquals(new HashSet<>(singletonList(subscription)), classUnderTest.getSubscriptions());
    }
}

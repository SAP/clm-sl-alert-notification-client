package com.sap.cloud.alert.notification.client.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ErrorHttpResponseTest {

    private static final String TEST_MESSAGE = "TEST_MESSAGE";

    private AlertNotificationClientUtils.ErrorHttpResponse classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new AlertNotificationClientUtils.ErrorHttpResponse(TEST_MESSAGE);
    }

    @Test
    public void verifyThatClassIsCorrectlyAnnotated() throws NoSuchMethodException {
        assertNotNull(classUnderTest.getClass().getDeclaredMethod("hashCode"));
        assertNotNull(classUnderTest.getClass().getDeclaredMethod("equals",Object.class));
        assertEquals(classUnderTest.getClass().getAnnotation(JsonIgnoreProperties.class).ignoreUnknown(), true);
    }

    @Test
    public void verifyThatConstructorsAreCorrectlyAnnotated() throws Exception {
        assertEquals(stream(classUnderTest.getClass().getDeclaredConstructors()).findFirst().get().getAnnotation(JsonCreator.class).annotationType(), JsonCreator.class);
    }

    @Test
    public void verifyThatGettersAreCorrectlyAnnotated() throws Exception {
        assertEquals(classUnderTest.getClass().getMethod("getMessage").getAnnotation(JsonProperty.class).value(), "message");
    }

    @Test
    public void whenGettersAreCalled_thenCorrectResultIsReturned() {
        assertEquals(classUnderTest.getMessage(), TEST_MESSAGE);
    }
}

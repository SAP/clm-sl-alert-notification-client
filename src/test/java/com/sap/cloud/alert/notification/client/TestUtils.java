package com.sap.cloud.alert.notification.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.alert.notification.client.internal.SimpleRetryPolicy;
import com.sap.cloud.alert.notification.client.model.configuration.*;
import org.apache.http.client.HttpClient;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.sap.cloud.alert.notification.client.model.configuration.Predicate.CONTAINS;
import static com.sap.cloud.alert.notification.client.model.configuration.State.ENABLED;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;

public class TestUtils {

    public static final Integer TEST_PAGE = 3;
    public static final Long TEST_TIMESTAMP = 1L;
    public static final String TEST_ID = "TEST_ID";
    public static final State TEST_STATE = ENABLED;
    public static final Long TEST_TOTAL_PAGES = 12L;
    public static final Integer TEST_PAGE_SIZE = 131;
    public static final Integer TEST_FALLBACK_TIME = 2;
    public static final String TEST_NAME = "TEST_NAME";
    public static final String TEST_ACTION_NAME = "TEST_ACTION_NAME";
    public static final String TEST_CONDITION_NAME = "TEST_CONDITION_NAME";
    public static final String TEST_SUBSCRIPTION_NAME = "TEST_SUBSCRIPTION_NAME";
    public static final String TEST_TYPE = "TEST_TYPE";
    public static final Integer TEST_HTTP_ERROR_CODE = 404;
    public static final Predicate TEST_PREDICATE = CONTAINS;
    public static final Long TEST_TOTAL_RESULTS_COUNT = 16L;
    public static final String TEST_MESSAGE = "TEST_MESSAGE";
    public static final Long TEST_TIME_CREATED = 1586082414L;
    public static final Long TEST_LAST_MODIFIED = 1586082415L;
    public static final String TEST_USERNAME = "TEST_USERNAME";
    public static final String TEST_PASSWORD = "TEST_PASSWORD";
    public static final String TEST_PROPERTY_KEY = "eventType";
    public static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    public static final Set<String> TEST_LABELS = singleton("TEST_LABEL");
    public static final String TEST_PROPERTY_VALUE = "TEST_PROPERTY_VALUE";
    public static final Set<String> TEST_ACTIONS = singleton("TEST_ACTION");
    public static final String TEST_FALLBACK_ACTION = "TEST_FALLBACK_ACTION";
    public static final IRetryPolicy TEST_RETRY_POLICY = new SimpleRetryPolicy();
    public static final Set<String> TEST_CONDITIONS = singleton("TEST_CONDITION");
    public static final HttpClient TEST_HTTP_CLIENT = Mockito.mock(HttpClient.class);
    public static final URI TEST_OAUTH_SERVICE_URI = URI.create("https://nowhere.near.a.real.oauth.uri.sap.com");
    public static final Map<String, String> TEST_PROPERTIES = singletonMap("TEST_PROPERTY_KEY", "TEST_PROPERTY_VALUE");
    public static final Condition TEST_CONDITION = new Condition(TEST_NAME, TEST_DESCRIPTION, TEST_PROPERTY_KEY, TEST_PREDICATE,
            TEST_PROPERTY_VALUE, TEST_LABELS);
    public static final PagingMetadata TEST_CONFIGURATION_PAGING_METADATA = new PagingMetadata(TEST_PAGE, TEST_PAGE_SIZE, TEST_TOTAL_PAGES,
            TEST_TOTAL_RESULTS_COUNT);
    public static final ServiceRegion TEST_SERVICE_REGION = ServiceRegion.EU10;
    public static final URI TEST_SERVICE_URI =TEST_SERVICE_REGION.getServiceURI();
    private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(NON_NULL);
    public static final TypeReference<ConfigurationResponse<Condition>> CONDITION_CONFIGURATION_TYPE = new TypeReference<ConfigurationResponse<Condition>>() {

    };

    public static <T> T fromJsonString(String valueAsString, Class<T> clazz) {
        try {
            return JSON_OBJECT_MAPPER.readValue(valueAsString, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonString(String valueAsString, TypeReference<T> typeReference) {
        try {
            return JSON_OBJECT_MAPPER.readValue(valueAsString, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String toJsonString(T value) {
        try {
            return JSON_OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}

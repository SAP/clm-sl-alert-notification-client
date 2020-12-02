package com.sap.cloud.alert.notification.client.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.alert.notification.client.QueryParameter;
import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.exceptions.AuthorizationException;
import com.sap.cloud.alert.notification.client.exceptions.ServerResponseException;
import com.sap.cloud.alert.notification.client.model.configuration.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.IterableUtils.chainedIterable;
import static org.apache.commons.collections4.IterableUtils.toList;
import static org.apache.http.HttpStatus.*;

class AlertNotificationClientUtils {

    public static final String EMPTY = "";
    public static final Class<Action> ACTION_TYPE = Action.class;
    public static final Class<Condition> CONDITION_TYPE = Condition.class;
    public static final Class<Subscription> SUBSCRIPTION_TYPE = Subscription.class;
    public static final Class<Configuration> CONFIGURATION_TYPE = Configuration.class;
    public static final String APPLICATION_JSON = ContentType.APPLICATION_JSON.toString();
    public static final TypeReference<ConfigurationResponse<Action>> ACTION_CONFIGURATION_TYPE = new TypeReference<ConfigurationResponse<Action>>(){};
    public static final TypeReference<ConfigurationResponse<Condition>> CONDITION_CONFIGURATION_TYPE = new TypeReference<ConfigurationResponse<Condition>>(){};
    public static final TypeReference<ConfigurationResponse<Subscription>> SUBSCRIPTION_CONFIGURATION_TYPE = new TypeReference<ConfigurationResponse<Subscription>>(){};

    private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(NON_NULL);
    private static final List<String> PRODUCER_PATH_SEGMENTS = unmodifiableList(asList("producer", "v1", "resource-events"));
    private static final List<String> MATCHED_EVENTS_PATH_SEGMENTS = unmodifiableList(asList("consumer", "v1", "matched-events"));
    private static final List<String> UNDELIVERED_EVENTS_PATH_SEGMENTS = unmodifiableList(asList("consumer", "v1", "undelivered-events"));
    private static final List<String> ACTIONS_CONFIGURATION_PATH_SEGMENTS = unmodifiableList(asList("configuration","v1","action"));
    private static final List<String> CONDITIONS_CONFIGURATION_PATH_SEGMENTS = unmodifiableList(asList("configuration","v1","condition"));
    private static final List<String> SUBSCRIPTIONS_CONFIGURATION_PATH_SEGMENTS= unmodifiableList(asList("configuration","v1","subscription"));
    private static final List<String> CONFIGURATION_MANAGEMENT_PATH_SEGMENTS = unmodifiableList(asList("configuration","v1","configuration"));

    static <T> T fromJsonString(String valueAsString, Class<T> clazz) {
        try {
            return JSON_OBJECT_MAPPER.readValue(valueAsString, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static <T> T fromJsonString(String valueAsString, TypeReference<T> typeReference) {
        try {
            return JSON_OBJECT_MAPPER.readValue(valueAsString, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static <T> String toJsonString(T value) {
        try {
            return JSON_OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static void consumeQuietly(HttpResponse response) {
        if (nonNull(response)) {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    static void assertSuccessfulResponse(HttpResponse response) {
        StatusLine statusLine = response.getStatusLine();
        int code = statusLine.getStatusCode();
        String reason = statusLine.getReasonPhrase();

        if (code < SC_OK || code >= SC_MULTIPLE_CHOICES) {
            throw asList(SC_FORBIDDEN, SC_UNAUTHORIZED).contains(code) ?
                    new AuthorizationException(reason, code) :
                    new ServerResponseException(reason, code);
        }
    }

    static void assertHttpStatus(HttpResponse response, int expected) {
        if (response.getStatusLine().getStatusCode() != expected) {
            throw new ServerResponseException(extractMessage(response), response.getStatusLine().getStatusCode());
        }
    }

    public static String extractMessage(HttpResponse response) {
        try {
            return fromJsonString(EntityUtils.toString(response.getEntity(), UTF_8.name()), ConfigurationErrorResponse.class).getMessage();
        } catch (Exception e) {
            return response.getStatusLine().getReasonPhrase();
        }
    }

    static URI buildProducerURI(ServiceRegion serviceRegion) {
        return buildURI(
                serviceRegion.getServiceURI(),
                toPathSegments(serviceRegion, PRODUCER_PATH_SEGMENTS, emptyList()),
                emptyList()
        );
    }

    static URI buildMatchedEventsURI(ServiceRegion serviceRegion, Map<QueryParameter, String> filters) {
        return buildURI(
                serviceRegion.getServiceURI(),
                toPathSegments(serviceRegion, MATCHED_EVENTS_PATH_SEGMENTS, emptyList()),
                toQueryParameters(filters)
        );
    }

    static URI buildMatchedEventsURI(ServiceRegion serviceRegion, String eventId, Map<QueryParameter, String> filters) {
        return buildURI(
                serviceRegion.getServiceURI(),
                toPathSegments(serviceRegion, MATCHED_EVENTS_PATH_SEGMENTS, asList(eventId)),
                toQueryParameters(filters)
        );
    }

    static URI buildUndeliveredEventsURI(ServiceRegion serviceRegion, Map<QueryParameter, String> filters) {
        return buildURI(
                serviceRegion.getServiceURI(),
                toPathSegments(serviceRegion, UNDELIVERED_EVENTS_PATH_SEGMENTS, emptyList()),
                toQueryParameters(filters)
        );
    }

    static URI buildUndeliveredEventsURI(ServiceRegion serviceRegion, String eventId, Map<QueryParameter, String> filters) {
        return buildURI(
                serviceRegion.getServiceURI(),
                toPathSegments(serviceRegion, UNDELIVERED_EVENTS_PATH_SEGMENTS, asList(eventId)),
                toQueryParameters(filters)
        );
    }

    static URI buildConfigurationManagementUri(ServiceRegion serviceRegion) {
        return buildURI(
            serviceRegion.getServiceURI(),
            toPathSegments(serviceRegion, CONFIGURATION_MANAGEMENT_PATH_SEGMENTS, emptyList()),
            emptyList()
        );
    }

    static URI buildActionUri(ServiceRegion serviceRegion, String actionName) {
        return buildEntityUri(serviceRegion, ACTIONS_CONFIGURATION_PATH_SEGMENTS, actionName);
    }

    static URI buildActionsUri(ServiceRegion serviceRegion, Map<ConfigurationQueryParameter, String> queryParameters) {
        return buildEntitiesUri(serviceRegion, ACTIONS_CONFIGURATION_PATH_SEGMENTS, queryParameters);
    }

    static URI buildConditionUri(ServiceRegion serviceRegion, String conditionName) {
        return buildEntityUri(serviceRegion, CONDITIONS_CONFIGURATION_PATH_SEGMENTS, conditionName);
    }

    static URI buildConditionsUri(ServiceRegion serviceRegion, Map<ConfigurationQueryParameter, String> queryParameters) {
        return buildEntitiesUri(serviceRegion, CONDITIONS_CONFIGURATION_PATH_SEGMENTS, queryParameters);
    }

    static URI buildSubscriptionUri(ServiceRegion serviceRegion, String subscriptionName) {
        return buildEntityUri(serviceRegion, SUBSCRIPTIONS_CONFIGURATION_PATH_SEGMENTS, subscriptionName);
    }

    static URI buildSubscriptionsUri(ServiceRegion serviceRegion, Map<ConfigurationQueryParameter, String> queryParameters) {
        return buildEntitiesUri(serviceRegion, SUBSCRIPTIONS_CONFIGURATION_PATH_SEGMENTS, queryParameters);
    }

    private static List<String> toPathSegments(ServiceRegion serviceRegion, List<String> defaultSegments, List<String> customSegments) {
        return toList(chainedIterable(asList(serviceRegion.getPlatform().getKey()), defaultSegments, customSegments));
    }

    private static URI buildURI(URI serviceURI, List<String> pathSegments, List<NameValuePair> queryParameters) {
        try {
            return new URIBuilder(serviceURI).setPathSegments(pathSegments).setParameters(queryParameters).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static URI buildEntityUri(ServiceRegion serviceRegion, List<String> defaultPathSegments, String entityName) {
        return buildURI(
                serviceRegion.getServiceURI(),
                toPathSegments(serviceRegion, defaultPathSegments, singletonList(entityName)),
                emptyList()
        );
    }

    private static URI buildEntitiesUri(ServiceRegion serviceRegion, List<String> defaultPathSegments, Map<ConfigurationQueryParameter, String> queryParameters) {
        return buildURI(
                serviceRegion.getServiceURI(),
                toPathSegments(serviceRegion, defaultPathSegments, emptyList()),
                toConfigurationQueryParameterPairs(queryParameters)
        );
    }

    private static List<NameValuePair> toQueryParameters(Map<QueryParameter, String> queryFilters) {
        return queryFilters.entrySet().stream()
                .map(queryFilter -> new BasicNameValuePair(queryFilter.getKey().getKey(), queryFilter.getValue()))
                .collect(toList());
    }

    private static List<NameValuePair> toConfigurationQueryParameterPairs(Map<ConfigurationQueryParameter, String> requestParameters) {
        return requestParameters.entrySet().stream()
                .map(queryFilter -> new BasicNameValuePair(queryFilter.getKey().getKey(), queryFilter.getValue()))
                .collect(toList());
    }
}

package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.QueryParameter;
import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.exceptions.AuthorizationException;
import com.sap.cloud.alert.notification.client.exceptions.ServerResponseException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.IterableUtils.chainedIterable;
import static org.apache.commons.collections4.IterableUtils.toList;
import static org.apache.http.HttpStatus.*;

class AlertNotificationClientUtils {

    private static List<String> PRODUCER_PATH_SEGMENTS = unmodifiableList(asList("producer", "v1", "resource-events"));
    private static List<String> MATCHED_EVENTS_PATH_SEGMENTS = unmodifiableList(asList("consumer", "v1", "matched-events"));
    private static List<String> UNDELIVERED_EVENTS_PATH_SEGMENTS = unmodifiableList(asList("consumer", "v1", "undelivered-events"));

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

    private static List<NameValuePair> toQueryParameters(Map<QueryParameter, String> queryFilters) {
        return queryFilters.entrySet().stream()
                .map(queryFilter -> new BasicNameValuePair(queryFilter.getKey().getKey(), queryFilter.getValue()))
                .collect(toList());
    }
}

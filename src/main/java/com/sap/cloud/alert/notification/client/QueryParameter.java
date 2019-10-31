package com.sap.cloud.alert.notification.client;

import static java.util.Objects.requireNonNull;

public class QueryParameter {

    public static final QueryParameter PAGE = new QueryParameter("page");
    public static final QueryParameter INCLUDE = new QueryParameter("include");
    public static final QueryParameter SEVERITY = new QueryParameter("severity");
    public static final QueryParameter CATEGORY = new QueryParameter("category");
    public static final QueryParameter PAGE_SIZE = new QueryParameter("pageSize");
    public static final QueryParameter EVENT_TYPE = new QueryParameter("eventType");
    public static final QueryParameter RESOURCE_NAME = new QueryParameter("resourceName");
    public static final QueryParameter CORRELATION_ID = new QueryParameter("correlationId");
    public static final QueryParameter SOURCE_EVENT_ID = new QueryParameter("sourceEventId");
    public static final QueryParameter CACHE_TIME_INTERVAL = new QueryParameter("cacheTimeInterval");
    public static final QueryParameter CREATION_TIME_INTERVAL = new QueryParameter("creationTimeInterval");

    private final String key;

    public QueryParameter(String key) {
        this.key = requireNonNull(key);
    }

    public String getKey() {
        return key;
    }

}

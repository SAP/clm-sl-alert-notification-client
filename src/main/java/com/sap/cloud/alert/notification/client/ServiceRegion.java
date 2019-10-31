package com.sap.cloud.alert.notification.client;

import java.net.URI;

import static java.util.Objects.requireNonNull;

public class ServiceRegion {

    public static final ServiceRegion EU1 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.eu10.hana.ondemand.com/");
    public static final ServiceRegion EU2 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.eu10.hana.ondemand.com/");
    public static final ServiceRegion EU3 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.eu10.hana.ondemand.com/");
    public static final ServiceRegion US1 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.us10.hana.ondemand.com/");
    public static final ServiceRegion US2 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.us10.hana.ondemand.com/");
    public static final ServiceRegion US3 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.us10.hana.ondemand.com/");
    public static final ServiceRegion US4 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.us10.hana.ondemand.com/");
    public static final ServiceRegion JP1 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.jp10.hana.ondemand.com/");
    public static final ServiceRegion EU10 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.eu10.hana.ondemand.com/");
    public static final ServiceRegion US10 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.us10.hana.ondemand.com/");
    public static final ServiceRegion JP10 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.jp10.hana.ondemand.com/");

    private final Platform platform;
    private final URI serviceURI;

    public ServiceRegion(Platform platform, String serviceURI) {
        this.platform = requireNonNull(platform);
        this.serviceURI = URI.create(serviceURI);
    }

    public URI getServiceURI() {
        return serviceURI;
    }

    public Platform getPlatform() {
        return platform;
    }
}

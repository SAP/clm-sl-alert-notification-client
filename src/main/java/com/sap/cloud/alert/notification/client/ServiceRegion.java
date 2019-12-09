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

    public static final ServiceRegion NEO_ROT = EU1;
    public static final ServiceRegion NEO_FRANKFURT = EU2;
    public static final ServiceRegion NEO_AMSTERDAM = EU3;
    public static final ServiceRegion NEO_ASHBURN = US1;
    public static final ServiceRegion NEO_CHANDLER = US2;
    public static final ServiceRegion NEO_STERLING = US3;
    public static final ServiceRegion NEO_COLORADO_SPRINGS = US4;
    public static final ServiceRegion NEO_TOKYO = JP1;
    public static final ServiceRegion CF_AWS_FRANKFURT = EU10;
    public static final ServiceRegion CF_AWS_US_EAST = US10;
    public static final ServiceRegion CF_AWS_TOKYO = JP10;

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

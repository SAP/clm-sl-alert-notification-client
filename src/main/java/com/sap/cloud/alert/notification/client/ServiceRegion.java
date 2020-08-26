package com.sap.cloud.alert.notification.client;

import java.net.URI;

import static java.util.Objects.requireNonNull;

public class ServiceRegion {

    public static final ServiceRegion AE1 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.eu10.hana.ondemand.com/");
    public static final ServiceRegion AP1 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.ap10.hana.ondemand.com/");
    public static final ServiceRegion AP2 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.ap10.hana.ondemand.com/");
    public static final ServiceRegion BR1 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.br10.hana.ondemand.com/");
    public static final ServiceRegion CA1 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.ca10.hana.ondemand.com/");
    public static final ServiceRegion CA2 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.ca10.hana.ondemand.com/");
    public static final ServiceRegion CN1 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.eu10.hana.ondemand.com/");
    public static final ServiceRegion RU1 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.eu10.hana.ondemand.com/");
    public static final ServiceRegion SA1 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.eu10.hana.ondemand.com/");
    public static final ServiceRegion EU1 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.eu10.hana.ondemand.com/");
    public static final ServiceRegion EU2 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.eu10.hana.ondemand.com/");
    public static final ServiceRegion EU3 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.eu10.hana.ondemand.com/");
    public static final ServiceRegion US1 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.us10.hana.ondemand.com/");
    public static final ServiceRegion US2 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.us10.hana.ondemand.com/");
    public static final ServiceRegion US3 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.us10.hana.ondemand.com/");
    public static final ServiceRegion US4 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.us10.hana.ondemand.com/");
    public static final ServiceRegion JP1 = new ServiceRegion(Platform.NEO, "https://clm-sl-ans-live-ans-service-api.cfapps.jp10.hana.ondemand.com/");
    public static final ServiceRegion EU10 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.eu10.hana.ondemand.com/");
    public static final ServiceRegion EU20 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.eu20.hana.ondemand.com/");
    public static final ServiceRegion AP10 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.ap10.hana.ondemand.com/");
    public static final ServiceRegion BR10 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.br10.hana.ondemand.com/");
    public static final ServiceRegion CA10 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.ca10.hana.ondemand.com/");
    public static final ServiceRegion AP11 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.ap11.hana.ondemand.com/");
    public static final ServiceRegion US20 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.us20.hana.ondemand.com/");
    public static final ServiceRegion US21 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.us21.hana.ondemand.com/");
    public static final ServiceRegion AP21 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.ap21.hana.ondemand.com/");
    public static final ServiceRegion JP20 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.jp20.hana.ondemand.com/");
    public static final ServiceRegion US10 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.us10.hana.ondemand.com/");
    public static final ServiceRegion JP10 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.jp10.hana.ondemand.com/");
    public static final ServiceRegion US30 = new ServiceRegion(Platform.CF, "https://clm-sl-ans-live-ans-service-api.cfapps.us30.hana.ondemand.com/");

    public static final ServiceRegion NEO_ROT = EU1;
    public static final ServiceRegion NEO_FRANKFURT = EU2;
    public static final ServiceRegion NEO_AMSTERDAM = EU3;
    public static final ServiceRegion NEO_ASHBURN = US1;
    public static final ServiceRegion NEO_CHANDLER = US2;
    public static final ServiceRegion NEO_STERLING = US3;
    public static final ServiceRegion NEO_COLORADO_SPRINGS = US4;
    public static final ServiceRegion NEO_TOKYO = JP1;
    public static final ServiceRegion NEO_DUBAI = AE1;
    public static final ServiceRegion NEO_SYDNEY = AP1;
    public static final ServiceRegion NEO_SYDNEY_DR = AP2;
    public static final ServiceRegion NEO_SAO_PAULO = BR1;
    public static final ServiceRegion NEO_TORONTO = CA1;
    public static final ServiceRegion NEO_TORONTO_DR = CA2;
    public static final ServiceRegion NEO_RIYADH = SA1;
    public static final ServiceRegion NEO_SHANGHAI = CN1;
    public static final ServiceRegion NEO_MOSCOW = RU1;
    public static final ServiceRegion CF_AWS_SYDNEY = AP10;
    public static final ServiceRegion CF_AWS_SINGAPORE = AP11;
    public static final ServiceRegion CF_AWS_SAO_PAULO = BR10;
    public static final ServiceRegion CF_AWS_MONTREAL = CA10;
    public static final ServiceRegion CF_AWS_FRANKFURT = EU10;
    public static final ServiceRegion CF_AWS_TOKYO = JP10;
    public static final ServiceRegion CF_AWS_US_EAST = US10;
    public static final ServiceRegion CF_AZURE_SINGAPORE = AP21;
    public static final ServiceRegion CF_AZURE_NETHERLANDS = EU20;
    public static final ServiceRegion CF_AZURE_TOKYO = JP20;
    public static final ServiceRegion CF_AZURE_WA = US20;
    public static final ServiceRegion CF_AZURE_VA = US21;
    public static final ServiceRegion CF_GCP_IA = US30;

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

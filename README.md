
<img src="https://user-images.githubusercontent.com/11653294/64466233-7cd17480-d119-11e9-8965-e036c1e23c9a.png" alt="CF logo" height="150" align="left"/>

# SAP Alert Notification service for SAP BTP Client
>*Java-based client library to support the usage of Alert Notification service*

[![Documentation](https://img.shields.io/badge/Documentation-@SAP%20Help%20Portal-ff9900.svg)](https://help.sap.com/viewer/product/ALERT_NOTIFICATION/Cloud/en-US)
[![API](https://img.shields.io/badge/APIs-@SAP%20API%20Business%20Hub-f542da.svg)](https://api.sap.com/package/AlertNotification?section=Artifacts)
[![Blog](https://img.shields.io/badge/Blogs-@SAP%20Community%20Portal-3399ff.svg)](https://blogs.sap.com/tag/sap-cloud-platform-alert-notification/)
[![REUSE status](https://api.reuse.software/badge/github.com/SAP/clm-sl-alert-notification-client)](https://api.reuse.software/info/github.com/SAP/clm-sl-alert-notification-client)

### 1. Introduction
Alert Notification service is part of the DevOps portfolio of the SAP Business Technology Platform The service is specialized in instant delivery of events coming straight from the core platform services, e.g.
database or application monitoring tools. This way you're always the first one notified whenever an issue with your dependency occurs. Additionally, Alert Notification service provides means for posting real-time
crucial events directly from your application. All those events altogether - either your custom events, or the platform ones, could be received on whatever channel is preferred - e-mail, Slack, custom webhook.
Furthermore, events can be even stored in Alert Notification service storage and pulled from it later.


This library focuses on two major aspects:
1. Posting of custom events from your application;
2. Pulling of already stored events - either custom, or platform events, and on the other hand, either stored by request, or stored because the requested action has failed for some reason.


In this tutorial, we'll take a deep dive at the library's capabilities.

### 2. The Dependency
As usual, to get started using a new library, we first need to add either the binary itself or just a dependency for it:

```xml
<dependency>
    <groupId>com.sap.cloud.ans</groupId>
    <artifactId>clm-sl-alert-notification-client</artifactId>
    <version>${latest.version}</version>
</dependency>
```

Latest version & more dependency declaration examples see on [Alert Notification service @MVNrepository](https://mvnrepository.com/artifact/com.sap.cloud.ans/clm-sl-alert-notification-client).

### 3. Setting Up the Alert Notification service Client
As a prerequisite of this step, you must already be an Alert Notification service customer and you need credentials for API interactions. If not,
follow [The service enablement guide](https://help.sap.com/viewer/5967a369d4b74f7a9c2b91f5df8e6ab6/Cloud/en-US/812b6e3ed8934648ad15780cd51721ef.html)
and then [Credential Management](https://help.sap.com/viewer/5967a369d4b74f7a9c2b91f5df8e6ab6/Cloud/en-US/80fe24f86bde4e3aac2903ac05511835.html).

Let's start by constructing the needed parameters for the Alert Notification service client:

* HttpClient - used for connection to Alert Notification service. In this tutorial, we'll use the Apache Http default one:

```java
HttpClient httpClient = HttpClients.createDefault();
```

* IRetryPolicy - a simple interface that defines the retry policy on a request to Alert Notification service. In our example, let's use the build-in `SimpleRetryPolicy` implementation:

```java
IRetryPolicy retryPolicy = new SimpleRetryPolicy(5, Duration.ofMillis(100));
```

* ServiceRegion - defines the region where Alert Notification service is instantiated. For ease, all publicly available service regions
of Alert Notification are set as predefined constants and could be used out-of-the-box. In this tutorial, we will use the _cf-eu10_ region:

```java
ServiceRegion serviceRegion = ServiceRegion.EU10;
```

* IAuthorizationHeader - used to authorize in front of Alert Notification service. It could be either BasicAuthorizationHeader or OAuthAuthorizationHeader
depending on the type of credentials created in [Credential Management](https://help.sap.com/viewer/5967a369d4b74f7a9c2b91f5df8e6ab6/Cloud/en-US/80fe24f86bde4e3aac2903ac05511835.html).
In this tutorial, we'll use basic authorization mechanism:

```java
IAuthorizationHeader authorizationHeader = new BasicAuthorizationHeader("<<CLIEND_ID>>", "<<CLIENT_SECRET>>");
```

⚠️ **NOTE: <<CLIEND_ID>> and <<CLIENT_SECRET>> must be replaced by actual values received on credentials creation.**


Now, we are ready to construct the AlertNotificationClient:

```java
IAlertNotificationClient client = new AlertNotificationClient(httpClient, retryPolicy, serviceRegion, authorizationHeader);
```

### 3.1 Setting Up the Alert Notification service Client with OAuth authentication with generated certificate and private key

```java
  IAlertNotificationClient client = new AlertNotificationClientBuilder() //
            .withServiceRegion(<< SAP_SERVICE_REGION >>) //
            .withRetryPolicy(<< RETRY_POLICY >>) // 
            .withAuthentication("<< CERTIFICATE >>", "<< PRIVATE_KEY >>", "<< CLIENT_ID >>", "<< OAUTH_SERVICE_URI >>") //
            .build();
```

### 3.2 Setting Up the Alert Notification service Client with X509 authentication through Destination Service

```java
  IAlertNotificationClient alertNotificationClient = new AlertNotificationClientBuilder(<<HTTP_CLIENT>>)
        .withRetryPolicy(<<RETRY_POLICY>>)
        .buildFromDestinationBinding(new DestinationServiceBinding(
        <<DESTINATION_SERVICE_SERVICE_URI>>,
        <<OAUTH_URI>>,
        <<CLIENT_ID>>,
        <<CLIENT_SECRET>>
        ), <<TEST_DESTINATION_NAME>>);
```


### 3.3 Setting Up the Alert Notification service Client with SAP Alert Notification service for SAP BTP generated certificate authentication
```java
  IAlertNotificationClient client = new AlertNotificationClientBuilder() //
            .withServiceRegion(<< SAP_SERVICE_REGION >>) //
            .withRetryPolicy(<< RETRY_POLICY >>) // 
            .withCertificate("<< CERTIFICATE >>", "<< PRIVATE_KEY >>") //
            .build();
```

### 4. (Optional) Setting Up the Asynchronous Client
The library provides means for async calls to Alert Notification service - AlertNotificationAsyncClient. A couple of additional parameters must be
built before constructing it:

* ExecutorService - we'll use a fixed pool:

```java
ExecutorService executorService = Executors.newFixedThreadPool(10);
```

* ICustomerResourceEventBuffer - an in-memory buffer keeping the events waiting to be posted on Alert Notification service:

```java
ICustomerResourceEventBuffer buffer = new InMemoryCustomerResourceEventBuffer(1000);
```

Now, we're ready to construct the async client itself using the Alert Notification service client we've created on the previous step:

```java
IAlertNotificationAsyncClient asyncClient = new AlertNotificationAsyncClient(executorService, buffer, client);
```

### 5. Post an Event on Alert Notification service
Once we have the Alert Notification service client, we are ready to send events. Along the tutorial, we will use the AlertNotificationClient built in
step 3). However, it can be replaced with the async client we've created in step 4) as well.

Before posting an even we first need to construct it.

Alert Notification event is always related to some resource - application or service:

```java
AffectedCustomerResource resource = new AffectedCustomerResourceBuilder()
                .withName("my-java-application")     // resource name
                .withType("java-app")                // resource type
                .withInstance("v9192c8cba")          // identifier of the particular instance of the resource
                .withTags(Collections.emptyMap())    // additional information in form of key-value pairs
                .build();
);
```

```java
CustomerResourceEvent event = new CustomerResourceEventBuilder()
                .withType("TestEvent")                                      // type of the event
                .withCategory(EventCategory.NOTIFICATION)                   // event category
                .withSeverity(EventSeverity.INFO)                           // event severity
                .withSubject("First Event Posted on Alert Notification")    // subject
                .withBody("This event has test purpose.")                   // body
                .withTags(Collections.singletonMap("my-tag", "test"))       // additional information in form of key-value pairs
                .withAffectedResource(resource)                             // the affected resource
                .build();
);
```

>**NOTE**: Further information on the resource & event properties could be found on [SAP API Business Hub](https://api.sap.com/api/cf_producer_api/resource)

Now, we are ready to send the event:

```java
client.sendEvent(event);
```

### 6. Get Stored Events from Alert Notification service
All events [defined for storage](https://help.sap.com/viewer/5967a369d4b74f7a9c2b91f5df8e6ab6/Cloud/en-US/f7bac80425124baebbfe0ff1d50b2956.html) in your Alert Notification service instance can be pulled by means of the library:

```java
client.getMatchedEvents(Collections.emptyMap());
```

Filtering on pull is also available - we'll create a filter for all events with _INFO_ severity related to _my-java-application_
and  stored in the last two hours:

```java
long currentTimeSeconds = System.currentTimeMillis() / 1000;
long timeTwoHoursAgo = currentTimeSeconds - 7200;

Map<QueryParameter, String> matchedEventsFilter = ImmutableMap.<QueryParameter, String>builder()
        .put(QueryParameter.CACHE_TIME_INTERVAL, String.format("(%s;%s)", timeTwoHoursAgo, currentTimeSeconds))
        .put(QueryParameter.RESOURCE_NAME, "my-java-application")
        .put(QueryParameter.SEVERITY, "INFO")
        .build();

client.getMatchedEvents(matchedEventsFilter);
```

⚠️ **NOTE: Keep in mind that the cache time interval is <u>exclusive</u>.**

Or we can filter one specific event only:

```java
client.getMatchedEvent("<<EVENT_ID>>", Collections.emptyMap());
```

⚠️ **NOTE: The <<EVENT_ID>> placeholder must be replaced with the _**id**_ property returned on post to Alert Notification service.**

### 7. Get Undelivered Events from Alert Notification service
Any event that is matched by at least one subscription and some of the related actions has failed upon execution _(e.g. Webhook is
unavailable)_ can be retrieved.

```java
client.getUndeliveredEvents(Collections.emptyMap());
```

Again, we can filter the pulled events. Let's get only those with _INFO_ severity:

```java
client.getUndeliveredEvents(Collections.singletonMap(QueryParameter.SEVERITY, "INFO"));
```

Often we need to know the reason for the delivery failure. This is possible by adding the special value
**FAILURE_REASON** to the **include** query parameter:

```java
Map<QueryParameter, String> undeliveredEventsFilter = ImmutableMap.<QueryParameter, String>builder()
        .put(QueryParameter.INCLUDE, "FAILURE_REASON")
        .put(QueryParameter.RESOURCE_NAME, "my-java-application")
        .build();

client.getUndeliveredEvents(undeliveredEventsFilter);
```

We can also check the failure deliveries and their reasons for one particular event:

```java
client.getUndeliveredEvent("<<EVENT_ID>>", Collections.singletonMap(QueryParameter.INCLUDE, "FAILURE_REASON"));
```

### 8. Setting Up a Test Environment
Do you want to test yourself all of those examples? Get started by [importing](https://help.sap.com/viewer/5967a369d4b74f7a9c2b91f5df8e6ab6/Cloud/en-US/771da5b383ee4722afc4eb1f58aa4648.html)
the following configuration:

```json
{
    "conditions": [
        {
            "name": "type-TestEvent",
            "propertyKey": "eventType",
            "predicate": "EQUALS",
            "propertyValue": "TestEvent",
            "labels": [],
            "description": "Catches events which type equals 'TestEvent'."
        }
    ],
    "actions": [
        {
            "name": "store-action",
            "state": "ENABLED",
            "labels": [],
            "description": "This action stores the event in Alert Notification storage. Thus, it can be retrieved via Matched Events API later.",
            "type": "STORE"
        },
        {
            "name": "unavailable-webhook",
            "state": "ENABLED",
            "type": "WEB_HOOK",
            "labels": [],
            "description": "This action is used for demonstration of the Undelivered Events API. It attempts to send an event to an unavailable webhook service.",
            "properties": {
                "destination": "https://httpstat.us/503",
                "sslTrustAll": false,
                "payloadTemplate": ""
            }
        }
    ],
    "subscriptions": [
        {
            "name": "TestEvent-store-and-webhook",
            "conditions": [
                "type-TestEvent"
            ],
            "actions": [
                "unavailable-webhook",
                "store-action"
            ],
            "labels": [],
            "state": "ENABLED",
            "description": "All events with type \"TestEvent\" are stored and posted to an unavailable webhook."
        }
    ]
}
```

Once the event from step 5) is posted, there will be one stored event immediately accessible on the Matched Events API. Another
event will be stored as an undelivered event after the webhook [retry policy](https://help.sap.com/viewer/5967a369d4b74f7a9c2b91f5df8e6ab6/Cloud/en-US/086361cb02fb467993acd6f9515607d4.html)
expires. Then it will be available on the Undelivered Events endpoint.

### 9. Setting Up a Test Environment using the Alert Notification service Configuration Client
The configuration can also be managed through the Alert Notification service Configuration client. It can be used to create, read, update and delete actions, conditions and subscriptions.
The required parameters to construct the configuration client are the same as those described in step 3) for the Alert Notification service Client.
```java
IAlertNotificationConfigurationClient configurationClient = new AlertNotificationConfigurationClient(httpClient, retryPolicy, serviceRegion, authorizationHeader);
```
Using the configuration client we can create the configuration programmatically:
```json
configurationClient.createCondition(
    new ConditionBuilder()
        .withName("type-TestEvent")
        .withPropertyKey("eventType")
        .withPredicate(Predicate.EQUALS)
        .withPropertyValue("TestEvent")
        .withMandatory(false)
        .withDescription("Catches events which type equals 'TestEvent'.")
        .build()
);

configurationClient.createAction(
    new ActionBuilder()
        .withName("store-action")
        .withState(State.ENABLED)
        .withType("STORE")
        .withDescription("This action stores the event in Alert Notification service storage. Thus, it can be retrieved via Matched Events API later.")
        .build()
);

configurationClient.createAction(
    new ActionBuilder()
        .withName("unavailable-webhook")
        .withState(State.ENABLED)
        .withType("WEB_HOOK")
        .withDescription("This action is used for demonstration of the Undelivered Events API. It attempts to send an event to an unavailable webhook service.")
        .withProperty("destination","https://httpstat.us/503")
        .withProperty("sslTrustAll","false")
        .build()
);

configurationClient.createSubscription(
    new SubscriptionBuilder()
        .withName("TestEvent-store-and-webhook")
        .withState(State.ENABLED)
        .withDescription("All events with type \"TestEvent\" are stored and posted to an unavailable webhook.")
        .withAction("store-action")
        .withAction("unavailable-webhook")
        .withCondition("type-TestEvent")
        .build()
);
```

### 10. Have an issue?
Please, let us know by filing a [new issue](https://github.com/sap/clm-sl-alert-notification-client/issues/new).

### 11. Contributing
We're always open for improvements! If you think the library could be better, please, open an issue and propose your solution as a pull request. We will contact you for discussion as soon as possible.

### 12. License
This project is run under the licensing terms of Apache License 2.0. The paper could be found in the [LICENSE](https://github.com/sap/clm-sl-alert-notification-client/blob/master/LICENSE) file in the top-level directory. Detailed information including third-party components and their licensing/copyright information is available via [the REUSE tool](https://api.reuse.software/info/github.com/SAP/clm-sl-alert-notification-client).

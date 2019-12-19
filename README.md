
<img src="https://user-images.githubusercontent.com/11653294/64466233-7cd17480-d119-11e9-8965-e036c1e23c9a.png" alt="CF logo" height="100" align="left"/>

# SAP Cloud Platform Alert Notification Java Client
[![Documentation](https://img.shields.io/badge/Documentation-@SAP%20Help%20Portal-ff9900.svg)](https://help.sap.com/viewer/5967a369d4b74f7a9c2b91f5df8e6ab6/Cloud/en-US/b90ed0f3a9604f8e844c73a78d5fad45.html)
[![Blog](https://img.shields.io/badge/Blog-@SAP%20Community-03cdff.svg)](https://blogs.sap.com/tag/sap-cloud-platform-alert-notification/)
[![Video](https://img.shields.io/badge/Video-@SAP%20Videos-8c02db.svg)](https://video.sap.com/embedplaylist/secure/embed/playlistId/1_qo955uqi/v2/0/uiConfId/29075341)

### 1. Introduction
SAP Cloud Platform Alert Notification is part of the DevOps portfolio of the SAP Cloud Platform. The service is specialized in instant delivery of events coming straight from the core platform services, e.g. 
database or application monitoring tools. This way you're always the first one notified whenever an issue with your dependency occurs. Additionally, Alert Notification provides means for posting real-time 
crucial events directly from your application. All those events altogether - either your custom events, or the platform ones, could be received on whatever channel is preferred - e-mail, Slack, custom webhook. 
Furthermore, events can be even stored in Alert Notification storage and pulled from it later. 


This library focuses on two major aspects: 
1. Posting of custom events from your application;
2. Pulling of already stored events - either custom, or platform events, and on the other hand, either stored by request, or stored because the requested action has failed for some reason.   


In this tutorial, we'll take a deep dive at the library's capabilities.

### 2. The Maven Dependency
As usual, to get started using a new library, we first need to add either the binary itself or just a dependency for it:

```xml 
<dependency>
    <groupId>com.sap.cloud</groupId>
    <artifactId>alert-notification-client</artifactId>
    <version>${latest.version}</version>
</dependency>
```

Latest version & more dependency declaration examples see on [SAP Cloud Platform Alert Notification@MVNrepository](https://mvnrepository.com/artifact/com.sap.cloud.ans/clm-sl-alert-notification-client).

### 3. Setting Up the Alert Notification Client
As a prerequisite of this step is already being an Alert Notification customer and also having credentials for API interactions. If not,
follow [The service enablement guide](https://help.sap.com/viewer/5967a369d4b74f7a9c2b91f5df8e6ab6/Cloud/en-US/812b6e3ed8934648ad15780cd51721ef.html) 
and then [Acquiring API access credentials](https://help.sap.com/viewer/5967a369d4b74f7a9c2b91f5df8e6ab6/Cloud/en-US/80fe24f86bde4e3aac2903ac05511835.html).
 
Let's start by constructing the needed parameters for the Alert Notification client:

* HttpClient - used for connection to Alert Notification. In this tutorial, we'll use the Apache Http default one:

```java 
HttpClient httpClient = HttpClients.createDefault();
```

* RetryPolicy - defines the retry policy on a request to Alert Notification: 

```java 
RetryPolicy retryPolicy = new RetryPolicy().withMaxRetries(3).withBackoff(3, 5, TimeUnit.MINUTES);
```

* ServiceRegion - defines the region where Alert Notification is instantiated. For ease, all publicly available service regions 
of Alert Notification are set as predefined constants and could be used out-of-the-box. In this tutorial, we will use the _cf-eu10_ region:

```java 
ServiceRegion serviceRegion = ServiceRegion.EU10;
``` 

* IAuthorizationHeader - used to authorize in front of Alert Notification. It could be either BasicAuthorizationHeader or OAuthAuthorizationHeader 
depending on the type of credentials created in [Acquiring API access credentials](https://help.sap.com/viewer/5967a369d4b74f7a9c2b91f5df8e6ab6/Cloud/en-US/80fe24f86bde4e3aac2903ac05511835.html).
In this tutorial, we'll use basic authorization:

```java 
IAuthorizationHeader authorizationHeader = new BasicAuthorizationHeader("<<CLIEND_ID>>", "<<CLIENT_SECRET>>");
```

⚠️ **NOTE: <<CLIEND_ID>> and <<CLIENT_SECRET>> must be replaced by actual values received on credentials creation.**


Now, we are ready to construct AlertNotificationClient:

```java 
IAlertNotificationClient client = new AlertNotificationClient(httpClient, retryPolicy, serviceRegion, authorizationHeader);
```

### 4. (Optional) Setting Up the Asynchronous Client
The library provides means for async calls to Alert Notification - AlertNotificationAsyncClient. A couple of additional parameters must be 
built before constructing it:

* ExecutorService - we'll use a fixed pool:

```java 
ExecutorService executorService = Executors.newFixedThreadPool(10);
```

* ICustomerResourceEventBuffer - an in-memory buffer keeping the events waiting to be posted on Alert Notification:

```java 
ICustomerResourceEventBuffer buffer = new InMemoryCustomerResourceEventBuffer(1000);
```

Now, we're ready to construct the async client itself using the Alert Notification client we've created on the previous step:

```java 
IAlertNotificationAsyncClient asyncClient = new AlertNotificationAsyncClient(executorService, buffer, client);
```

### 5. Post an Event on Alert Notification
Once we have the Alert Notification client, we are ready to send events. Along the tutorial, we will use the AlertNotificationClient built in 
step 3). However, it can be replaced with the async client we've created in step 4) as well.

Before posting an even we first need to construct it. 

Alert Notification event is always related to some resource - application or service:

```java 
AffectedCustomerResource resource = new AffectedCustomerResource(
        "my-java-application",  // resource name
        "java-app",             // resource type
        "v9192c8cba",           // identifier of the particular instanca of the resource
        Collections.emptyMap()  // additional information in form of key-value pairs
);
``` 

```java 
CustomerResourceEvent event = new CustomerResourceEvent(
        null,                                              // identifier given by Alert Notification on receiving
        "TestEvent",                                       // type of the event
        1567659671L,                                       // timestamp - when the event occured
        EventSeverity.INFO,                                // event severity
        EventCategory.NOTIFICATION,                        // event category
        1,                                                 // event priority
        "First Event Posted on Alert Notification",        // subject
        "This event has test purpose.",                    // body
        Collections.singletonMap("app.status", "started"), // additional information in form of key-value pairs
        resource                                           // the affected resource
);
```

Now, we are ready to send the event:

```java 
client.sendEvent(event);
```

### 6. Get Stored Events from Alert Notification
All events defined for storage in your Alert Notification instance can be pulled by means of the library:

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

⚠️ **NOTE: The <<EVENT_ID>> placeholder must be replaced with the _**id**_ property returned on post to Alert Notification.**

### 7. Get Undelivered Events from Alert Notification
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
            "labels": [],
            "description": "This action is used for demonstration of the Undelivered Events API. It attempts to send an event to an unavailable webhook service.",
            "destination": "https://httpstat.us/503",
            "sslTrustAll": false,
            "payloadTemplate": "",
            "type": "WEB_HOOK"
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
event will be stored as an undelivered event after the webhook [retry policy](https://help.sap.com/viewer/5967a369d4b74f7a9c2b91f5df8e6ab6/Cloud/en-US/da4fd4e6d0f74bd6b0939145d0e6b8f1.html)
expires. Then it will be available on the Undelivered Events endpoint.


### 9. Have an issue?
Please, let us know by filing a [new issue](https://github.com/sap-staging/clm-sl-alert-notification-client/issues/new). 

### 10. Contributing
We're always open for improvements! If you think the library could be better, please, open an issue and propose your solution as a pull request. We will contact you for discussion as soon as possible.

### 11. License
This project is run under the licensing terms of Apache License 2.0. The paper could be found in the [LICENSE](https://github.com/sap-staging/clm-sl-alert-notification-client/blob/master/LICENSE) file 
in the top-level directory. 


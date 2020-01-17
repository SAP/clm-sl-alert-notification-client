package com.sap.cloud.alert.notification.client.internal;

import com.sap.cloud.alert.notification.client.IAlertNotificationClient;
import com.sap.cloud.alert.notification.client.ICustomerResourceEventBuffer;
import com.sap.cloud.alert.notification.client.QueryParameter;
import com.sap.cloud.alert.notification.client.builder.AlertNotificationAsyncClientBuilder;
import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import com.sap.cloud.alert.notification.client.exceptions.ServerResponseException;
import com.sap.cloud.alert.notification.client.model.AffectedCustomerResource;
import com.sap.cloud.alert.notification.client.model.CustomerResourceEvent;
import com.sap.cloud.alert.notification.client.model.PagedResponse;
import com.sap.cloud.alert.notification.client.util.SynchronousExecutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

import static com.sap.cloud.alert.notification.client.QueryParameter.CORRELATION_ID;
import static com.sap.cloud.alert.notification.client.model.EventCategory.NOTIFICATION;
import static com.sap.cloud.alert.notification.client.model.EventSeverity.INFO;
import static com.sap.cloud.alert.notification.client.model.PredefinedEventTag.SOURCE_EVENT_ID;
import static java.lang.Integer.valueOf;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonMap;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.groupingBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlertNotificationAsyncClientTest {

    private static final String TEST_EVENT_ID = "TEST_EVENT_ID";
    private static final Map<QueryParameter, String> TEST_QUERY_PARAMETERS = singletonMap(CORRELATION_ID, "test_correlation_id");


    private PagedResponse testPagedResponse;
    private ExecutorService testExecutorService;
    private CustomerResourceEvent testResourceEvent;
    private AlertNotificationAsyncClient classUnderTest;
    private ICustomerResourceEventBuffer testEventBuffer;
    private IAlertNotificationClient testAlertNotificationClient;

    @BeforeEach
    public void setUp() {
        testPagedResponse = mock(PagedResponse.class);
        testResourceEvent = mock(CustomerResourceEvent.class);
        testExecutorService = spy(new SynchronousExecutorService());
        testAlertNotificationClient = mock(IAlertNotificationClient.class);
        testEventBuffer = spy(new InMemoryCustomerResourceEventBuffer(100));
        classUnderTest = new AlertNotificationAsyncClient(testExecutorService, testEventBuffer, testAlertNotificationClient);
    }

    @Test
    public void givenThatExecutorServiceIsNull_whenConstructingClient_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new AlertNotificationAsyncClient(null, testEventBuffer, testAlertNotificationClient);
        });
    }

    @Test
    public void givenThatEventBufferIsNull_whenConstructingClient_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new AlertNotificationAsyncClient(testExecutorService, null, testAlertNotificationClient);
        });
    }

    @Test
    public void givenThatAlertNotificationClientIsNull_whenConstructingClient_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new AlertNotificationAsyncClient(testExecutorService, testEventBuffer, null);
        });
    }

    @Test
    public void whenGetExecutorServiceIsCalled_thenCorrectResultIsReturned() {
        assertEquals(testExecutorService, classUnderTest.getExecutorService());
    }

    @Test
    public void whenGetOrderedEventExecutorServicesIsCalled_thenCorrectResultIsReturned() {
        assertEquals(0, classUnderTest.getOrderedEventExecutorServices().size());
        assertEquals(3, new AlertNotificationAsyncClient(testExecutorService, testEventBuffer, testAlertNotificationClient, 3)
                .getOrderedEventExecutorServices().size());
    }

    @Test
    public void whenGetEventBufferIsCalled_thenCorrectResultIsReturned() {
        assertEquals(testEventBuffer, classUnderTest.getEventBuffer());
    }

    @Test
    public void whenGetAlertNotificationClientIsCalled_thenCorrectResultIsReturned() {
        assertEquals(testAlertNotificationClient, classUnderTest.getAlertNotificationClient());
    }

    @Test
    public void whenSendEventIsCalled_thenEventIsScheduledForSending() throws Exception {
        doReturn(testResourceEvent).when(testAlertNotificationClient).sendEvent(testResourceEvent);

        assertSame(testResourceEvent, classUnderTest.sendEvent(testResourceEvent).get());

        InOrder executionOrder = inOrder(testEventBuffer, testAlertNotificationClient);
        executionOrder.verify(testEventBuffer).write(testResourceEvent);
        executionOrder.verify(testEventBuffer).read(any(UUID.class));
        executionOrder.verify(testAlertNotificationClient).sendEvent(testResourceEvent);
    }

    @Test
    public void whenGetMatchedEventsIsCalled_thenCorrectRequestIsScheduledForSending() throws Exception {
        doReturn(testPagedResponse).when(testAlertNotificationClient).getMatchedEvents(TEST_QUERY_PARAMETERS);

        assertSame(testPagedResponse, classUnderTest.getMatchedEvents(TEST_QUERY_PARAMETERS).get());

        verify(testAlertNotificationClient).getMatchedEvents(TEST_QUERY_PARAMETERS);
    }

    @Test
    public void whenGetMatchedEventIsCalled_thenCorrectRequestIsScheduledForSending() throws Exception {
        doReturn(testPagedResponse).when(testAlertNotificationClient).getMatchedEvent(TEST_EVENT_ID, TEST_QUERY_PARAMETERS);

        assertSame(testPagedResponse, classUnderTest.getMatchedEvent(TEST_EVENT_ID, TEST_QUERY_PARAMETERS).get());

        verify(testAlertNotificationClient).getMatchedEvent(TEST_EVENT_ID, TEST_QUERY_PARAMETERS);
    }

    @Test
    public void whenGetUndeliveredEventsIsCalled_thenCorrectRequestIsScheduledForSending() throws Exception {
        doReturn(testPagedResponse).when(testAlertNotificationClient).getUndeliveredEvents(TEST_QUERY_PARAMETERS);

        assertSame(testPagedResponse, classUnderTest.getUndeliveredEvents(TEST_QUERY_PARAMETERS).get());

        verify(testAlertNotificationClient).getUndeliveredEvents(TEST_QUERY_PARAMETERS);
    }

    @Test
    public void whenGetUndeliveredEventIsCalled_thenCorrectRequestIsScheduledForSending() throws Exception {
        doReturn(testPagedResponse).when(testAlertNotificationClient).getUndeliveredEvent(TEST_EVENT_ID, TEST_QUERY_PARAMETERS);

        assertSame(testPagedResponse, classUnderTest.getUndeliveredEvent(TEST_EVENT_ID, TEST_QUERY_PARAMETERS).get());

        verify(testAlertNotificationClient).getUndeliveredEvent(TEST_EVENT_ID, TEST_QUERY_PARAMETERS);
    }

    @Test
    public void whenShutdownIsCalled_thenExecutorServiceIsStopped() {
        classUnderTest.shutdown();

        verify(testExecutorService).shutdownNow();
    }

    @Test
    public void givenThatOrderedEventSendersCountIsGreaterThan0_whenSendingOrderedEvents_thenEventOrderIsPreserved() throws Exception {
        final CountDownLatch deliver = new CountDownLatch(100);
        final ArrayBlockingQueue<CustomerResourceEvent> deliveredEvents = new ArrayBlockingQueue<>(100, true);

        IAlertNotificationClient alertNotificationClient = new IAlertNotificationClient() {

            @Override
            public CustomerResourceEvent sendEvent(CustomerResourceEvent event) throws ClientRequestException, ServerResponseException {
                deliveredEvents.offer(event);
                deliver.countDown();
                return event;
            }

            @Override
            public PagedResponse getMatchedEvents(Map<QueryParameter, String> queryParameters)
                    throws ClientRequestException, ServerResponseException {
                return null;
            }

            @Override
            public PagedResponse getMatchedEvent(String eventId, Map<QueryParameter, String> queryParameters)
                    throws ClientRequestException, ServerResponseException {
                return null;
            }

            @Override
            public PagedResponse getUndeliveredEvents(Map<QueryParameter, String> queryParameters)
                    throws ClientRequestException, ServerResponseException {
                return null;
            }

            @Override
            public PagedResponse getUndeliveredEvent(String eventId, Map<QueryParameter, String> queryParameters)
                    throws ClientRequestException, ServerResponseException {
                return null;
            }
        };

        AlertNotificationAsyncClient alertNotificationAsyncClient = new AlertNotificationAsyncClientBuilder(alertNotificationClient)
                .withOrderedEventSendersCount(5).build();

        CustomerResourceEvent template = new CustomerResourceEvent(null, "TEST_TYPE", null, INFO, NOTIFICATION, 1000, "TEST_SUBJECT",
                "TEST_BODY", null, new AffectedCustomerResource("TEST_NAME", "TEST_RESOURCE_TYPE", null, null));

        ExecutorService executor = newFixedThreadPool(6);
        executor.execute(new EventSender(alertNotificationAsyncClient, template, "F98F6A0CF03D4D24B2A4CD65A58996F9", 10));
        executor.execute(new EventSender(alertNotificationAsyncClient, template, "E79A19A110AB4D5E89CD8B9E734207CB", 10));
        executor.execute(new EventSender(alertNotificationAsyncClient, template, "68F049A227E04F86B318F1C10B5FEBBC", 10));
        executor.execute(new EventSender(alertNotificationAsyncClient, template, "3C3027A0EC574AD985712FA909DC881D", 10));
        executor.execute(new EventSender(alertNotificationAsyncClient, template, null, 30)); //not to be ordered
        executor.execute(new EventSender(alertNotificationAsyncClient, template, "", 30)); //not to be ordered

        boolean deliveredInTime = deliver.await(5, SECONDS);
        executor.shutdownNow();
        alertNotificationAsyncClient.shutdown();

        assertEquals(true, deliveredInTime);
        assertEquals(100, deliveredEvents.size());

        Map<String, List<CustomerResourceEvent>> groupedBySourceEventId = stream(deliveredEvents.toArray(new CustomerResourceEvent[100]))
                .collect(groupingBy(event -> event.getId().substring(0, event.getId().indexOf(':'))));
        groupedBySourceEventId.entrySet().stream().forEach(entry -> {
            if ("null".equals(entry.getKey()) || "".equals(entry.getKey())) {
                //not ordered events -> all 30 delivered regardless of order
                assertEquals(30, entry.getValue().size());
            } else {
                //ordered events -> all 10 delivered in the order they were sent
                IntStream.range(0, 10).forEach(index -> assertEquals(index + 1, entry.getValue().get(index).getPriority().intValue()));
            }
        });
    }

    private static class EventSender implements Runnable {

        Map<String, String> tags = new HashMap<>();
        AlertNotificationAsyncClient alertNotificationAsyncClient;
        CustomerResourceEvent template;
        String sourceEventId;
        int eventsCount;

        EventSender(AlertNotificationAsyncClient alertNotificationAsyncClient, CustomerResourceEvent templateEvent, String sourceEventId,
                int eventsCount) {
            this.alertNotificationAsyncClient = alertNotificationAsyncClient;
            this.template = templateEvent;
            this.sourceEventId = sourceEventId;
            this.eventsCount = eventsCount;
            if (sourceEventId != null) {
                tags.put(SOURCE_EVENT_ID, sourceEventId);
            }
        }

        @Override
        public void run() {
            //request sending of eventsCount events one by one with increasing priority, starting from 1 and increased by 1
            IntStream.range(0, eventsCount).forEach(index -> alertNotificationAsyncClient.sendEvent(buildEvent(index)));
        }

        private CustomerResourceEvent buildEvent(int index) {
            return new CustomerResourceEvent(sourceEventId + ":" + index, template.getEventType(), System.currentTimeMillis(),
                    template.getSeverity(), template.getCategory(), valueOf(index + 1), template.getSubject(), template.getBody(), tags,
                    template.getResource());
        }
    }
}

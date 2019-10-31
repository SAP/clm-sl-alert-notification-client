package com.sap.cloud.alert.notification.client;

import com.sap.cloud.alert.notification.client.exceptions.BufferOverflowException;
import com.sap.cloud.alert.notification.client.exceptions.ServerResponseException;
import com.sap.cloud.alert.notification.client.model.CustomerResourceEvent;

import java.util.UUID;

public interface ICustomerResourceEventBuffer {

    /**
     * Returns the buffer capacity
     *
     */
    int getCapacity();

    /**
     * Read an event from the buffer
     *
     * @param eventUuid the event identifier that was given on putting in the queue
     */
    CustomerResourceEvent read(UUID eventUuid);

    /**
     * Put new event in the buffer
     *
     * @param event the event to be stored in the buffer
     * @throws BufferOverflowException if the buffer queue is full
     */
    UUID write(CustomerResourceEvent event) throws BufferOverflowException;
}

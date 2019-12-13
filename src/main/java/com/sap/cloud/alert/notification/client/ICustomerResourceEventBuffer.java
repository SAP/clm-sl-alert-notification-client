package com.sap.cloud.alert.notification.client;

import com.sap.cloud.alert.notification.client.exceptions.BufferOverflowException;
import com.sap.cloud.alert.notification.client.model.CustomerResourceEvent;

import java.util.UUID;

public interface ICustomerResourceEventBuffer {

    /**
     * Return the buffer capacity
     *
     * @return the number of events that could be kept simultaneously in the buffer
     */
    int getCapacity();

    /**
     * Read and remove an event from the buffer
     *
     * @param eventUuid the event identifier that was given on putting in the queue
     * @return the event itself that was read and removed from the buffer
     */
    CustomerResourceEvent read(UUID eventUuid);

    /**
     * Put a new event in the buffer
     *
     * @param event the event to be stored in the buffer
     * @return the unique identifier assigned to the event on putting into the buffer
     * @throws BufferOverflowException if the buffer queue is full
     */
    UUID write(CustomerResourceEvent event) throws BufferOverflowException;
}

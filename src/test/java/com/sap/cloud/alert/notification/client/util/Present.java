package com.sap.cloud.alert.notification.client.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

public class Present<T> implements Future<T> {

    private final T result;
    private final Exception exception;

    public Present(T result, Exception exception) {
        this.result = result;
        this.exception = exception;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public T get() throws ExecutionException {
        if (nonNull(exception)) {
            throw new ExecutionException(exception);
        }

        return result;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws ExecutionException {
        return get();
    }
}

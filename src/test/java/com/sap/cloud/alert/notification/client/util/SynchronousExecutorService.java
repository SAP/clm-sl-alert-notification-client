package com.sap.cloud.alert.notification.client.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class SynchronousExecutorService implements ExecutorService {

    private boolean isStopped;

    public SynchronousExecutorService() {
        this.isStopped = Boolean.FALSE;
    }

    @Override
    public void shutdown() {
        this.isStopped = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown();
        return emptyList();
    }

    @Override
    public boolean isShutdown() {
        return isStopped;
    }

    @Override
    public boolean isTerminated() {
        return isStopped;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) {
        return isStopped;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        assertNotStopped();
        return convertToFuture(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        assertNotStopped();
        return convertToFuture(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return submit(task, null);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) {
        return tasks.stream()
                .map(task -> {
                    assertNotStopped();
                    return convertToFuture(task);
                }).collect(toList());
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
        return invokeAll(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws ExecutionException {
        assertNotStopped();
        return invoke(tasks.iterator().next());
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws ExecutionException {
        return invokeAny(tasks);
    }

    @Override
    public void execute(Runnable command) {
        assertNotStopped();
        command.run();
    }

    private void assertNotStopped() {
        if (isStopped) {
            throw new RejectedExecutionException();
        }
    }

    private static <T> T invoke(Callable<T> callable) throws ExecutionException {
        try {
            return callable.call();
        } catch (Exception exception) {
            throw new ExecutionException(exception);
        }
    }

    private static <T> Future<T> convertToFuture(Runnable runnable, T result) {
        try {
            runnable.run();
            return new Present<>(result, null);
        } catch (Exception exception) {
            return new Present<>(null, exception);
        }
    }

    private static <T> Future<T> convertToFuture(Callable<T> callable) {
        try {
            return new Present<>(callable.call(), null);
        } catch (Exception exception) {
            return new Present<>(null, exception);
        }
    }
}

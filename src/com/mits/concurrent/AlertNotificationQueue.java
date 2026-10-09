package com.mits.concurrent;

import com.mits.model.Alert;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Thread-safe notification queue implementing the classic Producer-Consumer pattern.
 * Demonstrates:
 * - Unit 4: Thread synchronization with wait() and notifyAll()
 * - Unit 4: Shared monitor lock
 */
public class AlertNotificationQueue {
    private final Queue<Alert> queue = new LinkedList<>();
    private final int capacity;

    public AlertNotificationQueue(int capacity) {
        this.capacity = Math.max(10, capacity);
    }

    public AlertNotificationQueue() {
        this(50);
    }

    /**
     * Producer: Enqueues an alert. If queue is full, waits.
     */
    public synchronized void produce(Alert alert) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait(); // Wait for space to become available
        }
        queue.add(alert);
        notifyAll(); // Notify waiting consumers
    }

    /**
     * Non-blocking produce: Adds if space permits.
     */
    public synchronized boolean offer(Alert alert) {
        if (queue.size() >= capacity) {
            queue.poll(); // Evict oldest
        }
        boolean added = queue.offer(alert);
        notifyAll();
        return added;
    }

    /**
     * Consumer: Retrieves and removes an alert. If empty, waits.
     */
    public synchronized Alert consume() throws InterruptedException {
        while (queue.isEmpty()) {
            wait(); // Wait for alerts to arrive
        }
        Alert alert = queue.poll();
        notifyAll(); // Notify waiting producers
        return alert;
    }

    /**
     * Consumer with timeout.
     */
    public synchronized Alert poll(long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (queue.isEmpty()) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) return null;
            wait(remaining);
        }
        Alert alert = queue.poll();
        notifyAll();
        return alert;
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}

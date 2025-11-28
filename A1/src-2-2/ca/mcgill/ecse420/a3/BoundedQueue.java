package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class BoundedQueue<T> {
    private final T[] items;
    private int head = 0;
    private int tail = 0;
    private int count = 0;

    private final Lock enqLock = new ReentrantLock();
    private final Lock deqLock = new ReentrantLock();

    public BoundedQueue(int capacity) {
        items = (T[]) new Object[capacity];
    }

    public boolean enqueue(T x) {
        enqLock.lock();
        try {
            if (count == items.length) {
                return false; // queue full
            }
            items[tail] = x;
            tail = (tail + 1) % items.length;

            // Updating count must be atomic (protect with deqLock or a separate lock)
            synchronized (this) {
                count++;   
            }
            return true;
        } finally {
            enqLock.unlock();
        }
    }

    public T dequeue() {
        deqLock.lock();
        try {
            if (count == 0) {
                return null; // queue empty
            }
            T value = items[head];
            head = (head + 1) % items.length;

            synchronized (this) {
                count--;
            }
            return value;
        } finally {
            deqLock.unlock();
        }
    }
}

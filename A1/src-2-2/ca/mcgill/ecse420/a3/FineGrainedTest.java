package ca.mcgill.ecse420.a3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;

public class FineGrainedTest {

    // *    *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *
    // Note : The class is copied from the textbook, except the contains() method.
    // *    *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   
    static class FineList<T> {
        private class Node {
            T item;
            int key;
            Node next;
            Lock lock = new ReentrantLock();

            Node(T item) {
                this.item = item;
                this.key = item.hashCode();
            }
            
            // Constructor for sentinel nodes
            Node(int key) {
                this.item = null;
                this.key = key;
            }

            void lock() { lock.lock(); }
            void unlock() { lock.unlock(); }
        }

        private Node head;

        public FineList() {
            head = new Node(Integer.MIN_VALUE);
            head.next = new Node(Integer.MAX_VALUE);
        }

        public boolean add(T item) {
            int key = item.hashCode();
            head.lock();
            Node pred = head;
            try {
                Node curr = pred.next;
                curr.lock();
                try {
                    while (curr.key < key) {
                        pred.unlock();
                        pred = curr;
                        curr = curr.next;
                        curr.lock();
                    }
                    if (curr.key == key) {
                        return false;
                    }
                    Node newNode = new Node(item);
                    newNode.next = curr;
                    pred.next = newNode;
                    return true;
                } finally {
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }

        public boolean remove(T item) {
            Node pred = null, curr = null;
            int key = item.hashCode();
            head.lock();
            try {
                pred = head;
                curr = pred.next;
                curr.lock();
                try {
                    while (curr.key < key) {
                        pred.unlock();
                        pred = curr;
                        curr = curr.next;
                        curr.lock();
                    }
                    if (curr.key == key) {
                        pred.next = curr.next;
                        return true;
                    }
                    return false;
                } finally {
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }

        // QUESTION 2.1: The contains() method
        public boolean contains(T item) {
            int key = item.hashCode();
            head.lock();
            Node pred = head;
            try {
                Node curr = pred.next;
                curr.lock();
                try {
                    while (curr.key < key) {
                        pred.unlock();
                        pred = curr;
                        curr = curr.next;
                        curr.lock();
                    }
                    // If we found the key, return true
                    return curr.key == key;
                } finally {
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }
    }

    // QUESTION 2.2: Testing
    public static void main(String[] args) throws InterruptedException {

        System.out.println("--- FineGrainedList Concurrency Test ---");

        // Initialize test results
        AtomicBoolean test1 = new AtomicBoolean(true);
        AtomicBoolean test2 = new AtomicBoolean(true);
        
        // Setup: Pre-populate a list with numbers 0 to 99
        final FineList<Integer> list = new FineList<>();
        for (int i = 0; i < 100; i++) { list.add(i); }

        // Make a thread pool
        ExecutorService service = Executors.newFixedThreadPool(4);

        // Test 1: Ensure all items added are "contained" in the list
        service.submit(() -> {
            for (int i = 0; i < 100; i++) {
                if (!list.contains(i)) {
                    System.out.println("Error: contains() returned false for present key " + i);
                    test1.set(false);
                }
            }
        });

        // Test 2: Concurrent Add and Remove
        service.submit(() -> {
            list.add(150); // Add a new key: 150
            list.remove(50); // Remove an old key: 50
        });
        // Stop threads
        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);
        //Check
        if (!list.contains(150)) { System.out.println("Error: Key 150 missing after concurrent add"); test2.set(false) ; }
        if (list.contains(50)) { System.out.println("Error: Key 50 still present after concurrent remove"); test2.set(false); }

        if (test1.get() && test2.get()) { System.out.println("All tests passed succesfully"); }
    }
}

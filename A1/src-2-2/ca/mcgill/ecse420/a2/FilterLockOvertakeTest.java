package ca.mcgill.ecse420.a2;

public class FilterLockOvertakeTest {
    public static void main(String[] args) throws InterruptedException {
        int n = 8;
        FilterLock lock = new FilterLock(n);
        int[] entryCount = new int[n]; // how many times each thread entered CS
        boolean[] running = {true};

        Thread[] threads = new Thread[n];
        for (int i = 0; i < n; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                while (running[0]) {
                    lock.lock(id);
                    // Critical Section
                    entryCount[id]++;
                    try { Thread.sleep(1); } catch (InterruptedException e) {}
                    lock.unlock(id);
                }
            });
        }

        // Start all threads
        for (Thread t : threads) t.start();

        // Run for 5 seconds
        Thread.sleep(5000);
        running[0] = false;

        // Wait for all threads to finish
        for (Thread t : threads) t.join();

        // Print how many times each thread entered the CS
        System.out.println("=== Filter Lock Overtaking Test (n=8) ===");
        for (int i = 0; i < n; i++) {
            System.out.printf("Thread %d entered CS %d times%n", i, entryCount[i]);
        }
    }
}

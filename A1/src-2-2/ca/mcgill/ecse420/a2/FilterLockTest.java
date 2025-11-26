public class FilterLockTest {
    public static void main(String[] args) throws InterruptedException {
        for (int n = 2; n <= 8; n++) {
            System.out.println("\nTesting FilterLock with " + n + " threads:");
            testFilterLock(n);
        }
    }

    public static void testFilterLock(int n) throws InterruptedException {
        FilterLock lock = new FilterLock(n);

        // Shared variables for verification
        final int[] inCS = {0};
        final boolean[] error = {false};

        Thread[] threads = new Thread[n];

        for (int i = 0; i < n; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                for (int k = 0; k < 5; k++) { // each thread tries multiple times
                    lock.lock(id);

                    // Begin critical section
                    inCS[0]++;
                    if (inCS[0] > 1) {
                        System.out.println("❌ Mutual exclusion violated by thread " + id);
                        error[0] = true;
                    }

                    try {
                        Thread.sleep(10); // simulate work
                    } catch (InterruptedException e) { }

                    inCS[0]--;
                    // End critical section

                    lock.unlock(id);
                }
            });
        }

        // Start and wait for all threads
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        if (error[0]) {
            System.out.println("❌ FAILED: Mutual exclusion violated.");
        } else {
            System.out.println("✅ PASSED: Mutual exclusion held for " + n + " threads.");
        }
    }
}

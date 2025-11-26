public class BakeryLockTest {

    private static volatile int insideCS = 0;         // shared counter
    private static volatile boolean violationDetected; // flag for violations

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Bakery Lock Mutual Exclusion Test (no AtomicInteger) ===");
        for (int n = 2; n <= 8; n++) {
            testLockWithNThreads(n);            

        }
    }

    private static void testLockWithNThreads(int n) throws InterruptedException {
        BakeryLock lock = new BakeryLock(n);
        Thread[] threads = new Thread[n];
        violationDetected = false;
        insideCS = 0;

        for (int i = 0; i < n; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                for (int k = 0; k < 1000; k++) {
                    lock.lock(id);

                    // Begin critical section
                    insideCS++;
                    if (insideCS > 1) {
                        violationDetected = true;
                        System.err.println("⚠️  Mutual exclusion violated for thread " + id + " with n = " + n);
                    }

                    try {
                        Thread.sleep(1); // simulate work in CS
                    } catch (InterruptedException ignored) {}

                    insideCS--;
                    // End critical section

                    lock.unlock(id);
                }
            });
            threads[i].start();
        }

        // Wait for all threads
        for (Thread t : threads) t.join();

        if (violationDetected) {
            System.out.println("❌ Mutual exclusion FAILED for n = " + n);
        } else {
            System.out.println("✅ Mutual exclusion PASSED for n = " + n);
        }
    }
}

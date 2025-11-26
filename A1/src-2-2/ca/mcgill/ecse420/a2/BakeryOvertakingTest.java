package ca.mcgill.ecse420.a2;

public class BakeryOvertakingTest {

    private static final int N = 8;           // number of threads
    private static final int ITER = 1000;     // iterations per thread
    private static BakeryLock lock = new BakeryLock(N);

    private static int[] enterCount = new int[N];  // times each thread entered CS

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[N];

        for (int i = 0; i < N; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                for (int k = 0; k < ITER; k++) {
                    lock.lock(id);
                    
                    // increment per-thread counter
                    enterCount[id]++;
                    
                    // optional: tiny delay to make CS observable
                    try { Thread.sleep(1); } catch (InterruptedException ignored) {}

                    lock.unlock(id);
                }
            });
            threads[i].start();
        }

        // wait for all threads
        for (Thread t : threads) t.join();

        // print results
        System.out.println("=== Bakery Lock Overtaking Test (n=" + N + ") ===");
        for (int i = 0; i < N; i++) {
            System.out.println("Thread " + i + " entered CS " + enterCount[i] + " times");
        }
    }
}


public class FilterExample {
    static FilterLock lock = new FilterLock(3);

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            int id = i;
            new Thread(() -> {
                for (int k = 0; k < 3; k++) { // each thread tries 3 times
                    lock.lock(id);
                    System.out.println("Thread " + id + " in critical section");
                    try { Thread.sleep(200); } catch (InterruptedException e) {}
                    lock.unlock(id);
                }
            }).start();
        }
    }
}
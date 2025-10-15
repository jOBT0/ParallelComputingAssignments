public class BakeryLock {
    private final int n;              // number of threads
    private final boolean[] flag;     // flag[i] = true means thread i wants to enter CS
    private final int[] number;       // ticket number for each thread

    public BakeryLock(int n) {
        this.n = n;
        this.flag = new boolean[n];
        this.number = new int[n];
    }

    public void lock(int i) {
        flag[i] = true;
        number[i] = 1 + max(number);  // take a number greater than all current numbers

        for (int j = 0; j < n; j++) {
            if (j == i) continue;
            
            // Wait while thread j is interested and has priority
            while (flag[j] && (
                    (number[j] < number[i]) ||
                    (number[j] == number[i] && j < i)
            )) {
            	
            	Thread.yield(); // allow other threads to run
            }
        }
    }

    public void unlock(int i) {
        flag[i] = false;   // done — no longer interested in entering CS
    }

    private int max(int[] arr) {
        int m = 0;
        for (int x : arr) if (x > m) m = x;
        return m;
    }
}

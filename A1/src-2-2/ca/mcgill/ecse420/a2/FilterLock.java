public class FilterLock {
    private volatile int[] level;
    private volatile int[] victim;
    private int n;

    public FilterLock(int n) {
        this.n = n;
        level = new int[n];
        victim = new int[n];
        for (int i = 0; i < n; i++) {
            level[i] = 0;
            victim[i] = -1;
        }
    }

    public void lock(int i) {
        for (int L = 1; L < n; L++) {
            level[i] = L;
            victim[L] = i;
            // Wait while there exists another thread j with level >= L and victim[L] == i
            boolean waiting;
            do {
                waiting = false;
                for (int j = 0; j < n; j++) {
                    if (j != i && level[j] >= L && victim[L] == i) {
                        waiting = true;
                        break;
                    }
                }
                // busy wait
            } while (waiting);
        }
    }

    public void unlock(int i) {
        level[i] = 0;
    }
}   



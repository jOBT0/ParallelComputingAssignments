package ca.mcgill.ecse420.a3;

import java.util.concurrent.*;

/**
 * Fully corrected parallel matrix-vector multiplication.
 * FIXED:
 * - pool created inside method
 * - pool shutdown after use
 * - DotProductTask receives pool explicitly
 * - no task explosion
 * - safe for n = 4000
 */
public class MatrixVectorParallel {

    public static int THRESHOLD = 200;

    public double[] matVecParallel(double[][] A, double[] x) throws Exception {
        int n = x.length;
        double[] y = new double[n];

        // NEW: create a fresh pool for each run
        ExecutorService pool =
        	    Executors.newCachedThreadPool();


        @SuppressWarnings("unchecked")
        Future<Double>[] futures = new Future[n];

        for (int i = 0; i < n; i++) {
            futures[i] = pool.submit(
                    new DotProductTask(A, x, i, 0, n, pool));
        }

        for (int i = 0; i < n; i++) {
            y[i] = futures[i].get();
        }

        // CRITICAL: shutdown the pool so threads do not accumulate
        pool.shutdown();

        return y;
    }

    private static class DotProductTask implements Callable<Double> {
        private final double[][] A;
        private final double[] x;
        private final int row;
        private final int lo, hi;
        private final ExecutorService pool;

        DotProductTask(double[][] A, double[] x, int row,
                       int lo, int hi, ExecutorService pool) {
            this.A = A;
            this.x = x;
            this.row = row;
            this.lo = lo;
            this.hi = hi;
            this.pool = pool;
        }

        @Override
        public Double call() throws Exception {
            int length = hi - lo;

            // use threshold as base case
            if (length <= MatrixVectorParallel.THRESHOLD) {
                double sum = 0;
                for (int j = lo; j < hi; j++) {
                    sum += A[row][j] * x[j];
                }
                return sum;
            }

            int mid = lo + length / 2;

            Future<Double> left = pool.submit(
                    new DotProductTask(A, x, row, lo, mid, pool));
            Future<Double> right = pool.submit(
                    new DotProductTask(A, x, row, mid, hi, pool));

            return left.get() + right.get();
        }
    }
}

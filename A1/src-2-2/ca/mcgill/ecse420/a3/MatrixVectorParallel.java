package ca.mcgill.ecse420.a3;
import java.util.concurrent.*;


/**
 * Highly-parallel matrix-vector multiplication achieving:
 * Work = Θ(n^2)
 * Critical Path = Θ(log n)
 *
 * Uses divide-and-conquer dot-product per row, implemented via a Future-based
 * binary recursion, following the matrix-multiplication structure in Chapter 16.
 */
public class MatrixVectorParallel {

    private final ExecutorService pool = Executors.newCachedThreadPool();

    /**
     * Public API: multiply matrix A by vector x in parallel.
     */
    public double[] matVecParallel(double[][] A, double[] x) throws Exception {
        int n = x.length;
        double[] y = new double[n];

        // Spawn one dot-product task per row
        Future<Double>[] rowFutures = new Future[n];
        for (int i = 0; i < n; i++) {
            rowFutures[i] = pool.submit(new DotProductTask(A, x, i, 0, n));
        }

        // Join all row results
        for (int i = 0; i < n; i++) {
            y[i] = rowFutures[i].get();
        }

        return y;
    }

    /**
     * Divide-and-conquer dot product for a single row i over range [lo, hi).
     * Achieves span Θ(log n) due to binary splitting and parallel subtask creation.
     */
    private class DotProductTask implements Callable<Double> {
        private final double[][] A;
        private final double[] x;
        private final int row;
        private final int lo, hi;
        private static final int THRESHOLD = 1000;   // ← experiment with 50, 100, 250, 500, 1000

        

        DotProductTask(double[][] A, double[] x, int row, int lo, int hi) {
            this.A = A;
            this.x = x;
            this.row = row;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        public Double call() throws Exception {
            int length = hi - lo;

            // ---------------------------------------
            // 4.3: THRESHOLD TO REDUCE NUMBER OF TASKS
            // ---------------------------------------
            if (length <= THRESHOLD) {
                double sum = 0.0;
                for (int j = lo; j < hi; j++) {
                    sum += A[row][j] * x[j];
                }
                return sum;
            }

            // Otherwise, split into two parallel subtasks
            int mid = lo + length / 2;

            Future<Double> leftFuture = pool.submit(
                new DotProductTask(A, x, row, lo, mid)
            );
            Future<Double> rightFuture = pool.submit(
                new DotProductTask(A, x, row, mid, hi)
            );

            double left = leftFuture.get();
            double right = rightFuture.get();

            return left + right;
        }
    }

    /**
     * OPTIONAL TESTER (good for validating correctness with your sequential version).
     */
    public static void main(String[] args) throws Exception {
        MatrixVectorParallel mvp = new MatrixVectorParallel();

        double[][] A = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        double[] x = {1, 1, 1};

        double[] result = mvp.matVecParallel(A, x);

        System.out.println("Parallel result vector y:");
        for (double v : result) {
            System.out.print(v + " ");
        }
        System.out.println();

        // Recommended: compare to sequential version
        double[] seq = MatrixVectorSequential.matVecSequential(A, x);

        System.out.println("Matches sequential output? " +
                java.util.Arrays.equals(result, seq));
    }
}
package ca.mcgill.ecse420.a3;

import java.util.Random;

public class MatrixVectorTest {

    public static void main(String[] args) throws Exception {
        // Required size: 4000 × 4000
        final int n = 4000;

        System.out.println("Available processors: " +
                Runtime.getRuntime().availableProcessors());

        double[][] A = generateMatrix(n);
        double[] x = generateVector(n);

        // ---- Run Sequential Version ----
        long startSeq = System.currentTimeMillis();
        double[] ySeq = MatrixVectorSequential.matVecSequential(A, x);
        double seqChecksum = checksum(ySeq);         
        System.out.println("Sequential checksum: " + seqChecksum);
   
        long endSeq = System.currentTimeMillis();
        long seqTime = endSeq - startSeq;

        System.out.println("\nSequential time = " + seqTime + " ms");

        // ---- Run Parallel Version for Many Thresholds ----
        int[] thresholds = {100, 200, 300, 500, 1000};

        System.out.println("\nParallel Execution (varying thresholds):");
        System.out.println("----------------------------------------");

        for (int t : thresholds) {
            MatrixVectorParallel.THRESHOLD = t; // dynamically modify threshold

            MatrixVectorParallel mvp = new MatrixVectorParallel();
            long startPar = System.currentTimeMillis();
            double[] yPar = mvp.matVecParallel(A, x);
            long endPar = System.currentTimeMillis();
            long parTime = endPar - startPar;

            // Prevent JVM from optimizing away computations:
            double checksum = checksum(yPar);

            double speedup = (double) seqTime / parTime;
            
            System.out.printf("Threshold %4d --> Parallel time = %6d ms | Speedup = %.2f | Checksum = %.3f%n",
                    t, parTime, speedup, checksum);
        }
    }

    // ---------------- Helper functions ----------------

    private static double[][] generateMatrix(int n) {
        Random rand = new Random();
        double[][] A = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = rand.nextDouble();
            }
        }
        return A;
    }

    private static double[] generateVector(int n) {
        Random rand = new Random();
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = rand.nextDouble();
        }
        return x;
    }

    // Simple checksum to prevent dead-code elimination by JVM
    private static double checksum(double[] v) {
        double s = 0;
        for (double d : v) s += d;
        return s;
    }
}


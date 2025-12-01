package ca.mcgill.ecse420.a3;

import java.util.Random;

public class MatrixVectorTest {

    public static void main(String[] args) throws Exception {
        final int n = 4000;
        double[][] A = generateMatrix(n);
        double[] x = generateVector(n);

        // Sequential test
        long startSeq = System.currentTimeMillis();
        double[] ySeq = MatrixVectorSequential.matVecSequential(A, x);
        long endSeq = System.currentTimeMillis();
        long seqTime = endSeq - startSeq;

        // Parallel test (baseline)
        MatrixVectorParallel mvp = new MatrixVectorParallel();
        long startPar = System.currentTimeMillis();
        double[] yPar = mvp.matVecParallel(A, x);
        long endPar = System.currentTimeMillis();
        long parTime = endPar - startPar;

        long parTimeMs = parTime;
        long seqTimeMs = seqTime;

        System.out.println("Sequential time: " + seqTimeMs + " ms");
        System.out.println("Parallel time:   " + parTimeMs + " ms");
        System.out.println("Speedup = " + (double)seqTimeMs / parTimeMs);
    }

    private static double[][] generateMatrix(int n) {
        Random rand = new Random();
        double[][] A = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = rand.nextDouble(); // any double
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
}

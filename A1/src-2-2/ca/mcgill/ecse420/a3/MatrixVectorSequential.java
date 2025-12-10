package ca.mcgill.ecse420.a3;

public class MatrixVectorSequential {
	 /**
     * Sequential matrix-vector multiplication.
     * Computes y = A * x for an n × n matrix A and vector x of length n.
     */
    public static double[] matVecSequential(double[][] A, double[] x) {
        int n = x.length;
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {          // iterate over rows
            double sum = 0.0;
            for (int j = 0; j < n; j++) {      // compute dot product of row i with x
                sum += A[i][j] * x[j];
            }
            y[i] = sum;
        }
        return y;
    }

    /**
     * OPTIONAL: small tester to validate correctness.
     * You are allowed to remove this method if the assignment requires only the implementation.
     */
    
    public static void main(String[] args) {
        double[][] A = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        double[] x = {1, 1, 1};

        double[] y = matVecSequential(A, x);

        System.out.println("Result vector y:");
        for (double v : y) {
            System.out.print(v + " ");
        }
        System.out.println();
    }


}

package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.FileWriter;
import java.io.IOException;

public class MatrixMultiplication {
	
	private static int NUMBER_THREADS = 1;
	private static final int MATRIX_SIZE = 1000;

        public static void main(String[] args) {
		
        	double[][] aTest = {
        		    {1, 2},
        		    {3, 4}
        		};

        		double[][] bTest = {
        		    {5, 6},
        		    {7, 8}
        		};

        		double[][] resultTest = sequentialMultiplyMatrix(aTest, bTest);

        		// Print input and output
        		System.out.println("Matrix A:");
        		printMatrix(aTest);
        		System.out.println("Matrix B:");
        		printMatrix(bTest);
        		System.out.println("Result (A x B):");
        		printMatrix(resultTest);
        		// --- End of test ---
		// Generate two random matrices, same size
		double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		int optimalThreads = 8;
		//sequentialMultiplyMatrix(a, b);
		//parallelMultiplyMatrix(a, b);	
		benchmarkMatrixMultiplication(a,b);
		benchmarkVaryingMatrixSizes(optimalThreads);
		// --- Small 2x2 test case for validation output ---
		

		
	}
        
        private static void printMatrix(double[][] matrix) {
            for (double[] row : matrix) {
                for (double val : row) {
                    System.out.printf("%6.1f ", val);
                }
                System.out.println();
            }
        }
        
	
	/**
	 * Returns the result of a sequential matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * */
	public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {
		int rowsA = a.length;
		int colsA = a[0].length;
		int rowsB = b.length;
		int colsB = b[0].length;
		
		//check if multiplication is possible
		if (colsA != rowsB) {
			throw new IllegalArgumentException("Matrix A's columns must match Matrix B's rows.");
		}
		//Result matrix
		double[][] result = new double[rowsA][colsB];
		
		//Perform sequential matrix multiplication
		for (int i=0; i < rowsA; i++) {
			for (int j = 0; j < colsB; j++) {
				double sum = 0;
				for (int k = 0; k < colsA; k++) {
					sum += a[i][k] * b[k][j];
				}
				result[i][j] = sum;
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the result of a concurrent matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplicationwha
	 * */
    public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
    	int rowsA = a.length;
    	int colsA = a[0].length;
    	int colsB = b[0].length;
    	
    	double[][] result = new double[rowsA][colsB];
    	ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

    	// Submit one task per row
        for (int i = 0; i < rowsA; i++) {
            final int row = i;
            executor.submit(() -> {
                for (int j = 0; j < colsB; j++) {
                    double sum = 0;
                    for (int k = 0; k < colsA; k++) {
                        sum += a[row][k] * b[k][j];
                    }
                    result[row][j] = sum;
                }
            });
        }
    	
        // Shutdown executor and wait for all tasks to finish
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new RuntimeException("Matrix multiplication interrupted", e);
        }

        return result;
		
	}
    
    public static void benchmarkMatrixMultiplication(double[][] a, double[][] b) {
        // Time the sequential version once for baseline
        long startSeq = System.nanoTime();
        sequentialMultiplyMatrix(a, b);
        long endSeq = System.nanoTime();
        double sequentialTime = (endSeq - startSeq) / 1_000_000_000.0; // seconds
        System.out.printf("Sequential Time: %.4f seconds%n", sequentialTime);

        // Create a CSV file
        try (FileWriter csvWriter = new FileWriter("speedup_results.csv")) {
            csvWriter.append("Threads,ParallelTime,Speedup\n");

            // Test different thread counts
            for (int threads = 1; threads <= 25; threads++) {
                setNumberThreads(threads);

                long start = System.nanoTime();
                parallelMultiplyMatrix(a, b);
                long end = System.nanoTime();

                double parallelTime = (end - start) / 1_000_000_000.0;
                double speedup = sequentialTime / parallelTime;

                System.out.printf("Threads: %2d | Time: %.3f s | Speedup: %.2fx%n", threads, parallelTime, speedup);
                
                // Write to CSV
                csvWriter.append(String.format("%d,%.4f,%.4f\n", threads, parallelTime, speedup));
            }

            System.out.println("Speedup data written to speedup_results.csv");

        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }
    
    public static void benchmarkVaryingMatrixSizes(int optimalThreads) {
        int[] sizes = {100, 200, 500, 1000, 2000, 3000, 4000};

        try (FileWriter writer = new FileWriter("matrix_size_vs_time.csv")) {
            writer.write("MatrixSize,SequentialTime,ParallelTime\n");

            for (int size : sizes) {
                System.out.printf("Testing matrix size: %dx%d\n", size, size);

                double[][] a = generateRandomMatrix(size, size);
                double[][] b = generateRandomMatrix(size, size);

                // Sequential timing
                long startSeq = System.nanoTime();
                sequentialMultiplyMatrix(a, b);
                long endSeq = System.nanoTime();
                double sequentialTime = (endSeq - startSeq) / 1_000_000_000.0;

                // Parallel timing with optimalThreads
                setNumberThreads(optimalThreads);
                long startPar = System.nanoTime();
                parallelMultiplyMatrix(a, b);
                long endPar = System.nanoTime();
                double parallelTime = (endPar - startPar) / 1_000_000_000.0;

                writer.write(String.format("%d,%.4f,%.4f\n", size, sequentialTime, parallelTime));
                System.out.printf("Size: %d | Sequential: %.4f s | Parallel (%d threads): %.4f s\n",
                                  size, sequentialTime, optimalThreads, parallelTime);
            }

            System.out.println("Benchmark complete. Results saved to matrix_size_vs_time.csv.");

        } catch (IOException e) {
            System.err.println("Error writing results: " + e.getMessage());
        }
    }


        
        /**
         * Populates a matrix of given size with randomly generated integers between 0-10.
         * @param numRows number of rows
         * @param numCols number of cols
         * @return matrix
         */
        private static double[][] generateRandomMatrix (int numRows, int numCols) {
             double matrix[][] = new double[numRows][numCols];
        for (int row = 0 ; row < numRows ; row++ ) {
            for (int col = 0 ; col < numCols ; col++ ) {
                matrix[row][col] = (double) ((int) (Math.random() * 10.0));
            }
        }
        return matrix;
    }
        
        public static void setNumberThreads(int threads) {
            NUMBER_THREADS = threads;
        }
	
}

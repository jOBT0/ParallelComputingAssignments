package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixMultiplication {
	
	private static final int NUMBER_THREADS = 1;
	private static final int MATRIX_SIZE = 2000;

        public static void main(String[] args) {
		
		// Generate two random matrices, same size
		double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		sequentialMultiplyMatrix(a, b);
		parallelMultiplyMatrix(a, b);	
		
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
			throw new IllegalArgumentExcpetion("Matrix A's columns must match Matrix B's rows.");
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
    	Thread[i] rowResults = new Thread[rowsA];
    	
    	for (int i = 0; i < colsB; i++) {
    		final int row = i;
    		rowResults[i] = new Thread(() -> {
    			for (int j =0; j < colsB; j++) {
    				double sum = 0;
    				for (int k = 0; k < colsA; k++) {
    					sum += a[row][k] * b[k][j]; 
    				}
    				result[row][j] = sum;
    			}
    		});
    		rowResults[i].start();
    	}
    	
    	for (Thread rowResult : rowResults) {
    		rowResult.join();  //wait for all threads to finish
    	}
    	
    	return result;
		
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
	
}

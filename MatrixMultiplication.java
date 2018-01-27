 /**
 * CS 331
 * Professor: Tannaz Rezaei
 *
 * Project #1 - Task #2
 * 
 * Description: program both classical and strassens matrix multiplication. test nxn matrices, 
 * sizes n=2,4,8,16,32,64,128,... 
 *
 * @author - Eric Schenck
 * last modified: january 26, 2018
 *   
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MatrixMultiplication {

	private static long tenMinutes = 1000*60*10;								// ms -> 10 mins
	public static boolean runTimeUnderTenMins = true;							// setting boolean true initialy
	
	
	
	
	public static int[][] classicMatrixMult(int[][] matrixA , int[][] matrixB, int n){
		
		int[][] result = new int[n][n];											//  used to store and return result matrix
		
		for( int i = 0; i < n ; ++i){											// i,j, and k needed
			for(int j = 0; j < n ; ++j){
				result[i][j] = 0;												// initially set each result location to 0
				
				for(int k = 0; k < n; ++k){
					result[i][j] += ( matrixA[i][k] * matrixB[k][j] );			// performing multiplication in proper sequence
				}
			}
		}
		
		return result;															// returning result
	}
	
	
	
	public static int[][] strassenMatrixMult(int[][] matrixA, int[][] matrixB){
		
		int n = matrixA.length;													// both matrices will be same size n x n
		
		int[][] resultMatrix = new int[n][n];									// used to store result of operation
		
		if( n == 1){															// Base Case to end recursion
			resultMatrix[0][0] = matrixA[0][0] * matrixB[0][0];					// multiplication 
		}
		else{
			
			int[][] A11 = new int[n/2][n/2];									// creating new matrixA topleft 
			int[][] A12 = new int[n/2][n/2];									// creating new matrixA topright
			int[][] A21 = new int[n/2][n/2];									// creating new matrixA bottomleft
			int[][] A22 = new int[n/2][n/2];									// creating new matrixA bottomright
			int[][] B11 = new int[n/2][n/2];									// creating new matrixB topleft
			int[][] B12 = new int[n/2][n/2];									// creating new matrixB topright
			int[][] B21 = new int[n/2][n/2];									// creating new matrixB bottomleft
			int[][]	B22	= new int[n/2][n/2];									// creating new matrixB bottomright
			
			split(matrixA, A11, 0, 0);											// Dividing matrix A into 4 halves
			split(matrixA, A12, 0, n/2);
			split(matrixA, A21, n/2, 0);
			split(matrixA, A22, n/2, n/2);
			
			split(matrixB, B11, 0, 0);											// Dividing matrixB into 4 halves
			split(matrixB, B12, 0, n/2);
			split(matrixB, B21, n/2, 0);
			split(matrixB, B22, n/2, n/2);
			
			int[][] P1 = strassenMatrixMult(add(A11,A22), add(B11,B22));			// P1 = (A11+A22)*(B11+B22)
			int[][] P2 = strassenMatrixMult(add(A21,A22), B11);						// P2 = (A21+A22)*B11
			int[][] P3 = strassenMatrixMult(A11, sub(B12, B22));					// P3 = A11 * (B12 - B22)
			int[][] P4 = strassenMatrixMult(A22, sub(B21, B11));					// P4 = A22 * (B21 - B11)
			int[][] P5 = strassenMatrixMult(add(A11, A12), B22);					// P5 = (A11 + A12) * B22
			int[][] P6 = strassenMatrixMult(sub(A21, A11), add(B11, B12));			// P6 = (A21 - A11) * (B11 + B12)
			int[][] P7 = strassenMatrixMult(sub(A12, A22), add(B21, B22));			// P7 = (A12 - A22)*(B21 + B22)
			
			int[][] C11 = add(sub(add(P1, P4), P5), P7);							// C11 = P1 + P4 + P7 - P5 
			int[][] C12 = add(P3, P5);												// C12 = P3 + P5
			int[][] C21 = add(P2, P4);												// C21 = P2 + P4
			int[][] C22 = add(sub(add(P1,P3), P2), P6);								// C22 = P1 - P2 + P3 + P6
			
			join(C11, resultMatrix, 0, 0);											// join 4 halves into result matrix
			join(C12, resultMatrix, 0, n/2);							
			join(C21, resultMatrix, n/2, 0);
			join(C22, resultMatrix, n/2, n/2);								
			
		}
		
		return resultMatrix;														// return result
		
	}
	
	/**
	 * function to subtract two matrices
	 * @param matrixA
	 * @param matrixB
	 * @return
	 */
	public static int[][] sub(int[][] matrixA, int[][] matrixB){
		
		int n = matrixA.length;														// getting length of n
		
		int[][] resultMatrix = new int[n][n];										// creating matrix to store result
		
		for(int i = 0; i < n; ++i){													// for loops to traverse through matrix 
			for(int j = 0; j < n; ++j){														
				resultMatrix[i][j] = matrixA[i][j] - matrixB[i][j];					// performing subtraction
			}
		}
		return resultMatrix;														// returning result
		
	}
	
	
	/**
	 * function to add two matrices
	 * @param matrixA
	 * @param matrixB
	 * @return
	 */
	public static int[][] add(int[][] matrixA, int[][] matrixB){
		
		int n = matrixA.length;														// getting length of n
		
		int[][] resultMatrix = new int[n][n];										// creating matrix to store result
		
		for(int i = 0; i < n; ++i){
			for(int j = 0; j < n; ++j){
				resultMatrix[i][j] = matrixA[i][j] + matrixB[i][j];					// performing addition
			}
		}
		return resultMatrix;														// returning result
	}
	
	/**
	 * splits parent matrix into child matrices
	 * @param parentMatrix
	 * @param childMatrix
	 * @param iB
	 * @param jB
	 */
	public static void split(int[][] parentMatrix, int[][] childMatrix, int iB, int jB){
		
		for(int i1 = 0, i2 = iB; i1 < childMatrix.length; ++i1, ++i2){
			for(int j1 = 0, j2 = jB; j1 < childMatrix.length; ++j1, ++j2){
				childMatrix[i1][j1] = parentMatrix[i2][j2];							// splitting parent into child 			
			}
		}
	}
	
	/**
	 * function to joing child matrices into parent matrix
	 * @param childMatrix
	 * @param parentMatrix
	 * @param iB
	 * @param jB
	 */
	public static void join(int[][] childMatrix, int[][] parentMatrix, int iB, int jB){
		for(int i1 = 0, i2 = iB; i1 < childMatrix.length; ++i1, ++i2){
			for(int j1 = 0, j2 = jB; j1 < childMatrix.length; ++j1, ++j2){
				parentMatrix[i2][j2] = childMatrix[i1][j1];							// joining chilMatrices into parentMatrix
			}
		}
	}
	
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		
		String classyFileName = "classicalMatrixData.txt";
		
		PrintWriter classyOutputFile = new PrintWriter(classyFileName);
		
		Random rand1 = new Random();										// used to generate random list of integers
		
			classyOutputFile.println("Currently Running - classicalMatrix" );	// used to make better sense of output data
			classyOutputFile.println("-----------------------------------");
			classyOutputFile.println(" n   |    RunTime (s)             |");
			
			int n1 = 2;												// n starts at size 2
			
		A : while(runTimeUnderTenMins){
			
			int[][] matrixA = new int[n1][n1];						// matrices to be multiplied
			int[][] matrixB = new int[n1][n1];
			
			int[][] resultMatrix = new int[n1][n1];					// result
			
			
			for(int i = 0 ; i < n1; ++i){
				for(int j = 0 ; j < n1 ; ++j){
					matrixA[i][j] = rand1.nextInt(9) + 1;				// filling up matrices to be multiplied
					matrixB[i][j] = rand1.nextInt(9) + 1;
				}
			}
			
			long initTime = System.currentTimeMillis();					// getting current time in ms
			
			Timer myTimer = new Timer();
			myTimer.schedule(new TimerTask() {								// scheduling for 10 mins
				public void run(){
					runTimeUnderTenMins = false;
					//break A;												// exit while loop immediately
					myTimer.cancel();
				}
			}, tenMinutes);
			
			if(!runTimeUnderTenMins){										// exiting loop if over ten mins
				break A;
			}
					
			resultMatrix = classicMatrixMult(matrixA, matrixB, n1);
			
			long endTime = System.currentTimeMillis();					// getting current time in ms
			
			classyOutputFile.print(n1);
			classyOutputFile.println("  |  time: " + ((endTime - initTime) / 1000.0) + " secs");	// printing runtime to outputfile
					
			n1 *= 2;
				
																	// cancel timer so it doesnt keep running
			myTimer.cancel();
			
		}
		
		classyOutputFile.close();
		
		System.out.println("Classic Matrix Multiplication Complete");
		
		// Strassen
		
		runTimeUnderTenMins = true;										// ressetting to true

		String strassenFileName = "strassenMatrixData.txt";
		
		PrintWriter strassenOutputFile = new PrintWriter(strassenFileName);
		
		Random rand = new Random();										// used to generate random list of integers
		
			strassenOutputFile.println("Currently Running - strassenMatrix" );	// used to make better sense of output data
			strassenOutputFile.println("-----------------------------------");
			strassenOutputFile.println(" n   |    RunTime (s)             |");
			
			int n = 2;												// n starts at size 2
			
		B: while(runTimeUnderTenMins){
			
			int[][] matrixA = new int[n][n];						// matrices to be multiplied
			int[][] matrixB = new int[n][n];
			
			int[][] resultMatrix = new int[n][n];					// result
			
			
			for(int i = 0 ; i < n; ++i){
				for(int j = 0 ; j < n ; ++j){
					matrixA[i][j] = rand1.nextInt(9) + 1;				// filling up matrices to be multiplied
					matrixB[i][j] = rand1.nextInt(9) + 1;
				}
			}
			
			
			long initTime = System.currentTimeMillis();					// getting current time in ms
			
			Timer myTimer1 = new Timer();
			myTimer1.schedule(new TimerTask() {								// scheduling for 10 mins
				public void run(){
					runTimeUnderTenMins = false;
					myTimer1.cancel();
				}
			}, tenMinutes);
			
			if(!runTimeUnderTenMins){										// exiting loop if over ten mins
				break B;
			}
			
			resultMatrix = strassenMatrixMult(matrixA, matrixB);
			
			long endTime = System.currentTimeMillis();					// getting current time in ms
			
			strassenOutputFile.print(n);
			strassenOutputFile.println("  |  time: " + ((endTime - initTime) / 1000.0) + " secs");	// printing runtime to outputfile
					
			n *= 2;
				
																// cancel timer so it doesnt keep running
			myTimer1.cancel();
			
		}
		
		strassenOutputFile.close();

		System.out.println("Strassen Matrix Multiplication Complete");
	}
}

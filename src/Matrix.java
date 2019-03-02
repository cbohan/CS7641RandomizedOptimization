
public class Matrix {
	public static double[][] ones(int rows, int columns) {
		double[][] matrix = new double[rows][columns];
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				matrix[i][j] = 1;
		
		return matrix;
	}
	
	public static double[][] createColumnVector(double... x) {
		double[][] columnVector = new double[x.length][1];
		for (int i = 0; i < x.length; i++)
			columnVector[i][0] = x[i];
		
		return columnVector;
	}
	
	public static int columnVectorMax(double[][] x) {
		if (x[0].length != 1)
			throw new IllegalArgumentException("Not a column vector");
		
		int maxI = 0;
		double maxIValue = x[0][0]; 
		for (int i = 1; i < x.length; i++) {
			if (x[i][0] > maxIValue) {
				maxIValue = x[i][0];
				maxI = i;
			}
		}
		
		return maxI;
	}
	
	public static double total(double[][] x) {
		double total = 0;
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < x[0].length; j++)
				total += x[i][j];
			
		return total;
	}
	
	public static double[][] multiply(double[][] a, double[][] b) {

        int aRows = a.length;
        int aColumns = a[0].length;
        int bRows = b.length;
        int bColumns = b[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        double[][] c = new double[aRows][bColumns];

        for (int i = 0; i < aRows; i++)
            for (int j = 0; j < bColumns; j++)
                for (int k = 0; k < aColumns; k++)
                    c[i][j] += a[i][k] * b[k][j];

        return c;
    }
	
	public static double[][] add(double[][] a, double[][] b) {
		if (a.length != b.length)
            throw new IllegalArgumentException("A:Columns: " + a.length + " did not match B:Columns " + b.length + ".");
		if (a[0].length != b[0].length)
            throw new IllegalArgumentException("A:Rows: " + a[0].length + " did not match B:Rows " + b[0].length + ".");
		
		double[][] c = new double[a.length][a[0].length];
		for (int i = 0; i < a.length; i++)
			for (int j = 0; j < a[0].length; j++)
				c[i][j] = a[i][j] + b[i][j];
		
		return c;
	}
	
	public static void print(double[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.print("\n");
		}
	}
}

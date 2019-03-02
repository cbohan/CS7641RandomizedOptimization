
public class Main {
	
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		ByteStringTester.doMimic("US Map Coloring");
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startTime;
		System.out.println("Elapsed Time: " + (elapsedTime / 1000000000.0) + " seconds.");
	}
	
}



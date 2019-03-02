
public class NeuralNetworkDatum {
	double[] input;
	int expectedOutput;
	
	public NeuralNetworkDatum(double[] input, int expectedOutput) {
		this.input = input;
		this.expectedOutput = expectedOutput;
	}
	
	public double[] getInput() { return input; }
	public int getExpectedOutput() { return expectedOutput; }
	
	public void print() {
		System.out.println("Neural Network Datum Inputs: ");
		for (int i = 0; i < input.length; i++)
			System.out.println(" " + input[i]);
		System.out.println("Neural Network Datum Ouput: " + expectedOutput);
	}
}

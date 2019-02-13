import java.util.Random;

public class Neuron {
	double value;
	double[] inputWeights;
	double bias;
	
	public Neuron(int numInputs) {
		inputWeights = new double[numInputs];
		Random rand = new Random();
		
		//Initialize weights to random number with mean 0, standard deviation 1.
		for (int i = 0; i < numInputs; i++) 
			inputWeights[i] = rand.nextGaussian();
		bias = rand.nextGaussian();
	}
	
	public void storeValue(double[] inputValues) {
		if (inputValues.length != inputWeights.length) 
			System.err.println("Wrong number of inputs to neuron.");
		
		double tempValue = 0;
		for (int i = 0; i < inputValues.length; i++) 
			tempValue += inputWeights[i] * inputValues[i];
		tempValue += bias;
		
		value = activationFunction(tempValue);
	}
	
	public double getValue() {
		return value;
	}
	
	public void print(int neuronNumber) {
		String printString = "Neuron " + neuronNumber + " has weights {";
		for (int i = 0; i < inputWeights.length; i++) {
			printString += inputWeights[i];
			if (i < inputWeights.length - 1)
				printString += ", ";
		}
		printString += "} and bias " + bias;
				
		System.out.println("  " + printString);
	}
	
	private double activationFunction(double x) {
		return 1.0 / (1.0 + Math.pow(Math.E, -x));
	}
}

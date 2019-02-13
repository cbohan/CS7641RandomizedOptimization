
public class NeuralNetwork {
	Layer[] layers;
	int numInputs;
	
	public NeuralNetwork(int numInputs, int... layerSizes) {
		this.numInputs = numInputs;
		layers = new Layer[layerSizes.length];
		
		for (int i = 0; i < layerSizes.length; i++) {
			if (i == 0)
				layers[i] = new Layer(layerSizes[i], numInputs);
			else
				layers[i] = new Layer(layerSizes[i], layerSizes[i-1]);
		}	
	}
	
	public double[] getOutputs(double[] inputValues) {
		for (int i = 0; i < layers.length; i++) {
			if (i == 0)
				layers[i].storeValues(inputValues);
			else
				layers[i].storeValues(layers[i-1]);
		}
		
		return layers[layers.length-1].getValues();
	}
	
	public void print()
	{
		System.out.println("Neural Network has " + numInputs + " inputs and " + layers.length + " layers.\n");
		for (int i = 0; i < layers.length; i++) {
			layers[i].print(i);
		}
	}
}

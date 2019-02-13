
public class Layer {
	Neuron[] neurons;
	
	public Layer(int size, int previousLayerSize) {
		neurons = new Neuron[size];
		
		for (int i = 0; i < size; i++) {
			neurons[i] = new Neuron(previousLayerSize);
		}
	}
	
	public void storeValues(double[] inputValues) {
		for (int i = 0; i < neurons.length; i++) {
			neurons[i].storeValue(inputValues);
		}
	}
	
	public void storeValues(Layer inputLayer) {
		double[] inputValues = inputLayer.getValues();
		storeValues(inputValues);
	}
	
	public double[] getValues() {
		double[] values = new double[neurons.length];
		for (int i = 0; i < neurons.length; i++) 
			values[i] = neurons[i].getValue();
		
		return values;
	}
	
	public void print(int layerNum) {
		System.out.println(" Layer " + layerNum + " has " + neurons.length + " neurons");
		for (int i = 0; i < neurons.length; i++)
			neurons[i].print(i);
		System.out.println();
	}
}

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArffLoader {
	static String relation;
	static List<ArffAttribute> attributes;
	static List<NeuralNetworkDatum> data;
	
	public static void load(String fileName) {
		attributes = new ArrayList<ArffAttribute>();
		data = new ArrayList<NeuralNetworkDatum>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			boolean reachedData = false;
		    
			while ((line = br.readLine()) != null) {
		    	if (reachedData) {
		    		String[] split = line.split(",");
		    		if (split.length < attributes.size())
		    			continue;
		    		
		    		double[] datumInputs = new double[getNeuralNetworkInputSize()];
		    		int curPosition = 0;
		    		for (int i = 0; i < attributes.size() - 1; i++) {
		    			if (attributes.get(i).isReal) {
		    				datumInputs[curPosition++] = Double.parseDouble(split[i]);
		    			} else {
		    				for (String enumValue : attributes.get(i).enumValues) {
		    					datumInputs[curPosition++] 
		    							= (enumValue.equals(split[i])) ? 1 : 0;
		    				}
		    			}
		    		}
		    		
		    		int datumOutput = 0;
		    		for (String enumValue : attributes.get(attributes.size() - 1).enumValues) {
		    			if (enumValue.equals(split[split.length - 1]))
		    				break;
		    			datumOutput++;
		    		}
		    		
		    		NeuralNetworkDatum datum = new NeuralNetworkDatum(datumInputs, datumOutput);
		    		data.add(datum);
				} else {
					String[] split = line.split("\\s+");
					if (line.startsWith("@RELATION"))
						relation = split[1];
					else if (line.startsWith("@ATTRIBUTE")) {
						if (split[2].equals("REAL")) 
							attributes.add(new ArffAttribute(split[1]));
						else {
							String enumString = split[2].replace("{", "").replace("}", "");
							String[] enumValues = enumString.split(",");
							attributes.add(new ArffAttribute(split[1], enumValues));
						}
					} else if (line.startsWith("@DATA")) {
						reachedData = true;
					}
				}
		    }
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static int getNeuralNetworkInputSize() {
		int inputOffset = 0;
		for (int i = 0; i < attributes.size() - 1; i++) {
			if (attributes.get(i).isReal) {
				attributes.get(i).offset = inputOffset;
				inputOffset++;
			} else {
				attributes.get(i).offset = inputOffset;
				inputOffset += attributes.get(i).enumValues.length;
			}
		}
		return inputOffset;
	}
	
	private static int getNeuralNetworkOutputSize() {
		return attributes.get(attributes.size() - 1).enumValues.length;
	}
	
	public static NeuralNet generateNeuralNetwork() { return generateNeuralNetwork(1.0); }
	public static NeuralNet generateNeuralNetwork(double initRange) {		
		int hiddenLayer = (getNeuralNetworkInputSize() + getNeuralNetworkOutputSize()) / 2;
		NeuralNet neuralNetwork = new NeuralNet(initRange, getNeuralNetworkInputSize(), 
				hiddenLayer, getNeuralNetworkOutputSize());

		return neuralNetwork;
	}
	
	public static List<NeuralNetworkDatum> getData() { return data; }
	public static int dataCount() { return data.size(); }
	public static NeuralNetworkDatum getDatum(int index) { return data.get(index); }
	
	public static void scaleData(String name, double scaleValue) {
		int offset = -1;
		for (ArffAttribute attribute : attributes) {
			if (attribute.name.equals(name))
				offset = attribute.offset;
		}
		
		if (offset == -1)
			throw new IllegalArgumentException("Could not find attribute: " + name);
		
		for (NeuralNetworkDatum datum : data) {
			datum.input[offset] *= scaleValue;
		}
	}
}

class ArffAttribute {
	String name;
	boolean isReal;
	String[] enumValues;
	int offset;
	
	public ArffAttribute(String name) {
		this.name = name;
		isReal = true;
	}
	
	public ArffAttribute(String name, String[] enumValues) {
		this.name = name;
		isReal = false;
		this.enumValues = new String[enumValues.length];
		for (int i = 0; i < enumValues.length; i++)
			this.enumValues[i] = enumValues[i];
	}
}
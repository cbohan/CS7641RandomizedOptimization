import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArffLoader {
	static String relation;
	static List<ArffAttribute> attributes;
	
	public static void load(String fileName) {
		attributes = new ArrayList<ArffAttribute>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			boolean reachedData = false;
		    
			while ((line = br.readLine()) != null) {
		    	if (reachedData) {
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
	
	public static void generateNeuralNetwork() {
		//Create input nodes.
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
		
		System.out.println("Input nodes: " + inputOffset);
		
		int outputCount = attributes.get(attributes.size() - 1).enumValues.length;
		
		System.out.println("Ouput nodes: " + outputCount);
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
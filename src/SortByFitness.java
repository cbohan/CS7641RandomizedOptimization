//Contains the classes used for sorting by fitness which is a required part of genetic algorithms.

import java.util.Comparator;

class NeuralNetSortByFitness implements Comparator<NeuralNet> {
	public int compare(NeuralNet a, NeuralNet b) {
		if (a.fitnessRecord > b.fitnessRecord)
			return -1;
		else if (b.fitnessRecord > a.fitnessRecord)
			return 1;
		else 
			return 0;
	}
	

}

class ByteStringSortByFitness implements Comparator<ByteString> {
	public int compare(ByteString a, ByteString b) {
		return b.fitness - a.fitness;
	}
} 
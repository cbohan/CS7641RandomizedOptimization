import java.io.File;

public class Main {
	//Where to save the .arff files.
	public static final String POKEMON_TRAINING_DATA_SET = "pokemonTraining.arff"; 
	public static final String POKEMON_TEST_DATA_SET = "pokemonTest.arff"; 
	
	public static void main(String[] args) {
		PokemonArffGenerator.generateFile(1, false);
		ArffLoader.load("data" + File.separator + Main.POKEMON_TRAINING_DATA_SET);
		ArffLoader.generateNeuralNetwork();
		
		NeuralNetwork neuralNet = new NeuralNetwork(2, 3, 2);
		neuralNet.print();
		
		double[] output = neuralNet.getOutputs(new double[] {1, 1});
		for (int i = 0; i < output.length; i++)
			System.out.println(output[i]);
	}
}

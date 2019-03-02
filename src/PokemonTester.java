import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class PokemonTester {
	//Where to save the .arff files.
	public static final String POKEMON_TRAINING_DATA_SET = "pokemonTraining.arff"; 
	public static final String POKEMON_TEST_DATA_SET = "pokemonTest.arff"; 
	
	public static void init() {
		PokemonArffGenerator.generateFile(3, false);
		ArffLoader.load("data" + File.separator + POKEMON_TRAINING_DATA_SET);
		ArffLoader.scaleData("height", 1.0 / PokemonArffGenerator.maxPokemonHeight);
		ArffLoader.scaleData("weight", 1.0 / PokemonArffGenerator.maxPokemonWeight);
	}
	
	public static void doPokemonRandomSearch() {
		long startTime = System.nanoTime();
		double bestFitness = 0;
		int correctlyClassified = 0;
		
		for (int i = 0; i < 100000; i++) {
			NeuralNet nn = ArffLoader.generateNeuralNetwork(100);
			NeuralNetPerformance performance = getCurrentPerformance(nn);
			
			if (performance.heuristic > bestFitness) {
				bestFitness = performance.heuristic;
				correctlyClassified = performance.correctlyClassified;
				System.out.println("Best Fitness: " + bestFitness + 
						", Correctly Classified: " + correctlyClassified);
			}
			
		}
		
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startTime;
		System.out.println("Elapsed Time: " + (elapsedTime / 1000000000) + " seconds.");
	}
	
	public static void doPokemonGeneticAlgorithm() {
		long startTime = System.nanoTime();
		
		//Set up initial population.
		ArrayList<NeuralNet> neuralNets = new ArrayList<NeuralNet>();
		int numInstances = 1000;
		for (int i = 0; i < numInstances; i++)
			neuralNets.add(ArffLoader.generateNeuralNetwork(10.0));
		
		//Main loop.
		for (int i = 0; i < 10000; i++) {
			//Evaluate fitness
			double totalFitness = 0;
			int bestScore = 0;
			for (NeuralNet nn : neuralNets) {
				NeuralNetPerformance performance = getCurrentPerformance(nn);
				nn.fitnessRecord = performance.heuristic;
				totalFitness += nn.fitnessRecord;
				bestScore = Math.max(bestScore, performance.correctlyClassified);
			}
			double averageFitness = totalFitness / numInstances;
			
			//Sort them
			Collections.sort(neuralNets, new NeuralNetSortByFitness());
			System.out.println("Avg Fitness: " + averageFitness + ", Best Fitness: " + 
					neuralNets.get(0).fitnessRecord + ", Best Score: " + bestScore);
			
			//Create offspring
			for (int n = 0; n < numInstances / 4; n++) {
				int dadOffset = n * 2 + 0;
				int momOffset = dadOffset + 1;
				int sonOffset = (numInstances / 2) + dadOffset;
				int daughterOffset = sonOffset + 1;
				
				NeuralNet[] children = 
					NeuralNet.createOffspring(neuralNets.get(dadOffset), neuralNets.get(momOffset));
				
				neuralNets.set(sonOffset, children[0]);
				neuralNets.set(daughterOffset, children[1]);
			}
		}
		
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startTime;
		System.out.println("Elapsed Time: " + (elapsedTime / 1000000000) + " seconds.");
	}
	
	public static void doPokemonSimulatedAnnealing() {
		long startTime = System.nanoTime();
		
		NeuralNet nn = ArffLoader.generateNeuralNetwork();
		
		double temp = 1;
		double coolingRate = .0001;
		while (temp > .0000001) {
			NeuralNet oldNeuralNet = nn.copy();
			NeuralNet newNeuralNet = nn.copy();
			newNeuralNet.getRandomNeighbor();
			
			double oldHeuristic = getCurrentPerformance(oldNeuralNet).heuristic;
			double newHeuristic = getCurrentPerformance(newNeuralNet).heuristic;
			
			double probability = 1;
			if (oldHeuristic < newHeuristic) 
				probability = Math.exp((oldHeuristic - newHeuristic) / temp);
			
			System.out.println(oldHeuristic + " " + newHeuristic + " " + probability);
			
			if (probability > Math.random())
				nn = oldNeuralNet;
			else
				nn = newNeuralNet;
			
			NeuralNetPerformance performance = getCurrentPerformance(nn);
			System.out.println(temp + ", " + performance.heuristic + ", " + performance.correctlyClassified + "/" + PokemonArffGenerator.trainingDataCount);
			
			temp *= 1 - coolingRate;
		}
		
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startTime;
		System.out.println("Elapsed Time: " + (elapsedTime / 1000000000) + " seconds.");
	}
	
	public static void doPokemonHillClimbing() {
		long startTime = System.nanoTime();
		
		NeuralNet nn = ArffLoader.generateNeuralNetwork();

		int i = 0;
		double startingFitness = -1; //When we loop around back to hill climbing the first weight, we set this.
		while (true) {
			NeuralNetPerformance performance = getCurrentPerformance(nn);
			
			if (i % nn.getNumWeights() == 0) {
				//We've gone all the way around, hill-climbed every parameter and nothing happened.
				if (performance.heuristic == startingFitness) { 
					System.out.println("DONE");
					break;
				}
				startingFitness = performance.heuristic;
			}
			
			System.out.println(performance.heuristic + ", " + performance.correctlyClassified + "/" + PokemonArffGenerator.trainingDataCount);
			
			nn.localSearch(i, ArffLoader.getData());
			
			i++;
		}
		
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startTime;
		System.out.println("Elapsed Time: " + (elapsedTime / 1000000000) + " seconds.");
	}
	
	private static NeuralNetPerformance getCurrentPerformance(NeuralNet nn) {
		double heuristic = 0;
		int correctlyClassified = 0;
		
		for (NeuralNetworkDatum datum : ArffLoader.getData()) {
			double[][] prediction = nn.predict(Matrix.createColumnVector(datum.input));
			double correctAnswerValue = prediction[datum.expectedOutput][0];
			heuristic += correctAnswerValue;
			
			if (Matrix.columnVectorMax(prediction) == datum.expectedOutput)
				correctlyClassified++;
		}
		
		return new NeuralNetPerformance(heuristic, correctlyClassified);
	}
}

class NeuralNetPerformance {
	public NeuralNetPerformance(double h, int cc) {
		heuristic = h;
		correctlyClassified = cc;
	}
	
	public double heuristic;
	public int correctlyClassified;
}

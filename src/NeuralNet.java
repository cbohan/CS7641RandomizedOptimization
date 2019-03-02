import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class NeuralNet {
	List<double[][]> weights;
	List<double[][]> biases;
	int numWeights;
	static Random rand;
	public double fitnessRecord;
	
	public NeuralNet(double initRange, int... layerSizes) {
		weights = new LinkedList<double[][]>();
		biases = new LinkedList<double[][]>();
		numWeights = 0;
		for (int i = 1; i < layerSizes.length; i++) {
			weights.add(new double[layerSizes[i]][layerSizes[i-1]]);
			biases.add(new double[layerSizes[i]][1]);
			numWeights += (layerSizes[i] * layerSizes[i-1]) + layerSizes[i];
		}
		
		rand = new Random();
		for (double[][] matrix : weights) {
			for (int i = 0; i < matrix.length; i++)
				for (int j = 0; j < matrix[0].length; j++)
					matrix[i][j] = initRange * rand.nextGaussian() / Math.sqrt(matrix[0].length);
		}
	}
	
	private NeuralNet() {
		weights = new LinkedList<double[][]>();
		biases = new LinkedList<double[][]>();
	}
	
	public NeuralNet copy() {
		NeuralNet nn = new NeuralNet();
		nn.numWeights = numWeights;
		rand = new Random();
		
		for (double[][] weight : weights) {
			double[][] nnWeight = new double[weight.length][weight[0].length];
			for (int i = 0; i < weight.length; i++)
				for (int j = 0; j < weight[0].length; j++)
					nnWeight[i][j] = weight[i][j];
			nn.weights.add(nnWeight);
		}
		for (double[][] bias : biases) {
			double[][] nnBias = new double[bias.length][bias[0].length];
			for (int i = 0; i < bias.length; i++)
				for (int j = 0; j < bias[0].length; j++)
					nnBias[i][j] = bias[i][j];
			nn.biases.add(nnBias);
		}
		
		return nn;
	}
	
	public static NeuralNet[] createOffspring(NeuralNet a, NeuralNet b) {
		//Mix the weights and biases together.
		NeuralNet[] offspring = new NeuralNet[2];
		offspring[0] = a.copy();
		offspring[1] = a.copy();
		
		int i = 0;
		for (double[][] weight : b.weights) {
			double[][] weightCopy = new double[weight.length][weight[0].length];
			for (int n = 0; n < weight.length; n++)
				for (int m = 0; m < weight[0].length; m++)
					weightCopy[n][m] = weight[n][m];
			
			if (rand.nextFloat() >= .5)
				offspring[0].weights.set(i, weightCopy);
			if (rand.nextFloat() >= .5)
				offspring[1].weights.set(i, weightCopy);
			
			i++;
		}
		
		i = 0;
		for (double[][] bias : b.biases) {
			double[][] biasCopy = new double[bias.length][bias[0].length];
			for (int n = 0; n < bias.length; n++)
				for (int m = 0; m < bias[0].length; m++)
					biasCopy[n][m] = bias[n][m];
			
			if (rand.nextFloat() >= .5)
				offspring[0].biases.set(i, biasCopy);
			if (rand.nextFloat() >= .5)
				offspring[1].biases.set(i, biasCopy);
			
			i++;
		}
		
		//Add a small random number to each weight/bias.
		for (int x = 0; x <= 1; x++) {
			for (double[][] weight : offspring[x].weights)
				for (int n = 0; n < weight.length; n++)
					for (int m = 0; m < weight[0].length; m++)
						weight[n][m] += rand.nextGaussian();
			for (double[][] bias : offspring[x].biases)
				for (int n = 0; n < bias.length; n++)
					for (int m = 0; m <  bias[0].length; m++)
						bias[n][m] += rand.nextGaussian();
		}
		
		return offspring;
	}
	
	public int getNumWeights() { return numWeights; }
	
	public void getRandomNeighbor() {
		int weightNum = rand.nextInt(numWeights);
		int currentWeightNum = 0;
		
		for (int i = 0; i < weights.size(); i++) {
			double[][] curWeights = weights.get(i);
			int nextWeightNum = currentWeightNum + (curWeights.length * curWeights[0].length);
			
			if (currentWeightNum <= weightNum && weightNum < nextWeightNum) {
				int position = weightNum - currentWeightNum;
				int row = position % curWeights.length;
				int col = position / curWeights.length;
				curWeights[row][col] += rand.nextGaussian();
			} 
			
			currentWeightNum = nextWeightNum;
		}
		
		for (int i = 0; i < biases.size(); i++) {
			double[][] curBiases = biases.get(i);
			int nextWeightNum = currentWeightNum + (curBiases.length * curBiases[0].length);
			
			if (currentWeightNum <= weightNum && weightNum < nextWeightNum) {
				int position = weightNum - currentWeightNum;
				int row = position % curBiases.length;
				int col = position / curBiases.length;
				curBiases[row][col] += rand.nextGaussian();
			} 
			
			currentWeightNum =	nextWeightNum;
		}
	}
	
	public void localSearch(int weightNum, List<NeuralNetworkDatum> data) {
		weightNum = weightNum % numWeights;
		int currentWeightNum = 0;
		
		for (int i = 0; i < weights.size(); i++) {
			double[][] curWeights = weights.get(i);
			int nextWeightNum = currentWeightNum + (curWeights.length * curWeights[0].length);
			
			if (currentWeightNum <= weightNum && weightNum < nextWeightNum) {
				int position = weightNum - currentWeightNum;
				int row = position % curWeights.length;
				int col = position / curWeights.length;
				
				for (double jumpValue : new double[] {1, .1, .01, .001}) {
					boolean atMax = false;
					int abandonCounter = 0;

					while (atMax == false && (abandonCounter < 10 || jumpValue == 1)) {
						double currentFitness = evaluateFitness(data);
						curWeights[row][col] += jumpValue;
						double upFitness = evaluateFitness(data);
						curWeights[row][col] -= jumpValue * 2;
						double downFitness = evaluateFitness(data);
						
						if (upFitness > currentFitness && upFitness > downFitness)
							curWeights[row][col] += jumpValue * 2;
						else if (currentFitness >= upFitness && currentFitness >= downFitness) {
							curWeights[row][col] += jumpValue;
							atMax = true;
						}
						abandonCounter++;
					}
				} 				
				
				return;
			} 
			
			currentWeightNum = nextWeightNum;
		}
		
		for (int i = 0; i < biases.size(); i++) {
			double[][] curBiases = biases.get(i);
			int nextWeightNum = currentWeightNum + (curBiases.length * curBiases[0].length);
			
			if (currentWeightNum <= weightNum && weightNum < nextWeightNum) {
				int position = weightNum - currentWeightNum;
				int row = position % curBiases.length;
				int col = position / curBiases.length;

				for (double jumpValue : new double[] {1, .1, .01, .001}) {
					boolean atMax = false;
					int abandonCounter = 0;
					
					while (atMax == false && (abandonCounter < 10 || jumpValue == 1)) {
						double currentFitness = evaluateFitness(data);
						curBiases[row][col] += jumpValue;
						double upFitness = evaluateFitness(data);
						curBiases[row][col] -= jumpValue * 2;
						double downFitness = evaluateFitness(data);
						
						if (upFitness > currentFitness && upFitness > downFitness)
							curBiases[row][col] += jumpValue * 2;
						else if (currentFitness >= upFitness && currentFitness >= downFitness) {
							curBiases[row][col] += jumpValue;
							atMax = true;
						}	
						
						abandonCounter++;
					}
				} 	
				
				return;
			} 
			
			currentWeightNum =	nextWeightNum;
		}
	}
	
	private double evaluateFitness(List<NeuralNetworkDatum> data) {
		double fitness = 0;
		
		for (NeuralNetworkDatum datum : data) {
			double[][] prediction = this.predict(Matrix.createColumnVector(datum.input));
			double correctAnswerValue = prediction[datum.expectedOutput][0];
			fitness += correctAnswerValue;
		}
		
		return fitness;
	}
	
	public double[][] predict(double[][] input) {
		double[][] activations = input;

		for (int i = 0; i < weights.size(); i++) {
			double[][] w = weights.get(i);
			double[][] b = biases.get(i);
			
			double[][] temp = Matrix.multiply(w, activations);
			temp = Matrix.add(temp, b);
			activations = activation(temp);
		}
		
		return activations;
	}
	
	public double getMaxWeight() {
		double maxWeight = 0;
		for (double[][] weight : weights)
			for (int i = 0; i < weight.length; i++)
				for (int j = 0; j < weight[0].length; j++)
					if (Math.abs(weight[i][j]) > maxWeight)
						maxWeight = Math.abs(weight[i][j]);

		for (double[][] bias : biases)
			for (int i = 0; i < bias.length; i++)
				for (int j = 0; j < bias[0].length; j++)
					if (Math.abs(bias[i][j]) > maxWeight)
						maxWeight = Math.abs(bias[i][j]);
		
		return maxWeight;
	}
	
	private double[][] activation(double[][] x) { 
		double[][] output = new double[x.length][x[0].length];
		/*for (int i = 0; i < x.length; i++)
			for (int j = 0; j < x[0].length; j++)
				output[i][j] = 1.0 / (1.0 + Math.exp(-x[i][j])); */
		
		double total = 0;
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < x[0].length; j++)
				total += Math.exp(x[i][j]);
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < x[0].length; j++)
				output[i][j] = Math.exp(x[i][j]) / total;
		
		return output;
	}
}


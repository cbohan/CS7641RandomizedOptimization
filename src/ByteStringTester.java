import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ByteStringTester {
	private static Map<Byte, Character> ascii;
	
	public static void doMimic(String test) {
		//Create 1000 individuals with random byte strings.
		int byteStringLength = getByteStringLength(test);
		int numInstances = 1000;
		ArrayList<ByteString> byteStrings = new ArrayList<ByteString>();
		for (int i = 0; i < numInstances; i++)
			byteStrings.add(new ByteString(byteStringLength));
		
		double percentile = .85;
		int n = 0;
		boolean done = false;
		
		int bestAllTime = 0;
		while (done == false && n < 2000) {
			//Get the nth percentile.
			for (ByteString byteString : byteStrings)  
				getHeuristic(test, byteString);
			byteStrings.sort(new ByteStringSortByFitness());
			
			int numToRemove = (int) (byteStrings.size() * percentile);
			
			if (test.equals("Shakespear")) {
				numToRemove = 0;
				for (int i = 0; i < byteStrings.size(); i++)
					if (byteStrings.get(i).fitness < byteStrings.get(0).fitness)
						numToRemove ++;
			}
			
			for (int i = 0; i < numToRemove; i++)
				byteStrings.remove(byteStrings.size() - 1);
			
			double totalFitness = 0;
			for (ByteString byteString : byteStrings)
				totalFitness += byteString.fitness;
			
			int best = byteStrings.get(0).fitness;
			if (bestAllTime < best)
				bestAllTime = best;
			double average = totalFitness / byteStrings.size();
			
			System.out.println("Generation: " + (n++) + ", Best: " + best +  ", Average: " + 
			average);
			
			if (byteStrings.get(0).fitness == 41 && test.equals("Shakespear"))
				done = true;
			
			if (Math.abs(best - average) < 1)
				done = true;
			
			//Create new distribution.
			ProbabilityDistribution probDist = new ProbabilityDistribution(byteStrings);
			if (test.equals("Bumpy Hill"))
				probDist.maxProbability = 1;
			
			//Get new samples from it.
			byteStrings = probDist.sample(numInstances);			
		}
		
		System.out.println(bestAllTime);
	}
	
	public static void doSimulatedAnnealing(String test) {
		ByteString byteString = new ByteString(getByteStringLength(test));
		ByteString bestByteString = byteString;
		
		double temp = 500;
		double coolingRate = .000001;
		double minTemp = 10;
		
		if (test.equals("Shakespear")) {
			temp = 2;
			coolingRate = .000001;
			minTemp = .01;
		} else if (test.equals("US Map Coloring")) {
			temp = 5;
			coolingRate = .00001;
			minTemp = .01;
		}
		
		int generation = 0;
		while (temp > minTemp) {
			ByteString randomNeighbor = byteString.getANeighbor();
			
			getHeuristic(test, byteString);
			getHeuristic(test, randomNeighbor);
			
			if (byteString.fitness > bestByteString.fitness)
				bestByteString = byteString;
					
			double probability = 1;
			if (byteString.fitness > randomNeighbor.fitness)
				probability = Math.exp((randomNeighbor.fitness - byteString.fitness) / temp);
			
			System.out.println("Generation: " + (generation++) + ", Fitness: " + byteString.fitness);
			
			if (probability > Math.random())
				byteString = randomNeighbor;
			
			temp *= 1 - coolingRate;
		}
		
		System.out.println(bestByteString.fitness);
	}
	
	public static void doHillClimbing(String test) {
		//Create a byte string of the correct length.
		ByteString byteString = new ByteString(getByteStringLength(test));
		
		boolean foundSolution = false;
		int generation = 0;
		while (foundSolution == false) {
			//Check if we've found the solution.
			getHeuristic(test, byteString);
			System.out.println("Generation: " + (generation++) + ", Fitness: " + byteString.fitness);
			
			//Find the best neighbor.
			ByteString[] neighbors = byteString.getNeighbors();
			ByteString bestNeighbor = byteString;
			for (ByteString neighbor : neighbors) { 
				getHeuristic(test, neighbor);
				
				if (neighbor.fitness > bestNeighbor.fitness)
					bestNeighbor = neighbor;
			}
			
			if (bestNeighbor == byteString) 
				foundSolution = true;
			else
				byteString = bestNeighbor;
		}
	}
		
	public static void doGeneticAlgorithm(String test) {
		//Create 1000 individuals with random byte strings.
		int byteStringLength = getByteStringLength(test);
		int numInstances = 1000;
		ArrayList<ByteString> byteStrings = new ArrayList<ByteString>();
		for (int i = 0; i < numInstances; i++)
			byteStrings.add(new ByteString(byteStringLength));
		
		int bestFitness = Integer.MIN_VALUE;
		int generationOfBestFitness = 0;
		int generation = 0;
		boolean meetsCriteria = false;
		while ((generationOfBestFitness < generation - 1000) == false && meetsCriteria == false) {			
			//Sort by fitness.
			for (ByteString byteString : byteStrings) 
				getHeuristic(test, byteString);
			byteStrings.sort(new ByteStringSortByFitness());
			System.out.println("Generation: " + generation + ", Best Fitness: " + byteStrings.get(0).fitness);
			
			if (byteStrings.get(0).fitness > bestFitness) {
				bestFitness = byteStrings.get(0).fitness;
				generationOfBestFitness = generation;
			}
			generation++;
			
			//'Breed' high fitness parents.
			for (int n = 0; n < numInstances / 4; n++) {
				int dadOffset = n * 2 + 0;
				int momOffset = dadOffset + 1;
				int sonOffset = (numInstances / 2) + dadOffset;
				int daughterOffset = sonOffset + 1;
				
				ByteString[] children = ByteString.createOffspring(byteStrings.get(dadOffset), 
						byteStrings.get(momOffset));
				
				byteStrings.set(sonOffset, children[0]);
				byteStrings.set(daughterOffset, children[1]);
			}
			
			if (test.equals("Shakespear") && byteStrings.get(0).fitness == 41)
				meetsCriteria = true;
			else if (test.equals("US Map Coloring") && byteStrings.get(0).fitness == 0)
				meetsCriteria = true;
		}
		
		System.out.println(bestFitness);
	}
	
	private static void getHeuristic(String test, ByteString byteString) {
		if (test.equals("Shakespear"))
			byteString.fitness = shakespearHeuristic(byteString.byteString);
		else if (test.equals("Bumpy Hill"))
			byteString.fitness = bumpyHillHeuristic(byteString.byteString);
		else if (test.equals("US Map Coloring"))
			byteString.fitness = usMapColoringHeuristic(byteString.byteString);
	}
	
	private static int getByteStringLength(String test) {
		int byteStringLength = -1;
		if (test.equals("Shakespear"))
			byteStringLength = 41;
		else if (test.equals("Bumpy Hill"))
			byteStringLength = 4;
		else if (test.equals("US Map Coloring"))
			byteStringLength = 12;
		
		return byteStringLength;
	}
	
	private static int usMapColoringHeuristic(byte[] byteString) {
		//Each two bits represents the color of one state.
		int washington = getColor(byteString[0], 0);
		int oregon = getColor(byteString[0], 1);
		int california = getColor(byteString[0], 2);
		int idaho = getColor(byteString[0], 3);
		
		int nevada = getColor(byteString[1], 0);
		int utah = getColor(byteString[1], 1);
		int arizona = getColor(byteString[1], 2);
		int montana = getColor(byteString[1], 3);
		
		int wyoming = getColor(byteString[2], 0);
		int colorado = getColor(byteString[2], 1);
		int newMexico = getColor(byteString[2], 2);
		int northDakota = getColor(byteString[2], 3);
		
		int southDakota = getColor(byteString[3], 0);
		int nebraska = getColor(byteString[3], 1);
		int kansas = getColor(byteString[3], 2);
		int oklahoma = getColor(byteString[3], 3);
		
		int texas = getColor(byteString[4], 0);
		int minnesota = getColor(byteString[4], 1);
		int iowa = getColor(byteString[4], 2);
		int missouri = getColor(byteString[4], 3);
		
		int arkansas = getColor(byteString[5], 0);
		int louisiana = getColor(byteString[5], 1);
		int wisconsin = getColor(byteString[5], 2);
		int illinois = getColor(byteString[5], 3);
		
		int kentucky = getColor(byteString[6], 0);
		int tennessee = getColor(byteString[6], 1);
		int mississippi = getColor(byteString[6], 2);
		int michigan = getColor(byteString[6], 3);
		
		int indiana = getColor(byteString[7], 0);
		int alabama = getColor(byteString[7], 1);
		int ohio = getColor(byteString[7], 2);
		int georgia = getColor(byteString[7], 3);
		
		int maine = getColor(byteString[8], 0);
		int newHampshire = getColor(byteString[8], 1);
		int vermont = getColor(byteString[8], 2);
		int massachusetts = getColor(byteString[8], 3);
		
		int connecticut = getColor(byteString[9], 0);
		int rhodeIsland = getColor(byteString[9], 1);
		int newYork = getColor(byteString[9], 2);
		int pennsylvania = getColor(byteString[9], 3);
		
		int newJersey = getColor(byteString[10], 0);
		int delaware = getColor(byteString[10], 1);
		int maryland = getColor(byteString[10], 2);
		int westVirginia = getColor(byteString[10], 3);
		
		int virginia = getColor(byteString[11], 0);
		int northCarolina = getColor(byteString[11], 1);
		int southCarolina = getColor(byteString[11], 2);
		int florida = getColor(byteString[11], 3);
		
		int score = 0;
		if (washington == oregon) { score--; }
		if (washington == idaho) { score--; }
		if (oregon == idaho) { score--; }
		if (oregon == california) { score--; }
		if (oregon == nevada) { score--; }
		if (california == nevada) { score--; }
		if (california == arizona) { score--; }
		if (idaho == nevada) { score--; }
		if (idaho == utah) { score--; }
		if (idaho == wyoming) { score--; }
		if (idaho == montana) { score--; }
		if (nevada == utah) { score--; }
		if (nevada == arizona) { score--; }
		if (utah == wyoming) { score--; }
		if (utah == colorado) { score--; }
		if (utah == newMexico) { score--; }
		if (utah == arizona) { score--; }
		if (arizona == colorado) { score--; }
		if (arizona == newMexico) { score--; }
		if (montana == wyoming) { score--; }
		if (montana == northDakota) { score--; }
		if (montana == southDakota) { score--; }
		if (wyoming == southDakota) { score--; }
		if (wyoming == nebraska) { score--; }
		if (wyoming == colorado) { score--; }
		if (colorado == nebraska) { score--; }
		if (colorado == kansas) { score--; }
		if (colorado == oklahoma) { score--; }
		if (colorado == newMexico) { score--; }
		if (newMexico == oklahoma) { score--; }
		if (newMexico == texas) { score--; }
		if (northDakota == minnesota) { score--; }
		if (northDakota == southDakota) { score--; }
		if (southDakota == minnesota) { score--; }
		if (southDakota == iowa) { score--; }
		if (southDakota == nebraska) { score--; }
		if (nebraska == iowa) { score--; }
		if (nebraska == missouri) { score--; }
		if (nebraska == kansas) { score--; }
		if (kansas == missouri) { score--; }
		if (kansas == oklahoma) { score--; }
		if (oklahoma == missouri) { score--; }
		if (oklahoma == arkansas) { score--; }
		if (oklahoma == texas) { score--; }
		if (texas == arkansas) { score--; }
		if (texas == louisiana) { score--; }
		if (minnesota == wisconsin) { score--; }
		if (minnesota == iowa) { score--; }
		if (iowa == wisconsin) { score--; }
		if (iowa == illinois) { score--; }
		if (iowa == missouri) { score--; }
		if (missouri == illinois) { score--; }
		if (missouri == kentucky) { score--; }
		if (missouri == tennessee) { score--; }
		if (missouri == arkansas) { score--; }
		if (arkansas == tennessee) { score--; }
		if (arkansas == mississippi) { score--; }
		if (arkansas == louisiana) { score--; }
		if (wisconsin == michigan) { score--; }
		if (wisconsin == illinois) { score--; }
		if (illinois == indiana) { score--; }
		if (illinois == kentucky) { score--; }
		if (michigan == ohio) { score--; }
		if (michigan == indiana) { score--; }
		if (indiana == ohio) { score--; }
		if (indiana == kentucky) { score--; }
		if (kentucky == ohio) { score--; }
		if (kentucky == westVirginia) { score--; }
		if (kentucky == virginia) { score--; }
		if (kentucky == tennessee) { score--; }
		if (tennessee == virginia) { score--; }
		if (tennessee == northCarolina) { score--; }
		if (tennessee == georgia) { score--; }
		if (tennessee == alabama) { score--; }
		if (tennessee == mississippi) { score--; }
		if (mississippi == alabama) { score--; }
		if (alabama == georgia) { score--; }
		if (alabama == florida) { score--; }
		if (maine == newHampshire) { score--; }
		if (newHampshire == vermont) { score--; }
		if (newHampshire == massachusetts) { score--; }
		if (vermont == newYork) { score--; }
		if (vermont == massachusetts) { score--; }
		if (massachusetts == connecticut) { score--; }
		if (massachusetts == rhodeIsland) { score--; }
		if (massachusetts == newYork) { score--; }
		if (newYork == pennsylvania) { score--; }
		if (newYork == newJersey) { score--; }
		if (newYork == connecticut) { score--; }
		if (connecticut == rhodeIsland) { score--; }
		if (pennsylvania == newJersey) { score--; }
		if (pennsylvania == delaware) { score--; }
		if (pennsylvania == maryland) { score--; }
		if (westVirginia == maryland) { score--; }
		if (westVirginia == virginia) { score--; }
		if (virginia == northCarolina) { score--; }
		if (northCarolina == southCarolina) { score--; }
		if (georgia == southCarolina) { score--; }
		if (georgia == florida) { score--; }
		
		return score;
	}
	
	private static int getColor(byte b, int position)
	{
		int firstPosition = position * 2;
		int secondPosition = firstPosition + 1;
		return ((b >> firstPosition) & 1) + (2 * ((b >> secondPosition) & 1));
	}
	
	private static int bumpyHillHeuristic(byte[] byteString) {
		int valueInt = ByteBuffer.wrap(byteString).getInt();
		double value = valueInt / 1000.0;
		
		double function = Math.sin(value) + (Math.cos(value / 1.9)) + (Math.sin(value / 2.8)) +
				(Math.cos(value / 5.1)) + (Math.sin(value / 7.2)) + (Math.cos(value / 11.3)) + 
				(Math.sin(value / 13.4));
		function *= 1000;
		return (int) Math.round(function);
	}
	
	
	//I got the idea for this one from 'The Coding Train' Youtube channel.
	private static int shakespearHeuristic(byte[] byteString) {
		//Create a list of all the important ascii characters for this heuristic function.
		if (ascii == null) {
			ascii = new HashMap<Byte, Character>();
			ascii.put((byte)84, 'T');
			ascii.put((byte)111, 'o');
			ascii.put((byte)11, ' ');
			ascii.put((byte)98, 'b');
			ascii.put((byte)101, 'e');
			ascii.put((byte)114, 'r');
			ascii.put((byte)110, 'n');
			ascii.put((byte)116, 't');
			ascii.put((byte)104, 'h');
			ascii.put((byte)97, 'a');
			ascii.put((byte)105, 'i');
			ascii.put((byte)115, 's');
			ascii.put((byte)113, 'q');
			ascii.put((byte)117, 'u');
			ascii.put((byte)46, '.');
			ascii.put((byte)63, '?');
		}
		
		char[] correctCharString = "To be or not to be? That is the question.".toCharArray();
		char[] checkCharString = new char[byteString.length];
		for (int i = 0; i < byteString.length; i++) {
			if (ascii.containsKey(byteString[i]))
				checkCharString[i] = ascii.get(byteString[i]);
			else
				checkCharString[i] = '*';
		}
		
		int correctChars = 0;
		for (int i = 0; i < correctCharString.length; i++) {
			if (correctCharString[i] == checkCharString[i])
				correctChars++;
		}
		
		return correctChars;
	}
	
}

class ByteString {
	private static Random rand = new Random();
	public byte[] byteString;
	public int fitness;
	
	public ByteString(int length) {
		this.byteString = new byte[length];
		rand.nextBytes(byteString);
		this.fitness = 0;
	}
	
	private ByteString copy() {
		ByteString copy = new ByteString(byteString.length);
		for (int i = 0; i < byteString.length; i++)
			copy.byteString[i] = this.byteString[i];
		
		return copy;
	}
	
	public static ByteString[] createOffspring(ByteString dad, ByteString mom) {
		ByteString son = dad.copy();
		ByteString daughter = mom.copy();
		
		for (int i = 0; i < son.byteString.length; i++) {
			if (rand.nextBoolean()) {
				byte temp = son.byteString[i];
				son.byteString[i] = daughter.byteString[i];
				daughter.byteString[i] = temp;
			}
			
			if (rand.nextFloat() < .1f) {
				byte[] newByte = new byte[1];
				rand.nextBytes(newByte);
				son.byteString[i] = newByte[0];
			}
			
			if (rand.nextFloat() < .1f) {
				byte[] newByte = new byte[1];
				rand.nextBytes(newByte);
				daughter.byteString[i] = newByte[0];
			}
		}
		
		return new ByteString[] { son, daughter };
	}

	public ByteString[] getNeighbors() {
		ArrayList<ByteString> neighbors = new ArrayList<ByteString>();
		
		for (int i = 0; i < byteString.length; i++) {
			byte b = byteString[i];
			
			for (int j = 0; j < 8; j++) {
				byte newByte = (byte) (b ^ (1 << j));
				ByteString newByteString = this.copy();
				newByteString.byteString[i] = newByte;
				neighbors.add(newByteString);
			}
		}
		
		return neighbors.toArray(new ByteString[neighbors.size()]);
	}
	
	public ByteString getANeighbor() {
		int randomByteOffset = rand.nextInt(byteString.length);
		int randomBitOffset = rand.nextInt(8);
		
		ByteString newByteString = this.copy();
		newByteString.byteString[randomByteOffset] = (byte) 
				(newByteString.byteString[randomByteOffset] ^ (1 << randomBitOffset));
		
		return newByteString;
	}
	
	public ArrayList<Boolean> getBitString() {
		ArrayList<Boolean> bitString = new ArrayList<Boolean>();
		for (byte b : byteString) 
			for (int i = 0; i < 8; i++)
				bitString.add(getBit(b, i) != 0);
		
		return bitString;
	}
	
	private byte getBit(byte b, int position)
	{
	   return (byte) ((b >> position) & 1);
	}
	
	
	public void setByte(int offset, boolean[] bits) {
		byteString[offset] = 0;
		for (int i = 0; i < 8; i++)
			if (bits[i])
				byteString[offset] += Math.pow(2, i);
	}
}

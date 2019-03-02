import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Random;

public class PokemonArffGenerator {
	private static final int WRITES_PER_POKEMON_TEST = 100;
	
	public static int trainingDataCount = 0;
	
	public static double maxPokemonHeight = 0;
	public static double maxPokemonWeight = 0;
	
	//Creates two arff files. One contains the training data for the pokemon and the other 
	//contains the test data.
	public static void generateFile(int writesPerPokemonTraining, boolean showOutput) {
		File training = new File("data" + File.separator + PokemonTester.POKEMON_TRAINING_DATA_SET);
		File test = new File("data" + File.separator + PokemonTester.POKEMON_TEST_DATA_SET);
		
		try {
			for (File file : new File[] { training, test } )
			{
				int writesPerPokemon = writesPerPokemonTraining;
				if (file.equals(test))
					writesPerPokemon = WRITES_PER_POKEMON_TEST;
				
				FileOutputStream outputStream = new FileOutputStream(file);
				BufferedWriter bufferedWriter 
					= new BufferedWriter(new OutputStreamWriter(outputStream));
				
				WriteLine(bufferedWriter, "@RELATION pokemon");
				WriteLine(bufferedWriter, "");
				
				WriteLine(bufferedWriter, "@ATTRIBUTE	type	{grass,fire,water,bug,flying,normal,poison,electric,ground}");
				WriteLine(bufferedWriter, "@ATTRIBUTE	height	REAL");
				WriteLine(bufferedWriter, "@ATTRIBUTE	weight	REAL");
				WriteLine(bufferedWriter, "@ATTRIBUTE	name	{Bulbasaur,Ivysaur,Venusaur," + 
				"Charmander,Charmeleon,Charizard,Squirtle,Wartortle,Blastoise,Caterpie,Metapod," + 
				"Butterfree,Weedle,Kakuna,Beedrill,Pidgey,Pidgeotto,Pidgeot,Rattata,Raticate," + 
				"Spearow,Fearow,Ekans,Arbok,Pikachu,Raichu,Sandshrew,Sandslash,NidoranF,Nidorina," +
				"Nidoqueen,NidoranM,Nidorino,Nidoking}");
				WriteLine(bufferedWriter, "");
				
				Random rand = new Random();
				WriteLine(bufferedWriter, "@DATA");
				boolean isTraining = file.equals(training);
				WritePokemon(bufferedWriter, rand, "grass", 0.7f, 6.9f, "Bulbasaur", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "grass", 1.0f, 13.0f, "Ivysaur", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "grass", 2.0f, 100.0f, "Venusaur", writesPerPokemon, isTraining);
				
				WritePokemon(bufferedWriter, rand, "fire", 0.6f, 8.5f, "Charmander", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "fire", 1.1f, 19.0f, "Charmeleon", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "fire", 1.7f, 90.5f, "Charizard", writesPerPokemon, isTraining);
				
				WritePokemon(bufferedWriter, rand, "water", 0.5f, 9.0f, "Squirtle", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "water", 1.0f, 22.5f, "Wartortle", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "water", 1.6f, 85.5f, "Blastoise", writesPerPokemon, isTraining);
				
				WritePokemon(bufferedWriter, rand, "bug", 0.3f, 2.9f, "Caterpie", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "bug", 0.7f, 9.9f, "Metapod", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "bug", 1.1f, 32.0f, "Butterfree", writesPerPokemon, isTraining);
				
				WritePokemon(bufferedWriter, rand, "bug", 0.3f, 3.2f, "Weedle", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "bug", 0.6f, 10.0f, "Kakuna", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "bug", 1.0f, 29.5f, "Beedrill", writesPerPokemon, isTraining);
				
				WritePokemon(bufferedWriter, rand, "flying", 0.3f, 4.0f, "Pidgey", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "flying", 1.1f, 30.0f, "Pidgeotto", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "flying", 1.5f, 39.5f, "Pidgeot", writesPerPokemon, isTraining);
				
				WritePokemon(bufferedWriter, rand, "normal", 0.3f, 3.5f, "Rattata", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "normal", 0.7f, 18.5f, "Raticate", writesPerPokemon, isTraining);
				
				WritePokemon(bufferedWriter, rand, "flying", 0.3f, 2.0f, "Spearow", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "flying", 1.2f, 38.0f, "Fearow", writesPerPokemon, isTraining);
				
				WritePokemon(bufferedWriter, rand, "poison", 2.0f, 6.9f, "Ekans", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "poison", 3.5f, 65.0f, "Arbok", writesPerPokemon, isTraining);
				
				WritePokemon(bufferedWriter, rand, "electric", 0.4f, 6.0f, "Pikachu", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "electric", 0.8f, 30.0f, "Raichu", writesPerPokemon, isTraining);
				
				WritePokemon(bufferedWriter, rand, "ground", 0.6f, 12.0f, "Sandshrew", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "ground", 1.0f, 29.5f, "Sandslash", writesPerPokemon, isTraining);
				
				WritePokemon(bufferedWriter, rand, "poison", 0.4f, 7.0f, "NidoranF", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "poison", 0.8f, 20.0f, "Nidorina", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "poison", 1.3f, 60.0f, "Nidoqueen", writesPerPokemon, isTraining);
				
				WritePokemon(bufferedWriter, rand, "poison", 0.5f, 9.0f, "NidoranM", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "poison", 0.9f, 19.5f, "Nidorino", writesPerPokemon, isTraining);
				WritePokemon(bufferedWriter, rand, "poison", 1.4f, 62.0f, "Nidoking", writesPerPokemon, isTraining);
				
				bufferedWriter.close();
				if (showOutput)
					System.out.println("Finished writing to file " + file.getName());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void WriteLine(BufferedWriter bufferedWriter, String line) throws IOException {
		bufferedWriter.write(line);
		bufferedWriter.newLine();
	}
	
	private static void WritePokemon(BufferedWriter bufferedWriter, Random rand, String type, 
	float height, float weight, String name, int numWrites, boolean training) throws IOException {
		for (int i = 0; i < numWrites; i++) {
			float std = .1f;
			
			float randomHeight = (float) (height + (rand.nextGaussian() * height * std));
			float randomWeight = (float) (weight + (rand.nextGaussian() * weight * std));
			
			if (randomHeight > maxPokemonHeight)
				maxPokemonHeight = randomHeight;
			if (randomWeight > maxPokemonWeight)
				maxPokemonWeight = randomWeight;
			
			WriteLine(bufferedWriter, type + "," + round(randomHeight, 2) + "," + 
					round(randomWeight, 2) + "," + name);
			
			if (training)
				trainingDataCount++;
		}
	}
	
	private static float round(float d, int decimalPlace) {
	    BigDecimal bd = new BigDecimal(Float.toString(d));
	    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
	    return bd.floatValue();
	}
}

package com.starflask.JavaNESBrain;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.UIManager;

import jp.tanakh.bjne.nes.Cpu;
import jp.tanakh.bjne.nes.Nes;
import jp.tanakh.bjne.ui.BJNEmulator;

import com.starflask.JavaNESBrain.data.BrainInfoWindow;
import com.starflask.JavaNESBrain.data.GameDataManager;
import com.starflask.JavaNESBrain.evolution.Gene;
import com.starflask.JavaNESBrain.evolution.GenePool;
import com.starflask.JavaNESBrain.evolution.Genome;
import com.starflask.JavaNESBrain.evolution.NeuralNetwork;
import com.starflask.JavaNESBrain.evolution.Neuron;
import com.starflask.JavaNESBrain.evolution.Species;
import com.starflask.JavaNESBrain.utils.FastMath;

/**
 * //use this as vm arg:      -Dsun.java2d.opengl=True 
 * 
 * This basically..
 * 
 * reads bytes from memory to build array of the 'world blocks' build and
 * mutates genes and neurons sends commands out through a virtual gamepad when
 * the tile bytes and neurons match
 * 
 * 
 */


/**
 * 
 * BUG: Every species only has a single genome..?
 * 

 */

public class SuperBrain {

	VirtualGamePad gamepad = new VirtualGamePad();

	BJNEmulator emulator;

	GameDataManager gameData;

	public SuperBrain() {

		emulator = new BJNEmulator( "" );

		// emulator.run(); do not run continuously.. this class updates each
		// frame manually

		start();
	}

	public static void main(String[] args) throws IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Could not set system look and feel. Meh.");
		}

		SuperBrain brain = new SuperBrain();

	}

	
	boolean firstUpdateOccured = false;
	public void start() {
		
		gameData = new GameDataManager(this);

		 

		while (true) {

		 
			if(getCPU()!=null && emulator.isAiEnabled())
			{			
			 update();   
			 
			 
			}
			 
			
			
			emulator.stepEmulation(); 
			
		}

	}

	int TimeoutConstant = 20;

	public static final int MaxNodes = 1000000;

	GenePool pool;

	BrainInfoWindow infoWindow;
	
	int marioPreviousDistanceRight = 0;
		
	private void update() {
		
		
		if(!firstUpdateOccured)
		{
			firstUpdateOccured = true;
			
			initializePool();
			
			 
			emulator.setGamepadInput( gamepad.getIntegerBuffer() );
			
			infoWindow = new BrainInfoWindow( gamepad , getGameDataManager() , pool );
			
		}

		Species species = pool.getCurrentSpecies();
		Genome genome = pool.getCurrentGenome();

		/*
		 * if forms.ischecked(showNetwork) then displayGenome(genome) end
		 */

		if (pool.getCurrentFrame() % 5 == 0) {
			evaluateCurrent();
		}

	//	emulator.setControllers(getController(), getController());

		getGameDataManager().getPositions();

		
		int marioDistanceRight = getGameDataManager().getMarioPos().getX();
		
		int marioDistanceDelta = marioDistanceRight - runStartingMarioX;
		 		 
		marioPreviousDistanceRight = marioDistanceRight;
				
		// if mario gets farther than he has ever been... make the timeout longer (this may need to be changed to delta style)
		if (marioDistanceRight > rightmost) {
			rightmost =  marioDistanceRight ;
			timeout = TimeoutConstant;
		}

		timeout = timeout - 1;

		int timeoutBonus = pool.getCurrentFrame() / 4;

		
		/**
		 * Since we dont have savestates with this emulator, fitness is determined by the increase in distance
		 * 
		 */
		if (timeout + timeoutBonus <= 0) {
			 
			
			int fitness = 25 +  marioDistanceDelta - pool.getCurrentFrame() / 2;
			//if (getRomName().equals("Super Mario World (USA)") && rightmost > 4816) {
			//	fitness = fitness + 1000;
			//}

			if (getRomName().startsWith("Super Mario Bros.") && marioDistanceRight > 3186) {
				fitness = fitness + 1000;
			}
			
			//the amount that this genome contributed to the fitness
			 
			
			if (fitness == 0) {
				//make sure we mark that we did actually measure fitness so we can move on
				fitness = -1;
			}
			
			genome.setFitness(fitness);
			
		 
			if (fitness > pool.getMaxFitness()) {
				pool.setMaxFitness(fitness);
				
				//save the genome since it is good
				// forms.settext(maxFitnessLabel, "Max Fitness: " ..
				// math.floor(pool.maxFitness))
				// writeFile("backup." .. pool.generation .. "." ..
				// forms.gettext(saveLoadFile))
			}

			infoWindow.outputScreen();



			pool.setCurrentSpecies(0);
			pool.setCurrentGenome(0);

			while ( pool.getCurrentSpecies().getGenomes().isEmpty() || fitnessAlreadyMeasured() ) {
				nextGenome();
			 
			}

			initializeRun();
		}

	/*	int measured = 0;
		int total = 0;

		// for every genome in every species increment total and if fitness is
		// not zero then increment measured

		for (Species s : pool.getSpecies()) {
			for (Genome g : s.getGenomes()) {
				total++;
				if (g.getFitness() != 0) {
					measured++;
				}

			}
		}*/

	

		pool.setCurrentFrame(pool.getCurrentFrame() + 1);

		
		

	}

	private void nextGenome() {
		pool.setCurrentGenome(pool.getCurrentGenomeIndex() + 1);
		if (pool.getCurrentGenomeIndex() >= pool.getCurrentSpecies().getGenomes().size()) {
			pool.setCurrentGenome(0);
			pool.setCurrentSpecies(pool.getCurrentSpeciesIndex() + 1);

			if (pool.getCurrentSpeciesIndex() >= pool.getSpecies().size()) {
				pool.newGeneration();
				pool.setCurrentSpecies(0);
			}
		}

	}

	private boolean fitnessAlreadyMeasured() {

		return pool.getCurrentGenome().getFitness() != 0;
	}

	private GameDataManager getGameDataManager() {

		return gameData;
	}

	public void initializePool() {
		
		//getNES().saveSram("run");
		
		pool = new GenePool(gameData);

		initializeRun();

	}

	int timeout;
	int rightmost = 0; // the most right that we ever got so far
	
	int runStartingMarioX;

	public void initializeRun() {
		
		//getNES().loadSram("run");
		 
		runStartingMarioX= getGameDataManager().getMarioPos().getX();
		///rightmost = 0;
		
		pool.setCurrentFrame(0);
		timeout = TimeoutConstant; //reset the timer for the next genome
		
		gamepad.clear();

		Species species = pool.getCurrentSpecies();
		Genome genome = pool.getCurrentGenome();
		generateNetwork(genome);
		evaluateCurrent();

	}

	

	public void evaluateCurrent() {
		Species species = pool.getCurrentSpecies();
		Genome genome = pool.getCurrentGenome();

		HashMap<String, Boolean> gamePadOutputs = evaluateNetwork(genome.getNetwork(), getGameDataManager()
				.getBrainSystemInputs());

		if(gamePadOutputs!=null)
		{
		
		// if left and right are pressed at once, dont press either.. same with
		// up and down
		if (gamePadOutputs.containsKey("P1 Left") && gamePadOutputs.get("P1 Left")
				&& gamePadOutputs.containsKey("P1 Right") && gamePadOutputs.get("P1 Right")) {
			gamePadOutputs.put("P1 Left", false);
			gamePadOutputs.put("P1 Right", false);
		}

		if (gamePadOutputs.containsKey("P1 Up")  && gamePadOutputs.get("P1 Up")
				&& gamePadOutputs.containsKey("P1 Down")  && gamePadOutputs.get("P1 Down")) {
			gamePadOutputs.put("P1 Up", false);
			gamePadOutputs.put("P1 Down", false);
		}

		gamepad.setOutputs(gamePadOutputs);

		}
		
	}

	/*
	 * Thsi explains how neurons fit into genes and etc..
	 */
	private void generateNetwork(Genome genome) {
		NeuralNetwork network = new NeuralNetwork();

		for (int i = 0; i < getGameDataManager().getNumInputs(); i++) {
			network.getNeurons().put(i, new Neuron());
		}

		for (int o = 0; o < getGameDataManager().getNumOutputs(); o++) {
			network.getNeurons().put(MaxNodes + o, new Neuron());

		}

		Collections.sort(genome.getGenes(), new Comparator<Gene>() {

			@Override
			public int compare(Gene g1, Gene g2) {

				return g1.getNeuralOutIndex() < g2.getNeuralOutIndex() ? -1 : (g1.getNeuralOutIndex() == g2
						.getNeuralOutIndex() ? 0 : 1);

			}

			// sort by the out number
			// table.sort(genome.genes, function (a,b)
			// return (a.out < b.out)
			// end
		});

		for (int i = 0; i < genome.getGenes().size(); i++) {
			Gene gene = genome.getGenes().get(i);
			if (gene.isEnabled()) {
				if (network.getNeurons().get(gene.getNeuralOutIndex()) == null) {
					network.getNeurons().put(gene.getNeuralOutIndex(), new Neuron());
				}

				Neuron neuron = network.getNeurons().get(gene.getNeuralOutIndex());
				neuron.getIncomingGeneList().add(gene);

				if (network.getNeurons().get(gene.getNeuralInIndex()) == null)
					network.getNeurons().put(gene.getNeuralInIndex(), new Neuron());
			}
		}

		genome.setNetwork(network);
	}

	/**
	 * Input is the neural network and number of inputs, output is the current
	 * gamepad button-press states
	 * 
	 *  
	 * 
	 * 
	 */
	private HashMap<String, Boolean> evaluateNetwork(NeuralNetwork network, List<Integer> inputList) {
 
		

		inputList.add(1);

		if (inputList.size() != this.getGameDataManager().getNumInputs()) {
			System.err.println("Incorrect number of neural network inputs. " + inputList.size() + " vs " + this.getGameDataManager().getNumInputs() );
			return null;
		}

		for (int i = 0; i < this.getGameDataManager().getNumInputs(); i++) {	
			if(network.getNeurons().containsKey( i ))
			{
				network.getNeurons().get(i).setValue(inputList.get(i));
			}else{
				System.err.println(  "no neuron at " + i   );
			}
		}

		//for every neuron, sum up the values of its incoming genes and set its own value to that
		for (Neuron neuron : network.getNeurons().values()) {
			float sum = 0;

			for (int j = 0; j < neuron.getIncomingGeneList().size(); j++) {
				Gene incoming = neuron.getIncomingGeneList().get(j);
				Neuron other = network.getNeurons().get(incoming.getNeuralInIndex());
				sum = sum + incoming.getWeight() * other.getValue();
					 
			}

			if (neuron.getIncomingGeneList().size() > 0) { 
				neuron.setValue(sigmoid(sum));
			}
		}

		HashMap<String, Boolean> gamepadOutputs = new HashMap<String, Boolean>();

		
		for (int o = 0; o < this.getGameDataManager().getNumOutputs(); o++) {
			
			String button = "P1 " + this.getGameDataManager().getButtonNames()[o];

			if (network.getNeurons().get(MaxNodes + o).getValue() > 0) {
				gamepadOutputs.put(button, true);
			} else {
				gamepadOutputs.put(button, false);
			}

		}

		return gamepadOutputs;
	}

	public String getRomName() {
		if (emulator.getCurrentRomName() == null) {
			return "none";
		}

		return emulator.getCurrentRomName();
	}

	 
	public void saveGenePoolToFile() throws Exception
	{
		FileWriter file = new FileWriter("c:\\test.json");
		file.write(pool.getAsJson().toJSONString());
		file.flush();
		file.close();
		
	}

	public static float sigmoid(float sum) {
		return 2 / (1 + FastMath.exp(-4.9f * sum)) - 1;
	}

	//if pool == nil then initializePool() end

	public Cpu getCPU()
	{
		if(getNES()!=null)
		{
			return getNES().getCpu();
		}
		return null;
	}
	
	private Nes getNES() {
		
		return emulator.getNES();
	}

}

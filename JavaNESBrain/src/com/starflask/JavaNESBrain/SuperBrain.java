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
import jp.tanakh.bjne.nes.ROMEventListener;
import jp.tanakh.bjne.ui.BJNEmulator;

import com.starflask.JavaNESBrain.data.BrainInfoWindow;
import com.starflask.JavaNESBrain.data.GalagaGameDataManager;
import com.starflask.JavaNESBrain.data.GameDataManager;
import com.starflask.JavaNESBrain.data.MarioGameDataManager;
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
 * BUG: loading the state sometimes messes with the tile pattern ?
 * 
 * why doesnt the environment draw nicely  in the debug window?
 * 

 */

public class SuperBrain implements Runnable, ROMEventListener {

	VirtualGamePad gamepad = new VirtualGamePad();

	BJNEmulator emulator;

	GameDataManager gameData;

	public SuperBrain(BJNEmulator emulator) {

		this.emulator=emulator;
		
		emulator.addROMEventListener(this);

		  
	}
 
	boolean built = false;
	boolean firstUpdateOccured = false;
	public void build() {
		
		 
		 loadGameDataManager();
		 System.out.println(" GD " + gameData);
		 
		 built = true;
		 
		
	}

	@Override
	public void run() {
		
		while(true)
		{
			if (built) {
			
		 
				if(getCPU()!=null  )
				{			
					update(); 
				}
			
			 
			
			}
		}
		
	}



	public static final int MaxNodes = 1000000;

	GenePool pool;

	BrainInfoWindow infoWindow;
	 
		
	private void update() {
		
		
		if(!firstUpdateOccured)
		{
			firstUpdateOccured = true;
			
			initializePool();
			
			 
			emulator.setGamepadInput( gamepad.getIntegerBuffer() );
			
			infoWindow = new BrainInfoWindow( gamepad , gameData , pool );
			
		}

		Species species = pool.getCurrentSpecies();
		Genome genome = pool.getCurrentGenome();

		/*
		 * if forms.ischecked(showNetwork) then displayGenome(genome) end
		 */ 
		if (pool.getCurrentFrame() % 5 == 0) {
			evaluateCurrent();
		

	//	emulator.setControllers(getController(), getController());
		getGameDataManager().siphonData();

	 
		boolean giveUp = getGameDataManager().updateGiveUpTimer(   );
		
			int fitness = getGameDataManager().getCurrentFitness() ;
			
			if (fitness == 0) {
				fitness = -1;
			}
			genome.setFitness(fitness);

			if ( giveUp ) {
				
			if (fitness > pool.getMaxFitness()) {
				pool.setMaxFitness(fitness);
				// forms.settext(maxFitnessLabel, "Max Fitness: " ..
				// math.floor(pool.maxFitness))
				// writeFile("backup." .. pool.generation .. "." ..
				// forms.gettext(saveLoadFile))
			}

			



			pool.setCurrentSpecies(0);
			pool.setCurrentGenome(0);

			while ( pool.getCurrentSpecies().getGenomes().isEmpty() || fitnessAlreadyMeasured() ) {
				nextGenome();
			}

			initializeRun();
		}
		
		infoWindow.update();
		
		
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

	 
		pool.setCurrentFrame( getRunFrameCount() );

		
		

	}

	private int getRunFrameCount() {
		return (int) (getNES().getFrameCount() - runInitFrameCount);
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
		
		getNES().saveState( 0 );
		
		getNES().audioEnabled = false;
		
		pool = new GenePool(gameData);

		initializeRun();

	}
	
	long runInitFrameCount = 0;
	
	public void initializeRun() {
		
		 getNES().requestLoadState( 0 );
			
		 
		
		 // resets the game
		 
		runInitFrameCount = getNES().getFrameCount();
		
		pool.setCurrentFrame(0);
		
		
		getGameDataManager().initializeRun();
		
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
				
				//if(o == 0){System.out.println(" button press " + button);}
				
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
		//FileWriter file = new FileWriter("c:\\test.json");
		//file.write(pool.getAsJson().toJSONString());
		//file.flush();
		//file.close();
		
	}

	public static float sigmoid(float sum) {
		return 2 / (1 + FastMath.exp(-4.9f * sum)) - 1;
	}

	 

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

	public GenePool getPool() {
		return pool;
	}

	@Override
	public void onLoad() {
		loadGameDataManager();
		
	}

	private void loadGameDataManager() {
		if (getRomName().startsWith("Super Mario Bros."))
		{
			gameData = new MarioGameDataManager(this);
		}else{
			gameData = new GalagaGameDataManager(this);
		}
		
	}

	
}

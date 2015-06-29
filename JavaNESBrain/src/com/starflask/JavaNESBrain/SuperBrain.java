package com.starflask.JavaNESBrain;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIManager;

import com.grapeshot.halfnes.CPURAM;
import com.grapeshot.halfnes.NES;
import com.grapeshot.halfnes.ui.ControllerInterface;
import com.starflask.JavaNESBrain.evolution.Gene;
import com.starflask.JavaNESBrain.evolution.GenePool;
import com.starflask.JavaNESBrain.evolution.Genome;
import com.starflask.JavaNESBrain.evolution.NeuralNetwork;
import com.starflask.JavaNESBrain.evolution.Neuron;
import com.starflask.JavaNESBrain.evolution.Species;
import com.starflask.JavaNESBrain.utils.FastMath;

/**
 * //use this as vm arg   -Djava.library.path=natives
 * 
 * This basically..
 * 
 * reads bytes from memory to build array of the 'world blocks' 
 * build and mutates genes and neurons
 * sends commands out through a virtual gamepad when the tile bytes and neurons match
 * 
 * 
 */


	
public class SuperBrain  {

	VirtualGamePad gamepad;
	
	NES emulator;
	
	GameDataManager gameData;
	
	public SuperBrain() {
		
		 emulator = new  NES();
		 
       // emulator.run();  do not run continuously.. this class updates each frame manually
        
        
        
        start();
	}
	
	

	   public static void main(String[] args) throws IOException {
	        try {
	            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	        } catch (Exception e) {
	            System.err.println("Could not set system look and feel. Meh.");
	        }
	        
	        
	        SuperBrain brain = new SuperBrain(  ); 
	        
	        

	    }
	   
	   

	public ControllerInterface getController() { 
		return gamepad;
	}
	
 
	public void start()
	{
		gameData = new GameDataManager(this);
		
		initializePool();
		
		while(true)
		{

				update();
			
		}
		
	}
	
	
	
	
	
	int Population = 300;
	float DeltaDisjoint = 2.0f;
	float DeltaWeights = 0.4f;
	float DeltaThreshold = 1.0f;

	int StaleSpecies = 15;

	

	int TimeoutConstant = 20;

	int MaxNodes = 1000000;
	
	
	

	GenePool pool;


	private void update() {
 
		 
		        Species species = pool.getCurrentSpecies();
		        Genome genome =  pool.getCurrentGenome(); 
		       
		      /*  if forms.ischecked(showNetwork) then
		                displayGenome(genome)
		        end*/
		       
		        if (pool.getCurrentFrame() % 5 == 0 )
		        {
		                evaluateCurrent();
		        	}
		 
		         
		        
		        emulator.setControllers( getController() , null);
		        
		 
		        getGameDataManager().getPositions();
		        
		        //if mario gets farther than he has ever been...
		        if (getGameDataManager().getMarioPos().getX() > rightmost)
		        {
		                rightmost = (int) getGameDataManager().getMarioPos().getX() ;
		                timeout = TimeoutConstant ;
		        }
		       
		        timeout = timeout - 1 ; 		       
		       
		        int timeoutBonus = pool.getCurrentFrame() / 4 ;
		        
		        if (timeout + timeoutBonus <= 0) {
		        	
		                int fitness = rightmost - pool.getCurrentFrame() / 2 ;		                
		                if ( getRomName().equals("Super Mario World (USA)") && rightmost > 4816 )
		                {
		                        fitness = fitness + 1000;
		                }
		                
		                if ( getRomName().equals( "Super Mario Bros.") && rightmost > 3186 )
		                {
		                        fitness = fitness + 1000;
		                }
		                
		                if (fitness == 0 )
		                {
		                     fitness = -1;
		                }
		                genome.setFitness(fitness);
		               
		                if (fitness > pool.getMaxFitness()) 
		                {
		                        pool.setMaxFitness(fitness);
		                     //   forms.settext(maxFitnessLabel, "Max Fitness: " .. math.floor(pool.maxFitness))
		                      //  writeFile("backup." .. pool.generation .. "." .. forms.gettext(saveLoadFile))
		                }
		               		    
		                System.out.println( "Gen " + pool.getGeneration() + " species " + pool.getCurrentSpecies() + " genome " + pool.getCurrentGenome() + " fitness: " + fitness );
		                
		                pool.setCurrentSpecies(1);
		                pool.setCurrentGenome(1);
		                
		                while (fitnessAlreadyMeasured())
		                {
		                        nextGenome();
		                }
		                
		                initializeRun();
		        }
		 
		        
		        
		        
		        int measured = 0;
		        int total = 0;
		        
		        //for every genome in every species increment total and  if fitness is not zero then increment measured
		        
		        for(Species s : pool.getSpecies())
		        {
		        	for(Genome g : s.getGenomes() )
		        	{
		        		total ++;
		        		if(g.getFitness() != 0)
		        		{
		        			measured++;
		        		}
		        		
		        		
		        	}
		        }
		        
		        
		        /*
		        if (! forms.ischecked(hideBanner)) {
		                gui.drawText(0, 0, "Gen " .. pool.generation .. " species " .. pool.currentSpecies .. " genome " .. pool.currentGenome .. " (" .. math.floor(measured/total*100) .. "%)", 0xFF000000, 11)
		                gui.drawText(0, 12, "Fitness: " .. math.floor(rightmost - (pool.currentFrame) / 2 - (timeout + timeoutBonus)*2/3), 0xFF000000, 11)
		                gui.drawText(100, 12, "Max Fitness: " .. math.floor(pool.maxFitness), 0xFF000000, 11)
		        }*/
		        
		               
		        pool.setCurrentFrame(pool.getCurrentFrame()+1);
		 
		        
		        
		        emulator.update();
		                		
		                		
		
	}

	
	private void nextGenome() {
	 		pool.setCurrentGenome(pool.getCurrentGenomeIndex() + 1);
		        if (pool.getCurrentGenomeIndex() >  pool.getCurrentSpecies().getGenomes().size() ) 
		        {
		                pool.setCurrentGenome(1);
		                pool.setCurrentSpecies(pool.getCurrentSpeciesIndex() + 1);
		                
		                if (pool.getCurrentSpeciesIndex() > pool.getSpecies().size() ){
		                        pool.newGeneration() ;
		                        pool.setCurrentSpecies(  1);
		                }
		        }
		
	}




private boolean fitnessAlreadyMeasured()
{
   
    return pool.getCurrentGenome().getFitness() != 0 ;
}
	
private GameDataManager getGameDataManager() {
		 
		return gameData;
	}

 

public void initializePool()
{
pool = new GenePool( gameData );

for (int i=1; i < Population ; i++ )
{
        Genome basic = pool.createBasicGenome();
        pool.addToSpecies(basic) ;
}

initializeRun();

}




int timeout;
int rightmost = 0; // the most right that we ever got so far

public void  initializeRun()
{
	
// savestate.load(Filename);   //cannot do this with halfnes yet :/

rightmost = 0 ; 
pool.setCurrentFrame(0) ;
timeout = TimeoutConstant ; 
clearJoypad(); 

Species species = pool.getCurrentSpecies() ;
Genome genome = pool.getCurrentGenome() ; 
generateNetwork(genome) ;
evaluateCurrent();
 
}


public void evaluateCurrent()
{
	Species species = pool.getCurrentSpecies() ;
	Genome genome = pool.getCurrentGenome() ; 

 
	controller = evaluateNetwork(genome.getNetwork(), getGameDataManager().getBrainSystemInputs() ) ;

if (controller["P1 Left"] && controller["P1 Right"])
{
        controller["P1 Left"] = false;
        controller["P1 Right"] = false;
}
if (controller["P1 Up"] && controller["P1 Down"] )
{
        controller["P1 Up"] = false;
        controller["P1 Down"] = false;
}

joypad.set(controller);

}

/*
 * Thsi explains how neurons fit into genes and etc..
 * 
 */
private void generateNetwork(Genome genome)
{
        NeuralNetwork network = new NeuralNetwork();
         
       
        for (int i=1;i< getGameDataManager().getNumInputs(); i++ ) 
        {              
                network.getNeurons().add(i, new Neuron() );
        }
       
        for (int o=1;o< getGameDataManager().getNumOutputs(); o++ ) 
        {
        	  network.getNeurons().add(MaxNodes+o, new Neuron() );
               
        }
        
       //sort by the out number ? 
        table.sort(genome.genes, function (a,b)
                return (a.out < b.out)
        end
        
        
        
        for (int i=1; i < genome.getGenes().size() ; i++ )
        {
                Gene gene = genome.getGenes().get(i);
                if (gene.isEnabled()) 
                {
                        if (network.getNeurons().get( gene.getNeuralOutIndex() )  == null )
                        {                        	
                                network.getNeurons().add(gene.getNeuralOutIndex(), new Neuron() );                                 		
                        }
                        
                        Neuron neuron = network.getNeurons().get( gene.getNeuralOutIndex() );                        
                        neuron.getIncomingGeneList().add(gene);
                         
                        if (network.getNeurons().get( gene.getNeuralInIndex() )  == null )
                        	 network.getNeurons().add(gene.getNeuralInIndex(), new Neuron() );                                   
                        }
                }
		
	
       
        genome.setNetwork(network);
}
 

/**
 * Input is the neural network and number of inputs, output is the current gamepad button-press states
 * 
 */
private void evaluateNetwork(NeuralNetwork network, Integer[] inputs)
{
		
		List<Integer> inputList = Arrays.asList(inputs);
	
		inputList.add(1);
		
        
        if (inputList.size() != this.getGameDataManager().getNumInputs()) 
        {
                System.err.println("Incorrect number of neural network inputs.") ;
                return null ;
        }
       
        for (int i=1; i < this.getGameDataManager().getNumInputs(); i++){
                network.getNeurons().get(i).setValue(inputList.get(i))  ;
        }
       
        for (Neuron neuron : network.getNeurons())
        {
                float sum = 0;
                
                for (int j = 1; j < neuron.getIncomingGeneList().size() ; j++ ) 
                {
                        local incoming = neuron.incoming[j] ; 
                        local other = network.neurons[incoming.into]; 
                        sum = sum + incoming.weight * other.value;
                }
               
                if(neuron.getIncomingGeneList().size() > 0) {
                        neuron.setValue( sigmoid(sum) );
                }
		}
       
        local outputs = {}
        for (int o = 1; o < this.getGameDataManager().getNumOutputs() ; o ++){
                local button = "P1 " .. ButtonNames[o]
                if (network.neurons[MaxNodes+o].value > 0) 
                {
                        outputs[button] = true;
                }else{
                        outputs[button] = false;
                }
        }
       
        return outputs  ;
}















	public String getRomName()
	{
		if(emulator.getCurrentRomName() == null)
		{
			return "none";
		}
		
		return emulator.getCurrentRomName();
	}
	
	 
	public CPURAM getRAM() {
		 
		return emulator.getCPURAM();
	}
	

	
	

	
public static float sigmoid(float sum)
{
	return 2/(1+FastMath.exp(-4.9f*sum))-1 ;
}



	/*
	 * 
	 * 
	 *
	 * 
	 * if pool == nil then
initializePool()
end

 
 
function crossover(g1, g2)
        -- Make sure g1 is the higher fitness genome
        if g2.fitness > g1.fitness then
                tempg = g1
                g1 = g2
                g2 = tempg
        end
 
        local child = newGenome()
       
        local innovations2 = {}
        for i=1,#g2.genes do
                local gene = g2.genes[i]
                innovations2[gene.innovation] = gene
        end
       
        for i=1,#g1.genes do
                local gene1 = g1.genes[i]
                local gene2 = innovations2[gene1.innovation]
                if gene2 ~= nil and math.random(2) == 1 and gene2.enabled then
                        table.insert(child.genes, copyGene(gene2))
                else
                        table.insert(child.genes, copyGene(gene1))
                end
        end
       
        child.maxneuron = math.max(g1.maxneuron,g2.maxneuron)
       
        for mutation,rate in pairs(g1.mutationRates) do
                child.mutationRates[mutation] = rate
        end
       
        return child
end
 

 


 
function disjoint(genes1, genes2)
        local i1 = {}
        for i = 1,#genes1 do
                local gene = genes1[i]
                i1[gene.innovation] = true
        end
 
        local i2 = {}
        for i = 1,#genes2 do
                local gene = genes2[i]
                i2[gene.innovation] = true
        end
       
        local disjointGenes = 0
        for i = 1,#genes1 do
                local gene = genes1[i]
                if not i2[gene.innovation] then
                        disjointGenes = disjointGenes+1
                end
        end
       
        for i = 1,#genes2 do
                local gene = genes2[i]
                if not i1[gene.innovation] then
                        disjointGenes = disjointGenes+1
                end
        end
       
        local n = math.max(#genes1, #genes2)
       
        return disjointGenes / n
end
 
function weights(genes1, genes2)
        local i2 = {}
        for i = 1,#genes2 do
                local gene = genes2[i]
                i2[gene.innovation] = gene
        end
 
        local sum = 0
        local coincident = 0
        for i = 1,#genes1 do
                local gene = genes1[i]
                if i2[gene.innovation] ~= nil then
                        local gene2 = i2[gene.innovation]
                        sum = sum + math.abs(gene.weight - gene2.weight)
                        coincident = coincident + 1
                end
        end
       
        return sum / coincident
end
       
function sameSpecies(genome1, genome2)
        local dd = DeltaDisjoint*disjoint(genome1.genes, genome2.genes)
        local dw = DeltaWeights*weights(genome1.genes, genome2.genes)
        return dd + dw < DeltaThreshold
end
 
function rankGlobally()
        local global = {}
        for s = 1,#pool.species do
                local species = pool.species[s]
                for g = 1,#species.genomes do
                        table.insert(global, species.genomes[g])
                end
        end
        table.sort(global, function (a,b)
                return (a.fitness < b.fitness)
        end)
       
        for g=1,#global do
                global[g].globalRank = g
        end
end
 
function calculateAverageFitness(species)
        local total = 0
       
        for g=1,#species.genomes do
                local genome = species.genomes[g]
                total = total + genome.globalRank
        end
       
        species.averageFitness = total / #species.genomes
end
 
function totalAverageFitness()
        local total = 0
        for s = 1,#pool.species do
                local species = pool.species[s]
                total = total + species.averageFitness
        end
 
        return total
end
  
  
  

	 * 
	 */



}

package com.starflask.JavaNESBrain.evolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.starflask.JavaNESBrain.GameDataManager;
import com.starflask.JavaNESBrain.local;
import com.starflask.JavaNESBrain.utils.FastMath;

public class GenePool {

	List<Species> species = new ArrayList<Species>();
	private int generation = 0;
	int innovation = 10; //becomes equal to numOutputs
	private int currentSpecies = 1;
	private int currentGenome = 1;
	int currentFrame = 0;
	private int maxFitness = 0;
	
	Random rand = new Random();
	
	GameDataManager gameDataManager;
	
	
	public GenePool(GameDataManager gameDataManager)
	{
		this.gameDataManager=gameDataManager;
	}
	
	public GameDataManager getGameDataManager() {
		return gameDataManager;
	}


	public int newInnovation()
	{		
		return ++innovation;
	}


	public  Genome createBasicGenome()
	{
        Genome genome = new Genome();
		innovation = 1;
 
        genome.maxneuron = 6 ;  //numInputs
        mutate(genome);
       
        return genome;
	}
	
	
	private void mutate(Genome genome)
	{		
    for (String key : genome.mutationRates.keySet()  ){
    	
    	float rate = genome.mutationRates.get(key);
    	
            if ( rand.nextBoolean() ){ //50 50 chance
                   genome.mutationRates.put(key,0.95f*rate ) ;
            }else{
            	   genome.mutationRates.put(key,1.05263f*rate ) ; 
            }
	}

    if (rand.nextFloat() < genome.mutationRates.get("connections")) {
            pointMutate(genome) ;
    }
   
    float p = genome.mutationRates.get("link");
    while (p > 0){
            if (rand.nextFloat() < p) {
                    linkMutate(genome, false);
            }
            p = p - 1;
    }

    p = genome.mutationRates.get("bias");
    while (p > 0) {
            if (rand.nextFloat() < p) {
                    linkMutate(genome, true);
            }
            p = p - 1;
    }
   
    p = genome.mutationRates.get("node");
    while (p > 0) {
            if (rand.nextFloat() < p) {
                    nodeMutate(genome);
            }
            p = p - 1;
    }
   
    p = genome.mutationRates.get("enable");
    while (p > 0) {
            if (rand.nextFloat() < p) {
                    enableDisableMutate(genome, true);
            }
            p = p - 1;
    }

    p = genome.mutationRates.get("disable");
    while (p > 0) {
            if (rand.nextFloat() < p) {
                    enableDisableMutate(genome, false);
            }
            p = p - 1;
    }
	}

	
	float PerturbChance = 0.90f;

private void pointMutate(Genome genome)
{
        float step = genome.mutationRates.get("step") ;
       
        for (int i=1; i <genome.genes.size(); i++ )
        {
                Gene gene = genome.genes.get(i) ;
                if (rand.nextFloat() < PerturbChance) { 
                        gene.weight = gene.weight + rand.nextFloat() * step*2 - step ;
                }else{
                        gene.weight = rand.nextFloat()*4-2 ;
		}
	}
}
 

	
	
private void linkMutate(Genome genome, boolean forceBias)
	{
    int neuron1Index = randomNeuronIndex(genome.genes, false) ; 
    int neuron2Index = randomNeuronIndex(genome.genes, true) ;
     
    Gene newLink = new Gene();
    
    //if the index of the neuron is less than num inputs then it must be an input-neuron.. i should change this architecture
    if (neuron1Index <= getGameDataManager().getNumInputs() && neuron2Index <= getGameDataManager().getNumInputs()) 
    {
            //--Both input nodes
            return;
    }
    
    if (neuron2Index <= getGameDataManager().getNumInputs()) 
    {
           // -- Swap output and input   -- probably should copy !
            int temp = neuron1Index;
            neuron1Index = neuron2Index;
            neuron2Index = temp;
    }

    newLink.into = neuron1Index ; 
    newLink.out = neuron2Index ;
    
    if (forceBias)
    	{
            newLink.into = getGameDataManager().getNumInputs();
    	}
   
    if (containsLink(genome.genes, newLink) )
    {
            return;
    }
            		
            		
    newLink.innovation = newInnovation();
    newLink.weight = rand.nextFloat()*4-2 ;
   
    genome.genes.add(newLink);

}

 
 
private boolean containsLink(List<Gene> genes, Gene link) {

	 for (Gene gene : genes) 
	 {          
         if (gene.into == link.into && gene.out == link.out) 
                 return true;
         
	 }
	 return false;
}


//what does this do?   What is a neuron supposed to be.. boolean? int? struct?
private int randomNeuronIndex(List<Gene> genes, boolean nonInput)
{
	
boolean[] neuronMatchesInputState = new boolean[];

//every neuron corresponds with a gamepad button 
if (! nonInput ){
        for (int i=1; i < getGameDataManager().getNumInputs() ; i++ ){
        	neuronMatchesInputState[i] = true;
	}
}

for (int o=1; o < getGameDataManager().getNumOutputs(); o++ ) {
	neuronMatchesInputState[MaxNodes+o] = true;
}

for (int i=1; i < genes.size(); i++){
        if ((! nonInput) || genes.get(i).into > getGameDataManager().getNumInputs()) {
        	neuronMatchesInputState[genes.get(i).into] = true;
        }
        if ((! nonInput) || genes.get(i).out > getGameDataManager().getNumInputs() ){
        	neuronMatchesInputState[genes.get(i).out] = true;
        }
}

int count = 0 ;


for _,_ in pairs(neurons) do
        count = count + 1
end
local n = math.random(1, count)

for k,v in pairs(neurons) do
        n = n-1
        if n == 0 then
                return k
        end
end


return 0;
		
}



private void nodeMutate(Genome genome)
{
    if (genome.genes.size() == 0 )
            return;
    

    genome.maxneuron = genome.maxneuron + 1;

    
    int randomIndex = rand.nextInt(genome.genes.size()-1)+1;  //dont ever pick the zeroeth gene since it is always blank?
    Gene gene = genome.genes.get( randomIndex ) ;
    if (!gene.isEnabled()) 
            return;
    
            		
            		
    gene.setEnabled(false);
   
    Gene gene1 = gene.copy();
    gene1.out = genome.maxneuron;
    gene1.weight = 1.0f;
    gene1.innovation = newInnovation();
    gene1.setEnabled(true);
    genome.genes.add(gene1);
   
    Gene gene2 = gene.copy();
    gene2.into = genome.maxneuron;
    gene2.innovation = newInnovation();
    gene2.setEnabled(true);
    genome.genes.add(gene2);
}
	

public void enableDisableMutate(Genome genome, boolean enable)
{
	List<Gene> candidates = new ArrayList<Gene>();

	//find the genes that are not this enablestate
	for (Gene gene : genome.genes) {
        if (gene.isEnabled() != enable)
        {
              candidates.add(gene);
        }
	}

	if (candidates.isEmpty())
	{
        return;
      }

		//flip the enablestate of a random candidate
		int randomIndex = rand.nextInt(candidates.size()-1 ) + 1;
		Gene gene = candidates.get(randomIndex) ;
		gene.setEnabled(! gene.isEnabled()) ;
	
	}


public int getCurrentFrame() {
	 
	return currentFrame;
}


public Species getCurrentSpecies() {
	 
	return species.get(currentSpecies);
}


public Genome getCurrentGenome() {
	 
	return species.get(currentSpecies).genomes.get(currentGenome);
}


public int getMaxFitness() {
	return maxFitness;
}


public void setMaxFitness(int maxFitness) {
	this.maxFitness = maxFitness;
}


public void setCurrentSpecies(int currentSpecies) {
	this.currentSpecies = currentSpecies;
}


public void setCurrentGenome(int currentGenome) {
	this.currentGenome = currentGenome;
}


public int getGeneration() {
	return generation;
}


public void setGeneration(int generation) {
	this.generation = generation;
}


public int getCurrentGenomeIndex() {
	 
	return currentGenome;
}


public int getCurrentSpeciesIndex() {
 
	return currentSpecies;
}


public List<Species> getSpecies() {
	 
	return species;
}


public void setCurrentFrame(int i) {
	 currentFrame = i;
	
}

public void cullSpecies(boolean cutToOne) { 
	
    for (int s = 1; s < getSpecies().size(); s++)
    {
            Species species = getSpecies().get(s);
           
            table.sort(species.genomes, function (a,b)
                    return (a.fitness > b.fitness)
            end)
           
            local remaining = math.ceil(#species.genomes/2)
            if cutToOne then
                    remaining = 1
            end
            while #species.genomes > remaining do
                    table.remove(species.genomes)
            end
    }

}



public void newGeneration() {

	 cullSpecies(false); // Cull the bottom half of each species
     rankGlobally();
     removeStaleSpecies();
     rankGlobally();
     
     for (int s = 1 ; s < pool.getSpecies().size(); s++)
     {
             Species species = pool.getSpecies().get(s);
             calculateAverageFitness(species) ;
     }
     
     removeWeakSpecies();
     int sum = totalAverageFitness();
     local children = {}
     for (int s = 1 ; s < pool.getSpecies().size(); s++)
    	 Species species = pool.getSpecies().get(s);
             breed = math.floor(species.averageFitness / sum * Population) - 1
             for i=1,breed do
                     table.insert(children, breedChild(species))
             end
     end
     
     cullSpecies(true); //-- Cull all but the top member of each species
            		 
     while #children + #pool.species < Population do
             local species = pool.species[math.random(1, #pool.species)]
             table.insert(children, breedChild(species))
     end
     
     for c=1,#children do
             local child = children[c]
             addToSpecies(child)
     end
    
     pool.generation = pool.generation + 1
    
   //  writeFile("backup." .. pool.generation .. "." .. forms.gettext(saveLoadFile))
		
	}

public void  addToSpecies(Genome child)
{
boolean foundSpecies = false;

for (int s=1; s < pool.getSpecies().size() ; s++ ){
        Species species = pool.getSpecies().get(s);
        if (! foundSpecies && sameSpecies(child, species.genomes[1])) 
        {
        	species.getGenomes().add(child);                 
            foundSpecies = true;
        }
}

	if (!foundSpecies )
	{
        Species childSpecies = new Species();
       
        table.insert(childSpecies.genomes, child)
        table.insert(pool.species, childSpecies)
	}
}









private Genome  breedChild(Species species){
	Genome child = new Genome();
        
        if (rand.nextFloat() < CrossoverChance )
        {        	
        		int index1 = rand.nextInt(species.getGenomes().size() - 1 ) + 1; 
                g1 = species.getGenomes().get(index1);
                int index2 = rand.nextInt(species.getGenomes().size() - 1 ) + 1; 
                g2 = species.getGenomes().get(index2);
                child = crossover(g1, g2);
        }else{
        		int index = rand.nextInt(species.getGenomes().size() - 1 ) + 1; 
        		g = species.getGenomes().get(index1);
                child = copyGenome(g);
        }
       
        mutate(child);
       
        return child;
}
 
private void removeStaleSpecies()
{
        local survived = {}
 
        for s = 1,#pool.species do
                local species = pool.species[s]
               
                table.sort(species.genomes, function (a,b)
                        return (a.fitness > b.fitness)
                end)
               
                if species.genomes[1].fitness > species.topFitness then
                        species.topFitness = species.genomes[1].fitness
                        species.staleness = 0
                else
                        species.staleness = species.staleness + 1
                end
                if species.staleness < StaleSpecies or species.topFitness >= pool.maxFitness then
                        table.insert(survived, species)
                end
        end
 
        pool.species = survived
}
 
private void removeWeakSpecies()
{
        local survived = {}
 
        local sum = totalAverageFitness()
        for s = 1,#pool.species do
                local species = pool.species[s]
                breed = math.floor(species.averageFitness / sum * Population)
                if breed >= 1 then
                        table.insert(survived, species)
                end
        end
 
        pool.species = survived
}
 


}

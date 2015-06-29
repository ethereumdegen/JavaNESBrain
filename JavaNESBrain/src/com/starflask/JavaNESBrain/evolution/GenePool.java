package com.starflask.JavaNESBrain.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.starflask.JavaNESBrain.utils.FastMath;

public class GenePool {

	List<Species> species = new ArrayList<Species>();
	int generation = 0;
	int innovation = 10; //numOutputs
	int currentSpecies = 1;
	int currentGenome = 1;
	int currentFrame = 0;
	int maxFitness = 0;
	
	Random rand = new Random();
	
	
	public GenePool()
	{
		
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

    p = genome.mutationRates["disable"];
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
 

	
	
private void linkMutate(Genome genome, float forceBias)
	{
    Neuron neuron1 = randomNeuron(genome.genes, false) ; 
    Neuron neuron2 = randomNeuron(genome.genes, true) ;
     
    local newLink = newGene()
    if neuron1 <= Inputs and neuron2 <= Inputs then
            //--Both input nodes
            return
    end
    if neuron2 <= Inputs then
           // -- Swap output and input
            local temp = neuron1
            neuron1 = neuron2
            neuron2 = temp
    end

    newLink.into = neuron1
    newLink.out = neuron2
    if forceBias then
            newLink.into = Inputs
    end
   
    if containsLink(genome.genes, newLink) then
            return
    end
    newLink.innovation = newInnovation()
    newLink.weight = math.random()*4-2
   
    table.insert(genome.genes, newLink)
}




private Neuron randomNeuron(genes, nonInput)
{
local neurons = {}
if not nonInput then
        for i=1,Inputs do
                neurons[i] = true
        end
end
for o=1,Outputs do
        neurons[MaxNodes+o] = true
end
for i=1,#genes do
        if (not nonInput) or genes[i].into > Inputs then
                neurons[genes[i].into] = true
        end
        if (not nonInput) or genes[i].out > Inputs then
                neurons[genes[i].out] = true
        end
end

local count = 0
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

return 0
		
}



private void nodeMutate(Genome genome)
{
    if (genome.genes.size() == 0 )
            return;
    

    genome.maxneuron = genome.maxneuron + 1;

    Gene gene = genome.genes[math.random(1,genome.genes.size())] ;
    if (!gene.enabled) 
            return;
    
            		
            		
    gene.enabled = false;
   
    Gene gene1 = gene.copy();
    gene1.out = genome.maxneuron;
    gene1.weight = 1.0f;
    gene1.innovation = newInnovation();
    gene1.enabled = true;
    genome.genes.add(gene1);
   
    Gene gene2 = gene.copy();
    gene2.into = genome.maxneuron;
    gene2.innovation = newInnovation();
    gene2.enabled = true;
    genome.genes.add(gene2);
}
	

public void enableDisableMutate(Genome genome, boolean enable)
{
local candidates = {}
for _,gene in pairs(genome.genes) do
        if gene.enabled == not enable then
                table.insert(candidates, gene)
        end
end

if #candidates == 0 then
        return
end

local gene = candidates[math.random(1,#candidates)]
gene.enabled = not gene.enabled
end
}


}

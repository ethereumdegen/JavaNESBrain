package com.starflask.JavaNESBrain.evolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Genome {
	
	List<Gene> genes = new ArrayList<Gene>();

	float MutateConnectionsChance = 0.25f;
	float PerturbChance = 0.90f;
	float CrossoverChance = 0.75f;
	float LinkMutationChance = 2.0f;
	float NodeMutationChance = 0.50f;
	float BiasMutationChance = 0.40f;
	float StepSize = 0.1f;
	float DisableMutationChance = 0.4f;
	float EnableMutationChance = 0.2f;
	
	int fitness;
	int adjustedFitness;
	List<Neuron> network = new ArrayList<Neuron>();  //??
	int maxneuron = 0;
	int globalRank = 0;
	
	
	HashMap<String,Float> mutationRates = new HashMap<String,Float>();

	public Genome()
	{
		
		 mutationRates.put("connections", MutateConnectionsChance);
		 mutationRates.put("link", LinkMutationChance);
		 mutationRates.put("bias", BiasMutationChance);
		 mutationRates.put("node", NodeMutationChance);
		 mutationRates.put("enable", EnableMutationChance);
		 mutationRates.put("disable", DisableMutationChance);
		 mutationRates.put("step", StepSize);
		 
		 
	}
	
	public static Genome copy(Genome otherGenome)
	{
		 Genome genome2 = new Genome();
		 
		 //deep copy all genes
		    for(Gene otherGene : otherGenome.genes)
		    {
		    	genome2.genes.add(otherGene.copy() );
		    }
		 
		 
			genome2.maxneuron = otherGenome.maxneuron;		
		    genome2.mutationRates.put("connections", otherGenome.mutationRates.get("connections"));		    
		    genome2.mutationRates.put("link", otherGenome.mutationRates.get("link"));
		    genome2.mutationRates.put("bias", otherGenome.mutationRates.get("bias"));
		    genome2.mutationRates.put("node", otherGenome.mutationRates.get("node"));
		    genome2.mutationRates.put("enable", otherGenome.mutationRates.get("enable"));
		    genome2.mutationRates.put("disable", otherGenome.mutationRates.get("disable"));
		    		
		   return genome2 ;
	}
	 
  
 
 
}

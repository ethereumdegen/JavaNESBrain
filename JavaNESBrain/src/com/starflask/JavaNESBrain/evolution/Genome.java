package com.starflask.JavaNESBrain.evolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.starflask.JavaNESBrain.utils.FastMath;

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
	NeuralNetwork network = new NeuralNetwork();   
	

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

	public void setFitness(int newFitness) {
		this.fitness=newFitness;
		
	}

	public int getFitness() {
		 
		return fitness;
	}
	 
	public NeuralNetwork getNetwork() {
		return network;
	}

	public void setNetwork(NeuralNetwork network) {
		this.network = network;
	}

	public List<Gene> getGenes() {
		 
		return genes;
	}

	public void setGlobalRank(int rank) {
		globalRank=rank;
	}

	public int getGlobalRank() {
		
		return globalRank;
	}
	
	
	float DeltaDisjoint = 2.0f;
	float DeltaWeights = 0.4f;
	float DeltaThreshold = 1.0f;
	
	
	public  boolean sameSpeciesAs(Genome otherGenome)
	{
		float dd = DeltaDisjoint*disjoint(this.genes, otherGenome.genes);
		 float dw = DeltaWeights*weights(this.genes, otherGenome.genes);
			 
		return dd + dw < DeltaThreshold ;
	}
	

private float  disjoint(List<Gene> genes1, List<Gene> genes2)
{
        
        HashMap<Integer,Boolean> innovativeGene1 = new HashMap<Integer,Boolean>();
        
        for (int i = 1; i < genes1.size(); i++ )
        {
                Gene gene = genes1.get(i);
                innovativeGene1.put(gene.innovation, true) ;
        }
 
        HashMap<Integer,Boolean> innovativeGene2 = new HashMap<Integer,Boolean>();
        for (int i = 1; i < genes2.size(); i++ )
        {
        	 Gene gene = genes2.get(i);
        	 innovativeGene2.put(gene.innovation, true) ;
        }
       
        float disjointGenes = 0 ;
        		
         for (int i = 1; i < genes1.size(); i++ )
         {
        	 Gene gene = genes1.get(i);
         
                if (!innovativeGene2.containsKey(  gene.innovation ) || ! innovativeGene2.get(gene.innovation) )
                {
                	disjointGenes++;
                }
		}
       
         for (int i = 1; i < genes2.size(); i++ )
         {
        	 Gene gene = genes2.get(i);
         
        	 if (!innovativeGene1.containsKey(  gene.innovation ) || ! innovativeGene1.get(gene.innovation) )
        		 disjointGenes++;
         
                
         }
        
        	
       
        int max =  genes1.size();
        if(genes2.size() > genes1.size())
        {
        	max =  genes2.size();
        }
      
       
        return disjointGenes / max ;
}
 
private float  weights( List<Gene> genes1, List<Gene> genes2 )
{
	
	
	  HashMap<Integer,Gene> innovativeGene2 = new HashMap<Integer,Gene>();
        for (int i = 1; i < genes2.size(); i++)
        {
                Gene gene = genes2.get(i);
                
                innovativeGene2.put(gene.innovation, gene);
               
        }
 
        float sum = 0;
        float coincident = 0;
        		
        for (int i = 1; i < genes1.size(); i++)
        {
                Gene gene = genes1.get(i);
        
                if (innovativeGene2.get(gene.innovation) != null )
                {
                        Gene gene2 = innovativeGene2.get(gene.innovation);
                        sum = sum + FastMath.abs(gene.getWeight() - gene2.getWeight());
                        coincident++;
                }
        }
       
        return sum / coincident  ;
}
	
 
 
}

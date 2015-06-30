package com.starflask.JavaNESBrain.evolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Species {

	int topFitness;
	int staleness;
	
	List<Genome> genomes = new ArrayList<Genome>();
	
	
	public Species()
	{
		
	}


	public List<Genome> getGenomes() {
		 
		return genomes;
	}

	
	int averageFitness; //does this have to be buffered and stored?

	public int getAverageFitness() {
		
		return averageFitness;
	}


	public void setAverageFitness(int f) {
		averageFitness = f;
	}

	@Override
	public String toString()
	{
		return "SPECIES"+this.hashCode();		
	}
	  
 
}

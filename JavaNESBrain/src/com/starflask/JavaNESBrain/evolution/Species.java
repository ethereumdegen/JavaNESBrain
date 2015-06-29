package com.starflask.JavaNESBrain.evolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Species {

	int topFitness;
	int staleness;
	int averageFitness;
	List<Genome> genomes = new ArrayList<Genome>();
	
	
	public Species()
	{
		
	}


	public Collection<Genome> getGenomes() {
		 
		return genomes;
	}

 
	  
 
}

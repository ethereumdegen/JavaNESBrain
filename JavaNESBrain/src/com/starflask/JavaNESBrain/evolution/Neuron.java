package com.starflask.JavaNESBrain.evolution;

import java.util.ArrayList;
import java.util.List;

import com.starflask.JavaNESBrain.utils.Vector2f;

public class Neuron {

	List<Gene> incoming = new ArrayList<Gene>(); //incoming gene list
	float value;
	
	 
	
	public Neuron()
	{
		
	}

	public List<Gene> getIncomingGeneList() {
	 
		return incoming;
	}

	public void setValue(float val) {
		value = val;
		
	}

	public float getValue() {
		return value;
	}

	 
 


}

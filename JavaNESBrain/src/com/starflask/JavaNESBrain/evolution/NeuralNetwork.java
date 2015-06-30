package com.starflask.JavaNESBrain.evolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NeuralNetwork {
	/*List<Neuron> neurons = new ArrayList<Neuron>();

	public List<Neuron> getNeurons() {
		return neurons;
	}
	*/
	
	HashMap<Integer,Neuron> neurons = new HashMap<Integer,Neuron>();
	
	public HashMap<Integer,Neuron> getNeurons() {
		return neurons;
	}
}

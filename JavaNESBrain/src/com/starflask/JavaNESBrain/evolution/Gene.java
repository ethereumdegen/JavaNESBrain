package com.starflask.JavaNESBrain.evolution;

public class Gene {

	int into;  //an index for neurons
	int out;
	float weight;
	private boolean enabled = true;
	int innovation;
	
	
	public Gene()
	{
		
	}
 
 
public static Gene copyGene(Gene othergene)
{
        Gene gene2 = new Gene();
        gene2.into = othergene.into;
        gene2.out = othergene.out;
        gene2.weight = othergene.weight;
        gene2.setEnabled(othergene.isEnabled());
        gene2.innovation = othergene.innovation;
       
        return gene2;
}


public Gene copy() {
	 
	return copyGene(this);
}


public int getNeuralOutIndex() {
	 
	return out;
}


public boolean isEnabled() {
	return enabled;
}


public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}


public int getNeuralInIndex() {
	 
	return into;
}


}

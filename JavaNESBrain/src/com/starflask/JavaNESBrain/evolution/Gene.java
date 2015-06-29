package com.starflask.JavaNESBrain.evolution;

public class Gene {

	int into;
	int out;
	float weight;
	boolean enabled = true;
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
        gene2.enabled = othergene.enabled;
        gene2.innovation = othergene.innovation;
       
        return gene2;
}


public Gene copy() {
	 
	return copyGene(this);
}


}

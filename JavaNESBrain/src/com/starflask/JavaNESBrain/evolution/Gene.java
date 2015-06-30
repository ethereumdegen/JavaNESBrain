package com.starflask.JavaNESBrain.evolution;

public class Gene {

	private int into; // an index for neurons
	private int out;
	private float weight;
	private boolean enabled = true;
	int innovation;

	public Gene() {

	}

	public static Gene copyGene(Gene othergene) {
		Gene gene2 = new Gene();
		gene2.setInto(othergene.getNeuralInIndex());
		gene2.setOut(othergene.getNeuralOutIndex());
		gene2.setWeight(othergene.getWeight());
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

	public int getInnovation() {
		return innovation;
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

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	

	public void setInto(int into) {
		this.into = into;
	}

	

	public void setOut(int out) {
		this.out = out;
	}

}

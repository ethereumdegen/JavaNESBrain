package com.starflask.JavaNESBrain.data;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.starflask.JavaNESBrain.SuperBrain;
import com.starflask.JavaNESBrain.evolution.GenePool;
import com.starflask.JavaNESBrain.utils.FastMath;
import com.starflask.JavaNESBrain.utils.Vector2Int;

public class GameDataManager {

	//final static int TIMEOUT_CONSTANT = 1500;
	int timeout;
	int bestScoreThisRun = 0; // the most right that we ever got so far

	SuperBrain superBrain;

	public GameDataManager(SuperBrain superBrain) {
		this.superBrain = superBrain;

		init();

	}

	int numOutputs = 8;

	private String[] buttonNames;

	private void init() {

		// if (superBrain.getRomName().equals("Super Mario World (USA)"))
		// {
		String savefilename = "DP1.state";

		setButtonNames(new String[] { "A", "B", "Select", "Enter", "Up", "Down", "Left", "Right", });

		// }

		numOutputs = getButtonNames().length;

	}

	public void siphonData() {

	}

	protected String getRomName() {
		return superBrain.getRomName();
	}

	int getTile(Vector2Int delta) {

		return 0;

	}

	protected int readbyte(int addr) {

		return superBrain.getCPU().read8((short) addr);

	}

	Sprite[] sprites;
	Sprite[] extendedSprites;

	Integer[] inputs;

	public List<Integer> getBrainSystemInputs() {
		siphonData();

		List<Integer> inputs = new ArrayList<Integer>();

		return inputs;

	}

	public int getNumInputs() {

		return 1;
	}

	public int getNumOutputs() {

		return numOutputs;
	}

	public String[] getButtonNames() {
		return buttonNames;
	}

	public void setButtonNames(String[] buttonNames) {
		this.buttonNames = buttonNames;
	}

	public int getCurrentScore() {
		return 0; // for mario, how 'right' he is. for galaga, the score
	}

	public int getCurrentFitness() {

		if (getCurrentScore() > 3186) { // mario only
			return getCurrentScore() - (getCurrentFrame() / 2) + 1000;
		}

		return getCurrentScore() - (getCurrentFrame() / 2);

	}

	public void initializeRun() {

		bestScoreThisRun = 0;
		timeout = getTimeoutConstant();

	}

	protected int getTimeoutConstant()
	{
		return 1000;
	}
		
	
	public boolean updateGiveUpTimer() {
		int timeoutBonus = getCurrentFrame() / 2;

		// if mario gets farther than he has ever been this run...
		if (getCurrentScore() > bestScoreThisRun) {
			bestScoreThisRun = getCurrentScore();
			timeout = getTimeoutConstant(); // also reset the timeout

		}

		timeout = timeout - 1;

		return timeout + timeoutBonus <= 0; // should give up

	}

	private int getCurrentFrame() {

		return superBrain.getPool().getCurrentFrame();
	}

	public HashMap<Integer, DebugCell> drawNeurons(Graphics g, GenePool pool) {

		return null;
	}

}

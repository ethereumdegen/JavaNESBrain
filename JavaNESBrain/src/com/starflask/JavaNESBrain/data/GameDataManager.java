package com.starflask.JavaNESBrain.data;

import java.util.ArrayList;
import java.util.List;

import com.starflask.JavaNESBrain.SuperBrain;
import com.starflask.JavaNESBrain.utils.FastMath;
import com.starflask.JavaNESBrain.utils.Vector2Int;

public class GameDataManager {

	
	final static int TIMEOUT_CONSTANT = 500;
	int timeout;
	int bestFitnessThisRun = 0; // the most right that we ever got so far
	 


	
	SuperBrain superBrain;
	
	public GameDataManager(SuperBrain superBrain)
	{
		this.superBrain=superBrain;
		
		init();
		
		
	}

	
	

int BoxRadius = 6;



int InputSize = (BoxRadius*2+1)*(BoxRadius*2+1);
 
int numInputs = InputSize+1;
int numOutputs = 8;

private String[]  buttonNames;

	
private void init() {
		 
	//if (superBrain.getRomName().equals("Super Mario World (USA)"))
   // {
		String savefilename = "DP1.state" ;
    
	        setButtonNames(new String[]{	        		
	                "A",
	                "B",
	                "Select",
	                "Enter",
	                "Up",
	                "Down",
	                "Left",
	                "Right",
	        });
	
   // }  
 
	
	
	 numOutputs = getButtonNames().length;
    
    
	}

public void getPositions()
{
			
}
 



protected String getRomName() {	 
	return superBrain.getRomName();
}

int getTile(Vector2Int delta)
{
 
	 
	
	return 0;
            
}

 protected Sprite[] getSprites()
 {
	 Sprite[] sprites = new Sprite[5];
	 
	  
		 
		 for(int slot=0;slot<=4;slot++)
		 {
			  
			int enemy = readbyte(0xF+slot) ;
                     if (enemy != 0) 
                     {
                             int ex = readbyte(0x6E + slot)*0x100 + readbyte(0x87+slot);
                            int ey = readbyte(0xCF + slot)+24;
                             sprites[ slot ] = new Sprite(ex,ey);
                     }  
                     
                     
                     
		 }
		 
	 
 
	 return sprites;
 }
	 
 protected Sprite[] getExtendedSprites()
 {
	 
	 
	 return null;
 }
	 
protected int readbyte(int addr) {
 
		 
		return superBrain.getCPU().read8( (short) addr);
	 

}



Sprite[] sprites;
Sprite[] extendedSprites;

Integer[] inputs;

public List<Integer> getBrainSystemInputs()
{
	getPositions();
    
    sprites = getSprites();
    extendedSprites = getExtendedSprites();

    List<Integer> inputs = new ArrayList<Integer>();
    
      
   
    return inputs ;
    
}


public int getNumInputs() {
	 
	return numInputs;
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


public int getBoxRadius() {
	return BoxRadius;
}


public int getCurrentScore() {  
	return 0;  //for mario, how 'right' he is. for galaga, the score
}
 
public int getCurrentFitness() {  
	
	if (getCurrentScore() > 3186) {  //mario only
		return getCurrentScore() - (getCurrentFrame() / 2) + 1000;
	}
	
	return getCurrentScore() - (getCurrentFrame() / 2);
	
	
}

public void initializeRun() {
	
	bestFitnessThisRun = 0;
	timeout = TIMEOUT_CONSTANT;
	
}
  

public boolean updateGiveUpTimer() {
	int timeoutBonus= getCurrentFrame() / 2;

	// if mario gets farther than he has ever been this run...
	if (getCurrentFitness() > bestFitnessThisRun) {
		bestFitnessThisRun =   getCurrentFitness();
		timeout = TIMEOUT_CONSTANT; //also reset the timeout
		
	}

	timeout = timeout - 1;

	return timeout + timeoutBonus <= 0; //should give up
	
}

private int getCurrentFrame() {
	 
	return superBrain.getPool().getCurrentFrame();
}
 

}

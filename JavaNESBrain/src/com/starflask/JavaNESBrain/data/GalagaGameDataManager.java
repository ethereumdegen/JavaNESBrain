package com.starflask.JavaNESBrain.data;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.starflask.JavaNESBrain.SuperBrain;


/*
 * Need to feed inputs for the divebombing enemies and their projectiles
 * need to feed the enemy row offsets in as inputs
 * need to make the neurons operate like floating points, not just binary 0s and 1s (continuous nn)
 * 
 */

public class GalagaGameDataManager extends GameDataManager{

	 //figure out where in the memory the sprites and the score is ..
	
	public GalagaGameDataManager(SuperBrain superBrain) {
		super(superBrain);
		 
	}
	
 

	int InputSize = 50 + 1;
 
	int numInputs = InputSize+1;

	int gamescore = 0;
	int[] gameScoreDigits = new int[7];
	
	int playerXpos =0;
	
	
	
	@Override
	public void siphonData()
	{
		gamescore = 0;
		for(int i=0;i<7;i++)
		{
			gameScoreDigits[i] = readbyte(0xE0 + 6-i);
			gamescore += gameScoreDigits[i] * (Math.pow(10, i));
		}
			 
	     //System.out.println(gamescore);
	    
	     playerXpos = readbyte(0x203);
	     
	      
	        	 
	         
	}

	@Override
	public int getCurrentScore() {  
		return  gamescore;   
	}


@Override
public List<Integer> getBrainSystemInputs()
{
	siphonData();
    
   
    List<Integer> inputs = new ArrayList<Integer>();
    
    inputs.add( playerXpos );
    
    for(int row =0;row<5;row++)
    {
    	for(int column =0; column < 10; column++)
    	{
    		int enemyInPosition = readbyte(0x0400 + column + row*0x10) ;
    		
    		 inputs.add(enemyInPosition); 
    	}
    }
    
      
    
    
  // velocity of mario ??? this was commented out anyways
  //  --mariovx = memory.read_s8(0x7B) ; 
   // --mariovy = memory.read_s8(0x7D) ;
   
    return inputs ;
    
}




public HashMap<Integer,DebugCell> drawNeurons(Graphics g ) {
	 
	
	HashMap<Integer,DebugCell> cells = new HashMap<Integer,DebugCell>();
	
	
	
	
	//draw inputs
	
	
	g.setColor(Color.GRAY);
	 
	 g.drawString("Grid Map (AI Inputs)", 80, 110);
	
	List<Integer> cellValues = getBrainSystemInputs();
	
	 
	
	//Iterator<Integer> cellValueInterator = cellValues.iterator();
	
	int inputCount = 0;
	
	for(int row =0;row<5;row++)
    {
    	for(int column =0; column < 10; column++)
    	{
    		int enemyInPosition = readbyte(0x0400 + column + row*0x10) ;
    		
		  
    		 g.setColor(Color.GRAY);
    		 
    		 if(enemyInPosition < 0) //enemy
    		 {
    		 g.setColor(Color.RED);
    		 }
    		 
    		 if(enemyInPosition > 0) //tile
    		 {
    		 g.setColor(Color.BLACK);   
    		 }
    		
    		 
    			DebugCell inputCell = new DebugCell();
    			inputCell.x = 30 + (10)*16/2 + column*16;
    			inputCell.y = 120 +  (5)*16/2 + row*16;
    			inputCell.value = enemyInPosition;
    			
    			cells.put(inputCount, inputCell  );
    		
    				
    			inputCount++;
    			
    			g.fillRect((int) inputCell.x,(int)  inputCell.y, 8, 8);
		 
		 
	 }
	
}
	
	
	return cells;
   
   
	
}



@Override
protected int getTimeoutConstant()
{
	 
	return 3500;
}

public int getNumInputs() {
	 
	return numInputs;
}



}

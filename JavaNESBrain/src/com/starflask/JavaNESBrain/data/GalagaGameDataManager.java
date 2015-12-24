package com.starflask.JavaNESBrain.data;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.starflask.JavaNESBrain.SuperBrain;

import jp.tanakh.bjne.nes.Renderer.ScreenInfo;


/*
 * Need to feed inputs for the divebombing enemies and their projectiles
 * need to feed the enemy row offsets in as inputs
 * need to make the neurons operate like floating points, not just binary 0s and 1s (continuous nn)
 * 
 * http://www.thealmightyguru.com/Games/Hacking/Wiki/index.php?title=Galaga
 */

public class GalagaGameDataManager extends GameDataManager {

	 //figure out where in the memory the sprites and the score is ..
	
	public GalagaGameDataManager(SuperBrain superBrain) {
		super(superBrain);
		 
	}
	
 

	int InputSize = 240 + 1;
 
	int numInputs = InputSize+1;

	int gamescore = 0;
	int[] gameScoreDigits = new int[7];
	
	int playerXpos =0;
	int playerLives =0;
	
	int initLives = 0;
	
	@Override
	public void initializeRun() {		
		super.initializeRun();

		initLives = readbyte(0x487);
		playerLives = readbyte(0x487);
		
		System.out.println("initializing run");
	}
	
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
	     
	      
	     playerLives = readbyte(0x487);
	         
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
    		
    		// inputs.add(enemyInPosition); 
    	}
    }
    
    //screen is 256 by 256.. lets make it into 16-pixel large squares 
    if(screenPixelData!=null)
    {
    	///System.out.println("can process pixels");
    	//should average / blur colors together
    	
    	//height is 15 x 16
    	//width is 16 x 16
    	
    	for(int row = 0; row < 15 ; row++)
    	{
    		for(int column =0; column < 16; column++)
        	{
        		PixelTile tile = new PixelTile(screenPixelData,row,column);
        		 inputs.add(tile.grayscaleValue); 
        	}
    	}
    	
    	
    }
    
    
  // velocity of mario ??? this was commented out anyways
  //  --mariovx = memory.read_s8(0x7B) ; 
   // --mariovy = memory.read_s8(0x7D) ;
   
    return inputs ;
    
}

class PixelTile
{
	/*
	 * 
	 * for (int i = 0; i < SCREEN_WIDTH * SCREEN_HEIGHT; i++) {
			bgr[i * 3] = info.buf[i * 3 + 2];
			bgr[i * 3 + 1] = info.buf[i * 3 + 1];
			bgr[i * 3 + 2] = info.buf[i * 3 + 0];
		}
		
	 */
	int grayscaleValue;
	
	public PixelTile(ScreenInfo screenPixelData, int row, int column) {
		grayscaleValue = 0;
		
		//the 3 is for the bgr
		int index = (column*240 + row )* 16 * 3 ;
		
		int sum = 0;
		for(int x = 0; x < 16; x++)
		{
			for(int y = 0; y < 16; y++)
			{
				sum += (int) screenPixelData.buf[index + x*16 + y + 0];
				sum += (int) screenPixelData.buf[index + x*16 + y + 1];
				sum += (int) screenPixelData.buf[index + x*16 + y + 2];
			}
		}
		
		byte val = screenPixelData.buf[index+2];		
		//System.out.println("meep" +  sum);
		grayscaleValue = (int) val;
	}


}



/**
 * 
 */
@Override
public int getCurrentFitness() {

	if (getCurrentScore() > 3186) { // mario only
		return getCurrentScore() - (getCurrentFrame() / 200) + 1000;
	}

	return getCurrentScore() - (getCurrentFrame() / 200);

}



public HashMap<Integer,DebugCell> drawNeurons(Graphics g ) {
	 
	
	HashMap<Integer,DebugCell> cells = new HashMap<Integer,DebugCell>();
	
	
	
	
	//draw inputs
	
	
	g.setColor(Color.GRAY);
	 
	 g.drawString("Grid Map (AI Inputs)", 80, 110);
	
	List<Integer> cellValues = getBrainSystemInputs();
	
	 
	
	//Iterator<Integer> cellValueInterator = cellValues.iterator();
	
	int inputCount = 0;
	
	for(int row = 0; row < 15 ; row++)
	{
		for(int column =0; column < 16; column++)
    	{
    		PixelTile tile = new PixelTile(screenPixelData,row,column);
    		
    		int enemyInPosition = tile.grayscaleValue ; 
    		//int enemyInPosition = readbyte(0x0400 + column + row*0x10) ;
    		
		    
    		 g.setColor(Color.GRAY);
    		 
    		 if(enemyInPosition < 5) //enemy
    		 {
    		 g.setColor(Color.RED);
    		 }
    		 
    		 if(enemyInPosition > 5 ) //tile
    		 {
    		 g.setColor(Color.BLACK);   
    		 }
    		
    		 
    			DebugCell inputCell = new DebugCell();
    			inputCell.x = -40 + (10)*16/2 + column*16;
    			inputCell.y = 120 +  (5)*16/2 + row*16;
    			inputCell.value = enemyInPosition;
    			
    			cells.put(inputCount, inputCell  );
    		
    				
    			inputCount++;
    			
    			g.fillRect((int) inputCell.x,(int)  inputCell.y, 8, 8);
		 
		 
	 }
	
}
	
	
	return cells;
   
   
	
}

int bonusTime = 0;

/**
 * A genome will give up if it has not scored for 15 seconds or if it loses a life
 */
@Override
public boolean updateGiveUpTimer() {
	//int timeoutBonus = getCurrentFrame() / 2;

	// if mario gets farther than he has ever been this run...
	if (getCurrentScore() > bestScoreThisRun) {   
		bestScoreThisRun = getCurrentScore();
		
		bonusTime+=1; 
	}

	int timeElapsed = (int) (System.currentTimeMillis() - startTime);
	 
	return timeElapsed   >= getTimeoutConstant()+ bonusTime || playerLives < initLives; // should give up

}

@Override
protected int getTimeoutConstant()
{
	 
	return 15000;
}

public int getNumInputs() {
	 
	return numInputs;
}

ScreenInfo screenPixelData;

@Override
public void outputScreen(ScreenInfo scri) {
	this.screenPixelData=scri;
	
}



}

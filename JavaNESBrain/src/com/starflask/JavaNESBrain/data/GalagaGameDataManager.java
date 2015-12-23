package com.starflask.JavaNESBrain.data;

import java.util.ArrayList;
import java.util.List;

import com.starflask.JavaNESBrain.SuperBrain;
import com.starflask.JavaNESBrain.utils.FastMath;
import com.starflask.JavaNESBrain.utils.Vector2Int;

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
    
     /*
    sprites = getSprites();
   
    extendedSprites = getExtendedSprites();

    
    for(int dy = -BoxRadius*16 ; dy <= BoxRadius*16  ; dy+= 16)
    {
    	for(int dx = -BoxRadius*16 ; dx <= BoxRadius*16  ; dx+= 16)
        {
    		Vector2Int deltaPos = new Vector2Int(dx, dy);
    		 
    		int cellValue = 0;	
    		
    		
    		 int tile = getTile( deltaPos );
    		 
                     if (tile == 1 && marioPos.getY() + dy < 0x1B0 )
                     {
                    	 cellValue = 1;
                    	 
                     }
                            
                     
                 for(int i=1; i < sprites.length; i++)
                 { 
                	 if(sprites[i] != null)
                	 {
                        float distx = FastMath.abs(sprites[i].getPos().getX() - (marioPos.getX()+dx) )  ;
                        float disty = FastMath.abs(sprites[i].getPos().getY() - (marioPos.getY()+dy) )  ;
                         
                         if (distx <= 8 && disty <= 8 )
                         {
                        	 cellValue = -1;
                         }
                	 }
                 }
                 
                
    		
                 inputs.add(cellValue); 
                 // 0 means nothing
                 // 1 means a tile , white in color on the grid
                 // -1 mean a baddie, black in color on the grid
    		
        }
    
    }*/
    
    
  // velocity of mario ??? this was commented out anyways
  //  --mariovx = memory.read_s8(0x7B) ; 
   // --mariovy = memory.read_s8(0x7D) ;
   
    return inputs ;
    
}

@Override
protected int getTimeoutConstant()
{
	return 1500;
}

public int getNumInputs() {
	 
	return numInputs;
}



}

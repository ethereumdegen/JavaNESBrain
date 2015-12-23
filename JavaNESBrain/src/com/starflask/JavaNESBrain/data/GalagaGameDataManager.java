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
	
	int gamescore = 0;
	int[] gameScoreDigits = new int[7];
	
	
	@Override
	public void siphonData()
	{
		gamescore = 0;
		for(int i=0;i<7;i++)
		{
			gameScoreDigits[i] = readbyte(0xE0 + 6-i);
			gamescore += gameScoreDigits[i] * (Math.pow(10, i));
		}
			 
	     System.out.println(gamescore);
	    
    	//screenPos.setX( readbyte(0x03AD) );   //Mbc.ram[941] decimal
    	//screenPos.setY( readbyte(0x03B8) );   //Mbc.ram[952]  decimal    
	        	 
	         
	}

	@Override
	public int getCurrentScore() {  
		return  gamescore;  //for mario, how 'right' he is. for galaga, the score
	}


}

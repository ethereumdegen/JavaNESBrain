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

	@Override
	public void getPositions()
	{
				 
		gamescore = ( readbyte(0x6D)*0x100 + readbyte(0x86)) ;
	     
	               
	        	 
	         
	}

	@Override
	public int getCurrentScore() {  
		return  gamescore;  //for mario, how 'right' he is. for galaga, the score
	}


}

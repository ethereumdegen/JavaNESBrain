package com.starflask.JavaNESBrain.data;

import java.util.ArrayList;
import java.util.List;

import com.starflask.JavaNESBrain.SuperBrain;
import com.starflask.JavaNESBrain.utils.FastMath;
import com.starflask.JavaNESBrain.utils.Vector2Int;

public class MarioGameDataManager extends GameDataManager{


	Vector2Int marioPos = new Vector2Int(0,0);
	Vector2Int screenPos = new Vector2Int(0,0);
	 
	
	public MarioGameDataManager(SuperBrain superBrain) {
		super(superBrain);
		 
	}
	
	@Override
	public void getPositions()
	{
				 
	        	marioPos.setX( readbyte(0x6D)*0x100 + readbyte(0x86)) ;
	        	marioPos.setY( readbyte(0x03B8) + 16 );
	               
	        	screenPos.setX( readbyte(0x03AD) );   //Mbc.ram[941] decimal
	        	screenPos.setY( readbyte(0x03B8) );   //Mbc.ram[952]  decimal
	         
	}
	int getTile(Vector2Int delta)
	{
	  
		  
				
				int x = (marioPos.getX() + delta.getX() + 8);
				int y = (marioPos.getY() + delta.getY() - 16);
	             
				
				//is this math right?
	            int page = (int) (FastMath.floor(x/256f)%2);

	            int subx = (int) FastMath.floor((x%256)/16f);
	            int suby = (int) FastMath.floor((y - 32)/16f);
	            int addr = 0x500 + page*13*16+suby*16+subx ;
	           
	            
	          
	            if (suby >= 13 || suby < 0 )
	            {
	                    return 0;
	            }
	           
	            if (readbyte(addr) != 0 )
	            {
	                    return 1;
	            }else{
	                    return 0;
	            }
	            
			 
		 
	            
	}
	
@Override
public List<Integer> getBrainSystemInputs()
{
	getPositions();
    
    sprites = getSprites();
    extendedSprites = getExtendedSprites();

    List<Integer> inputs = new ArrayList<Integer>();
    
    
    
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
    }
    
    
  // velocity of mario ??? this was commented out anyways
  //  --mariovx = memory.read_s8(0x7B) ; 
   // --mariovy = memory.read_s8(0x7D) ;
   
    return inputs ;
    
}

 
public Vector2Int getMarioPos() {
	 
	return marioPos;
}


@Override
public int getCurrentScore() {  
	return (int) getMarioPos().getX();  //for mario, how 'right' he is. for galaga, the score
}


}

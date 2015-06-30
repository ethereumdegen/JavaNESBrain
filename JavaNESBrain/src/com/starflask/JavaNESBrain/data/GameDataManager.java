package com.starflask.JavaNESBrain.data;

import java.util.ArrayList;
import java.util.List;

import com.starflask.JavaNESBrain.SuperBrain;
import com.starflask.JavaNESBrain.utils.FastMath;
import com.starflask.JavaNESBrain.utils.Vector2Int;

public class GameDataManager {

	Vector2Int marioPos = new Vector2Int(0,0);
	Vector2Int screenPos = new Vector2Int(0,0);
	
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
			if (getRomName().startsWith("Super Mario Bros."))
			{
        	
        	marioPos.setX( readbyte(0x6D)*0x100 + readbyte(0x86)) ;
        	marioPos.setY( readbyte(0x03B8) + 16 );
               
        	screenPos.setX( readbyte(0x03AD) );
        	screenPos.setY( readbyte(0x03B8) ); 
        
			} 
}
 



private String getRomName() {	 
	return superBrain.getRomName();
}

int getTile(Vector2Int delta)
{
 
	if (getRomName().startsWith("Super Mario Bros."))
			{
	  
			
			int x = (int) (marioPos.getX() + delta.getX() + 8);
			int y = (int) (marioPos.getY() + delta.getY() - 16);
             
            int page = (int) (FastMath.floor(x/256)%2);

            int subx = (int) FastMath.floor((x%256)/16);
            int suby = (int) FastMath.floor((y - 32)/16);
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
	
	return 0;
            
}

 private Sprite[] getSprites()
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
	 
 private Sprite[] getExtendedSprites()
 {
	 
	 
	 return null;
 }
	 
private int readbyte(int addr) {
 
		 
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



}

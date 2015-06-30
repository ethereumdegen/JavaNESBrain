package com.starflask.JavaNESBrain;

import com.grapeshot.halfnes.CPURAM;
import com.starflask.JavaNESBrain.utils.FastMath;
import com.starflask.JavaNESBrain.utils.Vector2f;

public class GameDataManager {

	Vector2f marioPos = new Vector2f(0,0);
	Vector2f screenPos = new Vector2f(0,0);
	
	SuperBrain superBrain;
	
	GameDataManager(SuperBrain superBrain)
	{
		this.superBrain=superBrain;
		
		init();
		
		
	}

	
	

int BoxRadius = 6;
int InputSize = (BoxRadius*2+1)*(BoxRadius*2+1);
 
int numInputs = InputSize+1;
int numOutputs = 8;

String[]  buttonNames;

	
private void init() {
		 
	//if (superBrain.getRomName().equals("Super Mario World (USA)"))
   // {
		String savefilename = "DP1.state" ;
    
	        buttonNames = new String[]{
	                "A",
	                "B",
	                "Up",
	                "Down",
	                "Left",
	                "Right",
	        };
	
   // }  
 
	
	
	 numOutputs = buttonNames.length;
    
    
	}

void getPositions()
{
        
        	
        	marioPos.setX( readbyte(0x6D)*0x100 + readbyte(0x86)) ;
        	marioPos.setY( readbyte(0x03B8) + 16 );
               
        	screenPos.setX( readbyte(0x03AD) );
        	screenPos.setY( readbyte(0x03B8) ); 
        
        	 
}
 



private int getTile(Vector2f delta)
{
 
	 /* if (superBrain.getRomName().equals("Super Mario World (USA)"))
      {
		  
		 int x = (int) FastMath.floor((marioPos.x + delta.x +8)/16) ; 
	     int y = (int) FastMath.floor((marioPos.y + delta.y )/16);
	               
	     
	     int addr = 0x1C800 + ((int)FastMath.floor((x/0x10)))*0x1B0 + y*0x10 + x%0x10; 
	     
	     return readbyte( addr );  //used to be readbyte ...
		  
      }*/
	  

			int x = (int) (marioPos.getX() + delta.x + 8);
			int y = (int) (marioPos.getY() + delta.y - 16);
             
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

public Integer[] getBrainSystemInputs()
{
	getPositions();
    
    sprites = getSprites();
    extendedSprites = getExtendedSprites();

    inputs = new Integer[169];
    int  numSystemInputs = 0;
    
    for(int dy = -BoxRadius*16 ; dy < BoxRadius*16  ; dy+= 16)
    {
    	for(int dx = -BoxRadius*16 ; dx < BoxRadius*16  ; dx+= 16)
        {
    		Vector2f deltaPos = new Vector2f(dx, dy);
    		
    		numSystemInputs++;    		
    		inputs[numSystemInputs] = 0;
    		
    		 int tile = getTile( deltaPos );
    		 
                     if (tile == 1 && marioPos.getY() + dy < 0x1B0 )
                     {
                    	  inputs[numSystemInputs] = 1 ;
                     }
                            
                     
                 for(int i=1; i < sprites.length; i++)
                 { 
                	 if(sprites[i] != null)
                	 {
                        float distx = FastMath.abs(sprites[i].getPos().getX() - (marioPos.getX()+dx) )  ;
                        float disty = FastMath.abs(sprites[i].getPos().getY() - (marioPos.getY()+dy) )  ;
                         
                         if (distx <= 8 && disty <= 8 )
                         {
                        	 inputs[numSystemInputs] = -1 ;
                         }
                	 }
                 }
                 
                
    		
    		
    		
        }
    }
    
    
  // velocity of mario ???
  //  --mariovx = memory.read_s8(0x7B) ; 
   // --mariovy = memory.read_s8(0x7D) ;
   
    return inputs ;
    
}

public Vector2f getMarioPos() {
	 
	return marioPos;
}

public int getNumInputs() {
	 
	return numInputs;
}

public int getNumOutputs() {
	 
	return numOutputs;
}

 /*

function getInputs()
        getPositions()
       
        sprites = getSprites()
        extended = getExtendedSprites()
       
        local inputs = {}
       
        for dy=-BoxRadius*16,BoxRadius*16,16 do
                for dx=-BoxRadius*16,BoxRadius*16,16 do
                        inputs[#inputs+1] = 0
                       
                        tile = getTile(dx, dy)
                        if tile == 1 and marioY+dy < 0x1B0 then
                                inputs[#inputs] = 1
                        end
                       
                        for i = 1,#sprites do
                                distx = math.abs(sprites[i]["x"] - (marioX+dx))
                                disty = math.abs(sprites[i]["y"] - (marioY+dy))
                                if distx <= 8 and disty <= 8 then
                                        inputs[#inputs] = -1
                                end
                        end
 
                        for i = 1,#extended do
                                distx = math.abs(extended[i]["x"] - (marioX+dx))
                                disty = math.abs(extended[i]["y"] - (marioY+dy))
                                if distx < 8 and disty < 8 then
                                        inputs[#inputs] = -1
                                end
                        end
                end
        end
       
        --mariovx = memory.read_s8(0x7B)
        --mariovy = memory.read_s8(0x7D)
       
        return inputs
end

*/


}

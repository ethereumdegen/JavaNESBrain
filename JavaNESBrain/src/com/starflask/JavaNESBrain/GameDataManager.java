package com.starflask.JavaNESBrain;

import com.grapeshot.halfnes.CPURAM;
import com.starflask.JavaNESBrain.utils.FastMath;
import com.starflask.JavaNESBrain.utils.Vector2f;

public class GameDataManager {

	Vector2f marioPos;
	Vector2f screenPos;
	
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
    
	      String[]  buttonNames = new String[]{
	                "A",
	                "B",
	                "X",
	                "Y",
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
        if (superBrain.getRomName().equals("Super Mario World (USA)"))
        {
        	marioPos.setX( readS16( 0x94 ) );  // was memory.read_s16_le( 0x94) 
        	marioPos.setY( readS16( 0x96 ) );   
               
        	Vector2f layer1 = new Vector2f(
        			readS16( 0x1A ),
        			readS16(0x1C )
        			);
        	
        	screenPos.set( marioPos.subtract(layer1) );
               
        }
        else if (superBrain.getRomName().equals( "Super Mario Bros.") )
        		{
        	
                //marioX = memory.readbyte(0x6D) * 0x100 + memory.readbyte(0x86)
               // marioY = memory.readbyte(0x03B8)+16
       
                //screenX = memory.readbyte(0x03AD)
                //screenY = memory.readbyte(0x03B8)
        	}
}

private float readS16(int addr) {
	
	return getRAM().read(addr);
}

private CPURAM getRAM() {
	 
	return superBrain.getRAM();
}



private int getTile(Vector2f delta)
{
 
	  if (superBrain.getRomName().equals("Super Mario World (USA)"))
      {
		  
		 int x = (int) FastMath.floor((marioPos.x + delta.x +8)/16) ; 
	     int y = (int) FastMath.floor((marioPos.y + delta.y )/16);
	               
	     
	     int addr = 0x1C800 + ((int)FastMath.floor((x/0x10)))*0x1B0 + y*0x10 + x%0x10; 
	     
	     return readbyte( addr );  //used to be readbyte ...
		  
      }
	  
	  
	  
	  return 0;
}

 private Sprite[] getSprites()
 {
	 Sprite[] sprites = new Sprite[12];
	 
	 if (superBrain.getRomName().equals("Super Mario World (USA)"))
     {
		
		 
		 for(int slot=0;slot<12;slot++)
		 {
			  
			 int status = readbyte(0x14C8 + slot ) ; //used to be readbyte...
                     if ( status != 0 )
                     {
                             int spritex = readbyte(0xE4+slot) + readbyte(0x14E0+slot)*256 ;
                             int spritey = readbyte(0xD8+slot) + readbyte(0x14D4+slot)*256 ;
                             
                             sprites[ slot ] = new Sprite(spritex, spritey);
                     }
                     
		 }
		 
		
     }
 
	 return sprites;
 }
	 
 private Sprite[] getExtendedSprites()
 {
	 Sprite[] extendedSprites = new Sprite[12];
	 
	 if (superBrain.getRomName().equals("Super Mario World (USA)"))
     {
		 
		 
		 for(int slot=0;slot<12;slot++)
		 {
			 int  number = readbyte(0x170B+slot);
			 
			 if (number != 0)
			 {
                     int spritex = readbyte(0x171F+slot) + readbyte(0x1733+slot)*256 ; 
                     int spritey = readbyte(0x1715+slot) + readbyte(0x1729+slot)*256 ;
                      
                     extendedSprites[ slot ] = new Sprite(spritex, spritey);
			 }
			
                             int spritex = readbyte(0xE4+slot) +readbyte(0x14E0+slot)*256 ;
                             int spritey = readbyte(0xD8+slot) + readbyte(0x14D4+slot)*256 ;
                             
                            
                    
                     
		 }
     }
	 
	 return extendedSprites;
 }
	 
private int readbyte(int addr) {
	 
	return getRAM().read(/*(byte)*/ addr);
	
}



Sprite[] sprites;
Sprite[] extendedSprites;

int[] inputs;

public int[] getBrainSystemInputs()
{
	getPositions();
    
    sprites = getSprites();
    extendedSprites = getExtendedSprites();

    inputs = new int[BoxRadius*2 + 1];
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
                        float distx = FastMath.abs(sprites[i].getPos().getX() - (marioPos.getX()+dx) )  ;
                        float disty = FastMath.abs(sprites[i].getPos().getY() - (marioPos.getY()+dy) )  ;
                         
                         if (distx <= 8 && disty <= 8 )
                         {
                        	 inputs[numSystemInputs] = -1 ;
                         }
                 }
                 
                 for(int i=1; i < extendedSprites.length; i++)
                 { 
                        float distx = FastMath.abs(extendedSprites[i].getPos().getX() - (marioPos.getX()+dx) )  ;
                        float disty = FastMath.abs(extendedSprites[i].getPos().getY() - (marioPos.getY()+dy) )  ;
                         
                         if (distx <= 8 && disty <= 8 )
                         {
                        	 inputs[numSystemInputs] = -1 ;
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

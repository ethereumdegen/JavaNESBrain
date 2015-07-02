package com.starflask.JavaNESBrain.data;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List; 
 












import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import com.starflask.JavaNESBrain.SuperBrain;
import com.starflask.JavaNESBrain.VirtualGamePad;
import com.starflask.JavaNESBrain.evolution.Gene;
import com.starflask.JavaNESBrain.evolution.GenePool;
import com.starflask.JavaNESBrain.evolution.NeuralNetwork;
import com.starflask.JavaNESBrain.evolution.Neuron;
import com.starflask.JavaNESBrain.utils.FastMath;

public class BrainInfoWindow  {
	
	static final int SCREEN_SIZE_MULTIPLIER = 2;
	
	static final int SCREEN_WIDTH = 256;
	
	static final int SCREEN_HEIGHT = 240;
	 
	GameDataManager gameDataManager;
	
	VirtualGamePad gamepad;
	
	GenePool pool;
	
	public BrainInfoWindow(VirtualGamePad gamepad,	GameDataManager gameDataManager, GenePool pool) {
		 
		
		try {			
		
			Display.setDisplayMode(new DisplayMode(SCREEN_WIDTH*SCREEN_SIZE_MULTIPLIER,SCREEN_HEIGHT*SCREEN_SIZE_MULTIPLIER));
						
			Display.create();
			
			Display.setTitle("NES Brain Info");
			
			// init OpenGL
		    GL11.glMatrixMode(GL11.GL_PROJECTION);
		    GL11.glLoadIdentity();
		    GL11.glOrtho(0, SCREEN_WIDTH*SCREEN_SIZE_MULTIPLIER, SCREEN_HEIGHT*SCREEN_SIZE_MULTIPLIER, 0, 1, -1);
		    GL11.glMatrixMode(GL11.GL_MODELVIEW);
			
		} catch (LWJGLException e) {
			e.printStackTrace();
			 System.exit(0);
		}
		
		 
		
		this.gameDataManager=gameDataManager;
		
		System.out.println(" GD " + gameDataManager);
		
		this.pool=pool;
		this.gamepad=gamepad;
		
		loadAssets();
		
		/*
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onExit();
			}
		});

		
		 
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				onHardwareKey(e.getKeyCode(), true);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				onHardwareKey(e.getKeyCode(), false);
			}
		});
		 */

	}

	TrueTypeFont font;
	
 	
	
	private void loadAssets() {

		Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
		font = new TrueTypeFont(awtFont, false);
	 
		/*// load font from a .ttf file
		try {
			InputStream inputStream	= ResourceLoader.getResourceAsStream("myfont.ttf");
	 
			Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			awtFont2 = awtFont2.deriveFont(24f); // set font size
			font2 = new TrueTypeFont(awtFont2, false);
	 
		} catch (Exception e) {
			e.printStackTrace();
		}	*/
		
	}

	private void onExit() {
		System.exit(0);
	}

	protected void onHardwareKey(int keyCode, boolean pressed) {
		gamepad.onHardwareKey(keyCode,pressed);
	}

	private BufferedImage image = new BufferedImage(SCREEN_WIDTH,
			SCREEN_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
	
	 
	
	
	public void outputScreen() {
		
		
		 // Clear the screen and depth buffer
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);  
         
         
        setColor(Color.white);
        fillRect(0,0,SCREEN_WIDTH*SCREEN_SIZE_MULTIPLIER,SCREEN_HEIGHT*SCREEN_SIZE_MULTIPLIER);
        
        
        drawNeurons();
        
        
  
        Display.update();
		
		  
		
		//REWRITE THE DRAW FUNCTION
		
	 	 
		 
	}


	private void drawNeurons() {
		 
		
		HashMap<Integer,DebugCell> cells = new HashMap<Integer,DebugCell>();
		
		
		setColor(Color.BLACK);
		
		
		font.drawString(10, 50, "Generation #" + getPool().getGeneration());
		font.drawString(200, 50,"Species:" + getPool().getCurrentSpecies().toString() );
		font.drawString(10, 80, "Genome:" + getPool().getCurrentGenome().toString() );
		font.drawString(200, 80, "Fitness:" + getPool().getCurrentGenome().getFitness() );
		font.drawString(290, 80, "Max Fitness:" + getPool().getMaxFitness() );
		
		NeuralNetwork network = getPool().getCurrentGenome().getNetwork();
		
		
		
		//draw inputs
		
		
		setColor(Color.GRAY);
		 
		font.drawString(40, 110, "Grid Map (AI Inputs)" );
		
		List<Integer> cellValues = getGameData().getBrainSystemInputs();
		


		int inputCount = 0;

	    for(int dy = -getGameData().getBoxRadius()*16 ; dy <= getGameData().getBoxRadius()*16  ; dy+= 16)
	    {
	    	for(int dx = -getGameData().getBoxRadius()*16 ; dx <= getGameData().getBoxRadius()*16  ; dx+= 16)
	        {
	    
	    			    		    		
	    		 int tile = cellValues.get(inputCount);
			  
	    		 setColor(Color.GRAY);
	    		 
	    		 if(tile < 0) //enemy
	    		 {
	    		 setColor(Color.RED);
	    		 }
	    		 
	    		 if(tile > 0) //tile
	    		 {
	    		 setColor(Color.BLACK);   
	    		 }
	    		
	    		 
	    			DebugCell inputCell = new DebugCell();
	    			inputCell.x = 30 + (getGameData().getBoxRadius())*16/2 + dx/2;
	    			inputCell.y = 120 +  (getGameData().getBoxRadius())*16/2 + dy/2;
	    			inputCell.value = tile;
	    			
	    			cells.put(inputCount, inputCell  );
	    		
	    				
	    			inputCount++;
	    			
	    			fillRect((int) inputCell.x,(int)  inputCell.y, 8, 8);
			 
			 
		 }
		
	}
		
		
		
		//draw outputs
		//each output only has one assigned neuron, but that neuron can have multiple incoming genes
		for (int o = 0; o < this.getGameData().getNumOutputs(); o++) {
			
			String button = this.getGameData().getButtonNames()[o];

			setColor(Color.GRAY);
			
			DebugCell outputCell = new DebugCell();
			outputCell.x = 350;
			outputCell.y = 120 +  16 * o;
			outputCell.value = network.getNeurons().get( SuperBrain.MaxNodes + o) .getValue();
			
			
			
			if (network.getNeurons().get( SuperBrain.MaxNodes + o).getValue() > 0) {
				setColor(Color.RED);
			}
			
			
			
			font.drawString( 365, 120 + 12 +  16 * o , button );
			fillRect(350 ,120 +  16 * o, 12, 12);
			
			
			cells.put(SuperBrain.MaxNodes + o, outputCell );
			
		}
		
		//the key corresponds to which input affects this neuron
		
		for(int key : network.getNeurons().keySet())
		{
			Neuron neuron = network.getNeurons().get(key);
			
			if(key > getGameData().numInputs && key <= SuperBrain.MaxNodes)
			{
				DebugCell cell = new DebugCell();
				cell.x = 260;
				cell.y = 120;
				cell.value = neuron.getValue();
				cells.put(key, cell);
			}
			
			
			//draw lines for incoming gene list
			//neuron.getIncomingGeneList()
		}
		
		//neurons with a key in between 10 and 1000000 are 'middle men' and only connect to other...neurons? genes?
		
       for(int n =0; n < 4; n++)
       {
    	   for(Gene gene : pool.getCurrentGenome().getGenes() )
    	   {
    		   DebugCell cellIn = cells.get(gene.getNeuralInIndex());
			   DebugCell cellOut = cells.get(gene.getNeuralOutIndex());
    		   
    		   if(gene.isEnabled() && cellIn!=null && cellOut != null )
    		   {
    			  
    			   
    			   if(gene.getNeuralInIndex() > getGameData().getNumInputs() && gene.getNeuralInIndex() <= getMaxNodes()  )
    			   {
    				   
    				   cellIn.x =  0.75f*cellIn.x + 0.25f*cellOut.x;
    				   
    				   if(cellIn.x >= cellOut.x)
    				   {
    					   cellIn.x = cellIn.x - 40;
    				   }
    				   
    				   if(cellIn.x < 190)
    				   {
    					   cellIn.x = 190;
    				   }
    				   if(cellIn.x > 320)
    				   {
    					   cellIn.x = 320;
    				   }
    				   
    				   cellIn.y = 0.75f*cellIn.y + 0.25f*cellOut.y;
    				   
    			   }
    			   
    			   if(gene.getNeuralOutIndex() > getGameData().getNumInputs() && gene.getNeuralOutIndex() <= getMaxNodes()  )
    			   {
    				   		cellOut.x = 0.25f*cellIn.x + 0.75f*cellOut.x;
    				   		
                               if (cellIn.x >= cellOut.x )
                               {
                            		   cellOut.x = cellOut.x + 40;
                               }


                               if(cellOut.x < 190)
            				   {
                            	   cellOut.x = 190;
            				   }
            				   if(cellOut.x > 320)
            				   {
            					   cellOut.x = 320;
            				   }            				   
            				   
            				   cellOut.y = 0.25f*cellIn.y + 0.75f*cellOut.y;
    				   
    			   }
    			    
    			   
    		   }
    	   }
    	   
       }
   
       for(int cellIndex : cells.keySet())
       {
    	   DebugCell cell = cells.get(cellIndex);
    	   
    	   if(cellIndex > getGameData().getNumInputs() || cell.value!=0 )
    	   {
    		   int colordarkness = (int) FastMath.floor((cell.value+1)/ 2*256 );
    		   if( colordarkness > 255){ colordarkness = 255 ; }
    		   if( colordarkness < 0){ colordarkness = 0 ; }
    		   
    		   float opacity = cell.value == 0 ? 0.5f : 1f;
    		   
    		   Color color = new Color(colordarkness / 255f, 0.2f, 0.2f, opacity);
    		   
    		   setColor(color);
    		   drawRect((int) cell.x , (int) cell.y  , (int) (4 * opacity),(int)  (4 * opacity));

    	   }
    	   
       }
       
       for(Gene gene : pool.getCurrentGenome().getGenes() )
       {
    	   
    	   DebugCell cellIn = cells.get(gene.getNeuralInIndex());
		   DebugCell cellOut = cells.get(gene.getNeuralOutIndex());
    	   if(gene.isEnabled() && cellIn!=null && cellOut!=null )
		   {
			   
			   
			   
			   float opacity = cellIn.value == 0 ? 0.2f : 0.8f ;
			   float colorDarkness = 0.5f - FastMath.floor(FastMath.abs(SuperBrain.sigmoid(gene.getWeight())*0.5f  ));
			   
			   Color color = new Color(colorDarkness,colorDarkness,colorDarkness,opacity);
			   
			   setColor(color);
			   
			   drawLine((int) cellIn.x,(int)cellIn.y,(int) cellOut.x,(int) cellOut.y);
			   
		   }
    	   
       }
     
       
       
		
	}

	

	private void fillRect(int x, int y, int w, int h) {
		 // draw quad
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x,y);
        GL11.glVertex2f(x+w,y);
        GL11.glVertex2f(x+w,y+h);
        GL11.glVertex2f(x,y+h);
        GL11.glEnd();
		
	}

	private void drawLine(int x, int y, int x2, int y2) {
		 GL11.glBegin(GL11.GL_LINE_STRIP );
	        GL11.glVertex2f(x,y);
	        GL11.glVertex2f(x2,y2);
	        GL11.glEnd();		
	}

	private void drawRect(int x, int y, int w, int h) {
		drawLine(x,y,x+w,y);
		drawLine(x+w,y,x+w,y+h);
		drawLine(x+w,y+h,x,y+h);
		drawLine(x,y+h,x,y);
	}

	

	private void setColor(Color color) {
		GL11.glColor3f(color.getRed()/255f,color.getGreen()/255f,color.getBlue()/255f);
		
	}

	private int getMaxNodes() { 
		return SuperBrain.MaxNodes;
	}



	private GameDataManager getGameData() {
		 
		return gameDataManager;
	}

	
	private GenePool getPool()
	{
		return pool;
	}
	
	
	/**
	 * 
	 * 
function displayGenome(genome)
        local network = genome.network
        local cells = {}
        local i = 1
        local cell = {}
        for dy=-BoxRadius,BoxRadius do
                for dx=-BoxRadius,BoxRadius do
                        cell = {}
                        cell.x = 50+5*dx
                        cell.y = 70+5*dy
                        cell.value = network.neurons[i].value
                        cells[i] = cell
                        i = i + 1
                end
        end
        local biasCell = {}
        biasCell.x = 80
        biasCell.y = 110
        biasCell.value = network.neurons[Inputs].value
        cells[Inputs] = biasCell
       
        for o = 1,Outputs do
                cell = {}
                cell.x = 220
                cell.y = 30 + 8 * o
                cell.value = network.neurons[MaxNodes + o].value
                cells[MaxNodes+o] = cell
                local color
                if cell.value > 0 then
                        color = 0xFF0000FF
                else
                        color = 0xFF000000
                end
                gui.drawText(223, 24+8*o, ButtonNames[o], color, 9)
        end
       
        for n,neuron in pairs(network.neurons) do
                cell = {}
                if n > Inputs and n <= MaxNodes then
                        cell.x = 140
                        cell.y = 40
                        cell.value = neuron.value
                        cells[n] = cell
                end
        end
       
        for n=1,4 do
                for _,gene in pairs(genome.genes) do
                        if gene.enabled then
                                local c1 = cells[gene.into]
                                local c2 = cells[gene.out]
                                if gene.into > Inputs and gene.into <= MaxNodes then
                                        c1.x = 0.75*c1.x + 0.25*c2.x
                                        if c1.x >= c2.x then
                                                c1.x = c1.x - 40
                                        end
                                        if c1.x < 90 then
                                                c1.x = 90
                                        end
                                       
                                        if c1.x > 220 then
                                                c1.x = 220
                                        end
                                        c1.y = 0.75*c1.y + 0.25*c2.y
                                       
                                end
                                if gene.out > Inputs and gene.out <= MaxNodes then
                                        c2.x = 0.25*c1.x + 0.75*c2.x
                                        if c1.x >= c2.x then
                                                c2.x = c2.x + 40
                                        end
                                        if c2.x < 90 then
                                                c2.x = 90
                                        end
                                        if c2.x > 220 then
                                                c2.x = 220
                                        end
                                        c2.y = 0.25*c1.y + 0.75*c2.y
                                end
                        end
                end
        end
       
        gui.drawBox(50-BoxRadius*5-3,70-BoxRadius*5-3,50+BoxRadius*5+2,70+BoxRadius*5+2,0xFF000000, 0x80808080)
        for n,cell in pairs(cells) do
                if n > Inputs or cell.value ~= 0 then
                        local color = math.floor((cell.value+1)/2*256)
                        if color > 255 then color = 255 end
                        if color < 0 then color = 0 end
                        local opacity = 0xFF000000
                        if cell.value == 0 then
                                opacity = 0x50000000
                        end
                        color = opacity + color*0x10000 + color*0x100 + color
                        gui.drawBox(cell.x-2,cell.y-2,cell.x+2,cell.y+2,opacity,color)
                end
        end
        for _,gene in pairs(genome.genes) do
                if gene.enabled then
                        local c1 = cells[gene.into]
                        local c2 = cells[gene.out]
                        local opacity = 0xA0000000
                        if c1.value == 0 then
                                opacity = 0x20000000
                        end
                       
                        local color = 0x80-math.floor(math.abs(sigmoid(gene.weight))*0x80)
                        if gene.weight > 0 then
                                color = opacity + 0x8000 + 0x10000*color
                        else
                                color = opacity + 0x800000 + 0x100*color
                        end
                        gui.drawLine(c1.x+1, c1.y, c2.x-3, c2.y, color)
                end
        end
       
        gui.drawBox(49,71,51,78,0x00000000,0x80FF0000)
       
        if forms.ischecked(showMutationRates) then
                local pos = 100
                for mutation,rate in pairs(genome.mutationRates) do
                        gui.drawText(100, pos, mutation .. ": " .. rate, 0xFF000000, 10)
                        pos = pos + 8
                end
        end
end


	 */
	
	
	
}
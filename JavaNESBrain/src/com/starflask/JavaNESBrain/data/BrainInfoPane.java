package com.starflask.JavaNESBrain.data;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import jp.tanakh.bjne.ui.AWTRenderer;

import com.starflask.JavaNESBrain.SuperBrain;
import com.starflask.JavaNESBrain.VirtualGamePad;
import com.starflask.JavaNESBrain.evolution.Gene;
import com.starflask.JavaNESBrain.evolution.GenePool;
import com.starflask.JavaNESBrain.evolution.NeuralNetwork;
import com.starflask.JavaNESBrain.evolution.Neuron;
import com.starflask.JavaNESBrain.utils.FastMath;
import com.starflask.JavaNESBrain.utils.Vector2Int;
import com.starflask.JavaNESBrain.utils.Vector2f;

public class BrainInfoPane extends JPanel {
	
	GameDataManager gameDataManager;
	GenePool pool;
	
	public BrainInfoPane(GameDataManager gameDataManager, GenePool pool)
	{
		this.gameDataManager=gameDataManager;
		this.pool=pool;
		
	}
	
	 
	public void update()
	{
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		renderScreen(g);
		 
	}
	
	  void renderScreen(Graphics g) {
			
			
			
			/*byte[] bgr = ((DataBufferByte) image.getRaster().getDataBuffer())
					.getData();

			for (int i = 0; i < SCREEN_WIDTH * SCREEN_HEIGHT; i++) {
				//bgr[i * 3] = info.buf[i * 3 + 2];
				//bgr[i * 3 + 1] = info.buf[i * 3 + 1];
				//bgr[i * 3 + 2] = info.buf[i * 3 + 0];
				
				bgr[i * 3] = 0;
				bgr[i * 3 + 1] = 0;
				bgr[i * 3 + 2] = 0;
				
			}
	*/
			  
			  
			int left = getInsets().left;
			int top = getInsets().top;
			
			 
			
			//g.drawImage(image, left, top, left + SCREEN_WIDTH*SCREEN_SIZE_MULTIPLIER, top + SCREEN_HEIGHT*SCREEN_SIZE_MULTIPLIER, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
			g.clearRect(left, top, left + BrainInfoWindow.SCREEN_WIDTH*BrainInfoWindow.SCREEN_SIZE_MULTIPLIER, top + BrainInfoWindow.SCREEN_HEIGHT*BrainInfoWindow.SCREEN_SIZE_MULTIPLIER);
					
			drawHeaderInfo(g);
			HashMap<Integer,DebugCell>  cells = getGameDataManager().drawNeurons(g);
			//drawNeurons(g);
		 	 drawOutputs(g, cells);
		 
		}


	

		private void drawHeaderInfo(Graphics g) {

			
			g.setColor(Color.BLACK);
			
			
			g.drawString("Generation #" + getPool().getGeneration(), 10, 50);
			g.drawString("Species:" + getPool().getCurrentSpecies().toString(), 200, 50);
			g.drawString("Genome:" + getPool().getCurrentGenome().toString(), 10, 80);
			g.drawString("Fitness:" + getPool().getCurrentGenome().getFitness(), 200, 80);
			g.drawString("Max Fitness:" + getPool().getMaxFitness(), 290, 80);
			
			
			
		}


		private GenePool getPool() {
			 
			return pool;
		}
		
		private GameDataManager getGameDataManager() {
			 
			return gameDataManager;
		}

		private void drawOutputs(Graphics g, HashMap<Integer,DebugCell> cells) {
			
			
			NeuralNetwork network = getPool().getCurrentGenome().getNetwork();
			
			if(cells == null || cells.isEmpty() || network == null || network.getNeurons().isEmpty())
			{
				return;
			}
			
			
			//draw outputs
			//each output only has one assigned neuron, but that neuron can have multiple incoming genes
			for (int o = 0; o <  getGameDataManager().getNumOutputs(); o++) {
				
				String button =  getGameDataManager().getButtonNames()[o];

				g.setColor(Color.GRAY);
				
				DebugCell outputCell = new DebugCell();
				outputCell.x = 350;
				outputCell.y = 120 +  16 * o;
				
				try
				{
					outputCell.value = network.getNeurons().get( SuperBrain.MaxNodes + o) .getValue();
				}catch(Exception e)
				{
					e.printStackTrace();
					System.out.println( network );
					System.out.println( network.getNeurons() );
					System.out.println( SuperBrain.MaxNodes + o );
					System.out.println( network.getNeurons().get( SuperBrain.MaxNodes + o) );
				}
				
				
				if (network.getNeurons().get( SuperBrain.MaxNodes + o).getValue() > 0) {
					g.setColor(Color.RED);
				}
				
				
				
				g.drawString( button , 365, 120 + 12 +  16 * o);
				g.fillRect(350 ,120 +  16 * o, 12, 12);
				
				
					cells.put(SuperBrain.MaxNodes + o, outputCell );
				
			}
			
			//the key corresponds to which input affects this neuron
			
			for(int key : network.getNeurons().keySet())
			{
				Neuron neuron = network.getNeurons().get(key);
				
				if(key > getGameDataManager().getNumInputs() && key <= SuperBrain.MaxNodes)
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
	    			  
	    			   
	    			   if(gene.getNeuralInIndex() > getGameDataManager().getNumInputs() && gene.getNeuralInIndex() <= getMaxNodes()  )
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
	    			   
	    			   if(gene.getNeuralOutIndex() > getGameDataManager().getNumInputs() && gene.getNeuralOutIndex() <= getMaxNodes()  )
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
	    	   
	    	   if(cellIndex > getGameDataManager().getNumInputs() || cell.value!=0 )
	    	   {
	    		   int colordarkness = (int) FastMath.floor((cell.value+1)/ 2*256 );
	    		   if( colordarkness > 255){ colordarkness = 255 ; }
	    		   if( colordarkness < 0){ colordarkness = 0 ; }
	    		   
	    		   float opacity = cell.value == 0 ? 0.5f : 1f;
	    		   
	    		   Color color = new Color(colordarkness / 255f, 0.2f, 0.2f, opacity);
	    		   
	    		   g.setColor(color);
	    		   g.drawRect((int) cell.x , (int) cell.y  , (int) (4 * opacity),(int)  (4 * opacity));

	    	   }
	    	   
	       }
	       
	       for(Gene gene : pool.getCurrentGenome().getGenes() )
	       {
	    	   
	    	   DebugCell cellIn = cells.get(gene.getNeuralInIndex());
			   DebugCell cellOut = cells.get(gene.getNeuralOutIndex());
	    	   if(gene.isEnabled() && cellIn!=null && cellOut!=null )
			   {
				   
				   Color lowColor = Color.darkGray;
				   Color highColor = Color.green;
				   
				   boolean triggered = cellIn.value <= 0   ;
				   float colorDarkness = 0.5f - FastMath.floor(FastMath.abs(SuperBrain.sigmoid(gene.getWeight())*0.5f  ));
				   
				   Color color = lowColor;
				   
				   if(triggered){
					   color = highColor ;
				   }
				   
				   g.setColor(color);
				   
				   g.drawLine((int) cellIn.x,(int)cellIn.y,(int) cellOut.x,(int) cellOut.y);
				   
			   }
	    	   
	       }
	     
	      
		}

		private int getMaxNodes() { 
			return SuperBrain.MaxNodes;
		}

}
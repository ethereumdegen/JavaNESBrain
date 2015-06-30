package com.starflask.JavaNESBrain.data;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Iterator;
import java.util.List;

import jp.tanakh.bjne.ui.AWTRenderer;

import com.starflask.JavaNESBrain.VirtualGamePad;
import com.starflask.JavaNESBrain.utils.Vector2Int;

public class BrainInfoWindow extends Frame{
	
	static final int SCREEN_SIZE_MULTIPLIER = 2;
	
	static final int SCREEN_WIDTH = 256;
	
	static final int SCREEN_HEIGHT = 240;
	 
	GameDataManager gameDataManager;
	
	
	public BrainInfoWindow(VirtualGamePad gamepad,	GameDataManager gameDataManager) {
		super("NESBrain Info");
		
		this.gameDataManager=gameDataManager;
		
		setVisible(true);
		setVisible(false);
		setSize(SCREEN_WIDTH*SCREEN_SIZE_MULTIPLIER  + getInsets().left + getInsets().right, SCREEN_HEIGHT*SCREEN_SIZE_MULTIPLIER + getInsets().top + getInsets().bottom);
		setVisible(true);

	}

	private BufferedImage image = new BufferedImage(SCREEN_WIDTH,
			SCREEN_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
	
	 
	
	
	public void outputScreen() {
		
		byte[] bgr = ((DataBufferByte) image.getRaster().getDataBuffer())
				.getData();

		for (int i = 0; i < SCREEN_WIDTH * SCREEN_HEIGHT; i++) {
			//bgr[i * 3] = info.buf[i * 3 + 2];
			//bgr[i * 3 + 1] = info.buf[i * 3 + 1];
			//bgr[i * 3 + 2] = info.buf[i * 3 + 0];
			
			bgr[i * 3] = 0;
			bgr[i * 3 + 1] = 0;
			bgr[i * 3 + 2] = 0;
			
		}

		int left = getInsets().left;
		int top = getInsets().top;
		Graphics g = getGraphics();
		
		
		//g.drawImage(image, left, top, left + SCREEN_WIDTH*SCREEN_SIZE_MULTIPLIER, top + SCREEN_HEIGHT*SCREEN_SIZE_MULTIPLIER, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
		
		
		drawInfoElements( g );			 
		
	 	 
	}

	private void drawInfoElements(Graphics g) {
		
		drawTiles(g);
		
		
		
		
		
	}

	private void drawTiles(Graphics g) {
		
		List<Integer> cellValues = getGameData().getBrainSystemInputs();
		
		Iterator<Integer> cellValueInterator = cellValues.iterator();

	    for(int dy = -getGameData().getBoxRadius()*16 ; dy <= getGameData().getBoxRadius()*16  ; dy+= 16)
	    {
	    	for(int dx = -getGameData().getBoxRadius()*16 ; dx <= getGameData().getBoxRadius()*16  ; dx+= 16)
	        {
	    		Vector2Int deltaPos = new Vector2Int(dx, dy);
	    			    		    		
	    		 int tile = cellValueInterator.next();
			 
	    		 g.setColor(Color.GRAY);
	    		 
	    		 if(tile < 0)
	    		 {
	    		 g.setColor(Color.BLACK);
	    		 }
	    		 
	    		 if(tile > 0)
	    		 {
	    		 g.setColor(Color.BLUE);
	    		 }
	    		 
	    		 
	    		 g.fillRect(30+ (getGameData().getBoxRadius())*16 + dx,60+  (getGameData().getBoxRadius())*16 + dy, 12, 12);
			 
			 
		 }
		
	}

}
	
	private GameDataManager getGameData() {
		 
		return gameDataManager;
	}

	public void updateScreen() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
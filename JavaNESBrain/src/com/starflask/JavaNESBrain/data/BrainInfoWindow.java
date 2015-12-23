package com.starflask.JavaNESBrain.data;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
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

public class BrainInfoWindow extends Frame{
	
	static final int SCREEN_SIZE_MULTIPLIER = 2;
	
	static final int SCREEN_WIDTH = 256;
	
	static final int SCREEN_HEIGHT = 240;
	 
	
	
	VirtualGamePad gamepad;

	BrainInfoPane pane;
	
	
	public BrainInfoWindow(VirtualGamePad gamepad,	GameDataManager gameDataManager, GenePool pool) {
		super("NESBrain Info");
		
		
		
		System.out.println(" GD " + gameDataManager);
		
		pane = new BrainInfoPane(gameDataManager , pool);
		this.add(pane);
		
		//this.gameDataManager=gameDataManager;
		//this.pool=pool;
		this.gamepad=gamepad;
		
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("src/icon.png"));
		    this.setIconImage( img  );			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onExit();
			}
		});

		
		setVisible(true);
		setVisible(false);
		setSize(SCREEN_WIDTH*SCREEN_SIZE_MULTIPLIER  + getInsets().left + getInsets().right, SCREEN_HEIGHT*SCREEN_SIZE_MULTIPLIER + getInsets().top + getInsets().bottom);
		setVisible(true);
		
		
		pane.setSize(SCREEN_WIDTH*SCREEN_SIZE_MULTIPLIER  + getInsets().left + getInsets().right, SCREEN_HEIGHT*SCREEN_SIZE_MULTIPLIER + getInsets().top + getInsets().bottom);
		pane.setVisible(true);
		
		
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


	}

	
	public void update()
	{
		pane.update();
		
			 
		  
		
	}
 
	private void onExit() {
		System.exit(0);
	}

	protected void onHardwareKey(int keyCode, boolean pressed) {
		gamepad.onHardwareKey(keyCode,pressed);
	}

	/*private BufferedImage image = new BufferedImage(SCREEN_WIDTH,
			SCREEN_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
	*/
	 
	
	
	



 
	
}
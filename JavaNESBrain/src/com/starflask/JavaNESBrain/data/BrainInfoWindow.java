package com.starflask.JavaNESBrain.data;

import java.awt.Frame;

import jp.tanakh.bjne.ui.AWTRenderer;

import com.starflask.JavaNESBrain.VirtualGamePad;

public class BrainInfoWindow extends Frame{
	
	static final int SCREEN_SIZE_MULTIPLIER = 2;
	 
	public BrainInfoWindow(VirtualGamePad gamepad,	GameDataManager gameDataManager) {
		super("NESBrain Info");
		

		setVisible(true);
		setVisible(false);
		setSize(256*SCREEN_SIZE_MULTIPLIER  + getInsets().left + getInsets().right, 240*SCREEN_SIZE_MULTIPLIER + getInsets().top + getInsets().bottom);
		setVisible(true);

	}

	
	
	
	
}

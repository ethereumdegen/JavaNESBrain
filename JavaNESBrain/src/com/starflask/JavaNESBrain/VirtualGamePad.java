package com.starflask.JavaNESBrain;

import java.awt.event.KeyEvent;
import java.util.HashMap;


public class VirtualGamePad  {

	int[] integerBuffer = new int[16];   // 0 to 7 are player 1
	
	
	HashMap<String,Integer> KEYNAMEMAP = new HashMap<String,Integer>();
	
	VirtualGamePad()
	{
		KEYNAMEMAP.put("P1 A", 0);
		KEYNAMEMAP.put("P1 B", 1);
		KEYNAMEMAP.put("P1 Select", 2);
		KEYNAMEMAP.put("P1 Enter", 3);		
		KEYNAMEMAP.put("P1 Up", 4);
		KEYNAMEMAP.put("P1 Down", 5);
		KEYNAMEMAP.put("P1 Left", 6);
		KEYNAMEMAP.put("P1 Right", 7);
	
		
	}
	
	
	
	
	public void setOutputs(HashMap<String, Boolean> gamePadOutputs) {
				
		for(String key : gamePadOutputs.keySet())
		{
			setKeyState(key ,  gamePadOutputs.get(key) ) ;
		}		
	}
	
	
	 
	private void setKeyState(String keyname, Boolean pressed) {
		if(pressed)
		{
		System.out.println("pressing " + keyname );
		}
		
		int keyIndex = KEYNAMEMAP.get(keyname);
		
		int value = pressed ? 1 : 0 ;
		
		integerBuffer[keyIndex] = value;
						
		integerBuffer[2] = 0; //no select
		integerBuffer[3] = 0; //no enter
		
	}

	public void clear() {
		for (int i = 0; i < 16; i++){
		//	integerBuffer[i] = 0;
		}
		
	}

	public int[] getIntegerBuffer() {
		
		return integerBuffer;
	}


	static final int[][] keyDef = {
		{ KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_SHIFT,
				KeyEvent.VK_ENTER, KeyEvent.VK_UP, KeyEvent.VK_DOWN,
				KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, },
		{ KeyEvent.VK_V, KeyEvent.VK_B, KeyEvent.VK_N, KeyEvent.VK_M,
				KeyEvent.VK_O, KeyEvent.VK_COMMA, KeyEvent.VK_K,
				KeyEvent.VK_L, } };


	public void onHardwareKey(int keyCode, boolean pressed) {
		for (int i = 0; i < 16; i++){
				if (keyCode == keyDef[i/8][i%8]){					
					integerBuffer[i ] = (pressed ? 1 : 0);		
					
					System.out.println("forcing key " + i);
			}
		}
	}

}

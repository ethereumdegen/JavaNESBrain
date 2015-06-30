package com.starflask.JavaNESBrain;

import java.util.HashMap;

import com.grapeshot.halfnes.ui.ControllerInterface;

public class VirtualGamePad implements ControllerInterface {

	int[] integerBuffer = new int[16];   // 0 to 7 are player 1
	
	
	HashMap<String,Integer> KEYNAMEMAP = new HashMap<String,Integer>();
	
	VirtualGamePad()
	{
		KEYNAMEMAP.put("P1 A", 1);
		KEYNAMEMAP.put("P1 B", 2);
		KEYNAMEMAP.put("P1 Select", 3);
		KEYNAMEMAP.put("P1 Enter", 4);		
		KEYNAMEMAP.put("P1 Up", 5);
		KEYNAMEMAP.put("P1 Down", 6);
		KEYNAMEMAP.put("P1 Left", 7);
		KEYNAMEMAP.put("P1 Right", 8);
	
		
	}
	
	
	@Override
	public void strobe() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void output(boolean state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getbyte() {
		// TODO Auto-generated method stub
		return 0;
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

		int keyIndex = KEYNAMEMAP.get(keyname);
		
		int value = pressed ? 1 : 0 ;
		
		integerBuffer[keyIndex] = value;
		}
	}

	public void clear() {
	}

	public int[] getIntegerBuffer() {
		
		return integerBuffer;
	}

}

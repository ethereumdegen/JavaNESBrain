package com.starflask.JavaNESBrain;

import com.starflask.JavaNESBrain.utils.Vector2Int;

public class Sprite {
	
	Vector2Int pos;
	public Sprite(int x, int y) {
		pos = new Vector2Int(x,y);
	}
	public Vector2Int getPos() {
		 
		return pos;
	}

	
}

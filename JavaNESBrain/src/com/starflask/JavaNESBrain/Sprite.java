package com.starflask.JavaNESBrain;

import com.starflask.JavaNESBrain.utils.Vector2f;

public class Sprite {
	
	Vector2f pos;
	public Sprite(int x, int y) {
		pos = new Vector2f(x,y);
	}
	public Vector2f getPos() {
		 
		return pos;
	}

	
}

package com.starflask.JavaNESBrain;

import java.io.IOException;

import javax.swing.UIManager;

public class NESBrain {
	
	//use this as vm arg   -Djava.library.path=natives
	

	   public static void main(String[] args) throws IOException {
	        try {
	            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	        } catch (Exception e) {
	            System.err.println("Could not set system look and feel. Meh.");
	        }
	        com.grapeshot.halfnes.NES emulator = new com.grapeshot.halfnes.NES();
	        
	       
	        SuperBrain brain = new SuperBrain();
	        Thread AIThread = new Thread( brain );
	        AIThread.start();
	        
	        
	        if (args == null || args.length < 1 || args[0] == null) {
	        	emulator.run();
	        } else {
	        	emulator.run(args[0]);  	
	        	
	        	emulator.setControllers( brain.getController() , null);
	        }

	    }
	   
}

package jp.tanakh.bjne.ui;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

import jp.tanakh.bjne.nes.Cpu;
import jp.tanakh.bjne.nes.Nes;

public class BJNEmulator extends Frame {

	private static final long serialVersionUID = 1L;

	private Nes nes = null;
	private AWTRenderer r = null;
	private FPSTimer timer = new FPSTimer();

	private Object nesLock = new Object();

	public static void main(String[] args) {
		new BJNEmulator(args.length == 1 ? args[0] : null);
	}

	public BJNEmulator(String file) {
		// setup main window

		super("Nes Emulator");

		MenuBar menuBar = new MenuBar();
		setMenuBar(menuBar);

		{
			Menu menu = new Menu("File");
			{
				MenuItem item = new MenuItem("Open");
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						onOpen();
					}
				});
				menu.add(item);
				
				
				MenuItem aibutton = new MenuItem("Start AI");
				aibutton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						startAI();
					}
				});
				menu.add(aibutton);
				
				
			}
			{
				MenuItem item = new MenuItem("Exit");
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						onExit();
					}
				});
				menu.add(item);
			}
			menuBar.add(menu);
		}
		{
			Menu menu = new Menu("Settings");
			MenuItem mutebutton = new MenuItem("Mute");
			mutebutton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					toggleAudio();						
				}
			});
			menu.add(mutebutton);
			menuBar.add(menu);
		}
		{
			Menu menu = new Menu("Help");
			MenuItem item = new MenuItem("About");
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onAbout();
				}
			});
			menu.add(item);
			menuBar.add(menu);
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onExit();
			}
		});

		initializeNes();
		
		
		if (file != null)
			openRom(file);

		setVisible(true);
		setVisible(false);
		setSize(256 * AWTRenderer.SCREEN_SIZE_MULTIPLIER + getInsets().left + getInsets().right, 240 * AWTRenderer.SCREEN_SIZE_MULTIPLIER
				+ getInsets().top + getInsets().bottom);
		setVisible(true);

		
		
		//loop();
	}

	
	protected void toggleAudio() {
		nes.audioEnabled = !nes.audioEnabled;
	}


	boolean aiEnabled = false;
	public boolean isAiEnabled() {
		return aiEnabled;
	}

	protected void startAI() {
		aiEnabled = true;
	}

	final int FPS = 60;
	
	private void loop() {
		

		while(true) {
			
			stepEmulation(); 
			
		}
	}

	public void stepEmulation() {
	 
		synchronized (nesLock) {
			if (nes == null)
			{
				return;
			}
			
			long start = System.nanoTime();
			nes.execFrame();

			for (;;) {
				int bufStat = r.getSoundBufferState();
				if (bufStat < 0)
					break;
				if (bufStat == 0) {
					long elapsed = System.nanoTime() - start;
					long wait = (long) (1.0 / FPS - elapsed / 1e-9);
					try {
						if (wait > 0)
							Thread.sleep(wait);
					} catch (InterruptedException e) {
					}
					break;
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
			// timer.elapse(60);
		}
	}

	private void initializeNes() {
		try {
			r = new AWTRenderer(this);
		} catch (LineUnavailableException e) {
			System.out.println("Cannot initialize Renderer.");
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void openRom(String file) {
		System.out.println("loading " + file);
		synchronized (nesLock) {
			try {
				nes = new Nes(r);
				nes.load(file);
			} catch (IOException e) {
				System.out.println("error: loading " + file + " ("
						+ e.getMessage() + ")");
				nes = null;
			}
		}
	}

	private void onOpen() {
		FileDialog d = new FileDialog(this, "Open ROM", FileDialog.LOAD);
		d.setVisible(true);
		String dir = d.getDirectory();
		String file = d.getFile();
		openRom(dir + file);
	}

	private void onExit() {
		System.exit(0);
	}

	private class AboutDialog extends Dialog {
		private static final long serialVersionUID = 1L;

		AboutDialog(Frame owner) {
			super(owner);
			setLayout(new FlowLayout());

			add(new Label("Beautiful Japanese Nes Emulator for Java"));
			add(new Label("Version 0.2.0"));
			add(new Label("(Modded to add NESBrain AI)"));

			Button b = new Button("OK");
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			add(b);

			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			});

			setTitle("About");
			setSize(270, 200);
		}
	}

	private void onAbout() {
		Dialog dlg = new AboutDialog(this);
		dlg.setModal(true);
		dlg.setVisible(true);
	}


	public Nes getNES()
	{
		
		
		return nes;
	}
	
	public void setGamepadInput(int[] buf )
	{
		
		nes.setGamepadInput(buf);	 
	}

	public String getCurrentRomName() {
		
		if(nes!=null && nes.getRom()!=null)
		{
		return nes.getRom().getROMName();
		}
		
		return "none";
	}

}

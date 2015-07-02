package jp.tanakh.bjne.nes;

import java.io.IOException;
import java.util.HashMap;

public class Nes {
	public Nes(Renderer r) {
		renderer = r;
		cpu = new Cpu(this);
		apu = new Apu(this);
		ppu = new Ppu(this);
		mbc = new Mbc(this);
		regs = new Regs(this);
		rom = new Rom(this);
		mapper = null;
	}
	
	public boolean audioEnabled = true;

	public void load(String fname) throws IOException {
		rom.load(fname);
		mapper = MapperMaker.makeMapper(rom.mapperNo(), this);
		if (mapper == null)
			throw new IOException(String.format("unsupported mapper: #%d", rom.mapperNo()));
		reset();
	}

	public boolean checkMapper() {
		return mapper != null;
	}

	

	 
	SaveState[] saveStates = new SaveState[5];
	
	public void saveState(int stateNumber) {
		saveStates[stateNumber] = new SaveState(regs,mbc,cpu,apu,ppu);
		 
	}

	public void loadState(int stateNumber) {
		//rom.loadState( saveStates[stateNumber] );
		mbc.loadState( saveStates[stateNumber] );
		cpu.loadState( saveStates[stateNumber] );
		regs.loadState( saveStates[stateNumber] );
		apu.loadState( saveStates[stateNumber] );
		ppu.loadState( saveStates[stateNumber] );
		
	}

	public void reset() {
		// reset rom & mbc first
		rom.reset();
		mbc.reset();

		// reset mapper
		mapper.reset();

		// reset rest
		cpu.reset();
		apu.reset();
		ppu.reset();
		regs.reset();

		System.out.println("Reset virtual machine ...");
	}

	public void execFrame() {
		// CPU clock is 1.7897725MHz
		// 1789772.5 / 60 / 262 = 113.85...
		// 114 cycles per line?
		// 1789772.5 / 262 / 114 = 59.922 fps ?

		Renderer.ScreenInfo scri = renderer.requestScreen(256, 240);
		Renderer.SoundInfo sndi = renderer.requestSound();
		Renderer.InputInfo inpi = renderer.requestInput(2, 8);

		
		if (sndi != null && audioEnabled) {
			apu.genAudio(sndi);
			renderer.outputSound(sndi);
		}
		
		
		
		if (inpi != null)
		{
			regs.setInput(inpi.buf);
		}
		
		if(externalGamepadBuffer != null)
		{
			
			regs.setInput( externalGamepadBuffer );
		}
		

		regs.setVBlank(false, true);
		regs.startFrame();
		for (int i = 0; i < 240; i++) {
			if (mapper != null)
				mapper.hblank(i);
			regs.startScanline();
			if (scri != null)
				ppu.render(i, scri);
			ppu.spriteCheck(i);
			apu.sync();
			cpu.exec(114);
			regs.endScanline();
		}

		if ((regs.getFrameIrq() & 0xC0) == 0)
			cpu.setIrq(true);

		for (int i = 240; i < 262; i++) {
			if (mapper != null)
				mapper.hblank(i);
			apu.sync();
			if (i == 241) {
				regs.setVBlank(true, false);
				cpu.exec(0); // one extra op will execute after VBLANK
				regs.setVBlank(regs.getIsVBlank(), true);
				cpu.exec(114);
			} else
				cpu.exec(114);
		}

		if (scri != null)
			renderer.outputScreen(scri);
	}

	public Rom getRom() {
		return rom;
	}

	public Cpu getCpu() {
		return cpu;
	}

	public Ppu getPpu() {
		return ppu;
	}

	public Apu getApu() {
		return apu;
	}

	public Mbc getMbc() {
		return mbc;
	}

	public Regs getRegs() {
		return regs;
	}

	public Mapper getMapper() {
		return mapper;
	}

	private Rom rom;
	private Cpu cpu;
	private Ppu ppu;
	private Apu apu;
	private Mbc mbc;
	private Regs regs;
	private Mapper mapper;

	private Renderer renderer;
	
	int[] externalGamepadBuffer;
	public void setGamepadInput(int[] buf)
	{
		externalGamepadBuffer =  buf ; 
	}
}

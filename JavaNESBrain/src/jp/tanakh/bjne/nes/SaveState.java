package jp.tanakh.bjne.nes;

public class SaveState {

	Cpu cpuData;
	//Rom romData;
	Mbc mbcData;
	
	Regs regsData;
	Apu apuData;
	


	Ppu ppuData;
	 
	public SaveState(Regs regs, Mbc mbc, Cpu cpu, Apu apu, Ppu ppu) {
		cpuData = cpu.getCopy();
		regsData = regs.getCopy();		
		mbcData = mbc.getCopy();
		apuData = apu.getCopy();		
		ppuData = ppu.getCopy();		
	}
	 
	
	public Cpu getCpu() {
		 
		return cpuData;
	}
	
 


	public Regs getRegsData() {
		return regsData;
	}



	public Apu getApuData() {
		return apuData;
	}



	public Ppu getPpuData() {
		return ppuData;
	}


	public Mbc getMbc() {
		 
		return mbcData;
	}

}

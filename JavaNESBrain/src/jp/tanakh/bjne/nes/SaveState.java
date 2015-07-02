package jp.tanakh.bjne.nes;

public class SaveState {

	Cpu cpuData;
	//Rom romData;
	byte[] ramData = new byte[0x800];
	
	Regs regsData;
	Apu apuData;
	


	Ppu ppuData;
	 
	public SaveState(Regs regs, Mbc mbc, Cpu cpu, Apu apu, Ppu ppu) {
		cpuData = cpu.getCopy();
		regsData = regs.getCopy();		
		ramData = mbc.getRamCopy();
		apuData = apu.getCopy();		
		ppuData = ppu.getCopy();		
	}
	 
	
	public Cpu getCpu() {
		 
		return cpuData;
	}
	
	public byte[] getRamData() {
		return ramData;
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

}

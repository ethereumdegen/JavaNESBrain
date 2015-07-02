package jp.tanakh.bjne.nes;

public class SaveState {

	Cpu cpuData;
	Rom romData;
	byte[] ramData = new byte[0x800];
	
	public SaveState(Rom rom, Mbc mbc, Cpu cpu) {
		cpuData = cpu.getCopy();
		romData = rom.getCopy();		
		ramData = mbc.getRamCopy();
	}

	public Rom getRom() {
		 
		return romData;
	}
	
	public Cpu getCpu() {
		 
		return cpuData;
	}

}

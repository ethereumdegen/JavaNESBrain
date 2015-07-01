package jp.tanakh.bjne.nes;

public class SaveState {

	Rom romData;
	byte[] ramData = new byte[0x800];
	
	public SaveState(Rom rom, Mbc mbc) {
		 
		romData = rom.getCopy();		
		ramData = mbc.getRamCopy();
	}

	public Rom getRom() {
		 
		return romData;
	}

}

package jp.tanakh.bjne.nes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class Rom {
	
	public Rom() {
	}

	public Rom(Nes n) {
	}

	public void reset() {

	}

	public void release() {
		romDat = null;
		chrDat = null;
		sram = null;
		vram = null;
	}
	
	String filename = "";

	public void load(String fname) throws IOException {
		release();
		
		if(fname.length() > 2)
		{
			filename = fname.substring(fname.lastIndexOf(File.separatorChar)+1);	
		}
		
		System.out.println("filename: "+filename);

		FileInputStream is = new FileInputStream(fname);
		byte[] dat = new byte[is.available()];
		is.read(dat, 0, dat.length);

		if (!(dat[0] == 'N' && dat[1] == 'E' && dat[2] == 'S' && dat[3] == '\u001A'))
			throw new IOException("rom signature is invalid");

		prgPageCnt = dat[4] & 0xff;
		chrPageCnt = dat[5] & 0xff;

		mirroring = (dat[6] & 1) != 0 ? MirrorType.VERTICAL
				: MirrorType.HORIZONTAL;
		sramEnable = (dat[6] & 2) != 0;
		trainerEnable = (dat[6] & 4) != 0;
		fourScreen = (dat[6] & 8) != 0;

		mapper = ((dat[6] & 0xff) >> 4) | (dat[7] & 0xf0);

		int romSize = 0x4000 * prgPageCnt;
		int chrSize = 0x2000 * chrPageCnt;

		romDat = new byte[romSize];
		if (chrSize != 0)
			chrDat = new byte[chrSize];
		sram = new byte[0x2000];
		vram = new byte[0x2000];

		if (romSize > 0)
			System.arraycopy(dat, 16, romDat, 0, romSize);
		if (chrSize > 0)
			System.arraycopy(dat, 16 + romSize, chrDat, 0, chrSize);

		System.out.printf("Cartridge information:\n");
		System.out.printf("%d KB rom, %d KB vrom\n", romSize / 1024,
				chrSize / 1024);
		System.out.printf("mapper #%d\n", mapper);
		System.out.printf("%s mirroring\n",
				mirroring == MirrorType.VERTICAL ? "vertical" : "holizontal");
		System.out.printf("sram        : %s\n", sramEnable ? "Y" : "N");
		System.out.printf("trainer     : %s\n", trainerEnable ? "Y" : "N");
		System.out.printf("four screen : %s\n", fourScreen ? "Y" : "N");
	}

	
	
	
	
	public void loadState(SaveState saveState) {
		
		/*
		if (romSize() > 0)
			System.arraycopy(saveState.getRom().romDat, 16, romDat, 0, romSize());
		if (chrSize() > 0)
			System.arraycopy(saveState.getRom().chrDat, 16 + romSize(),  chrDat, 0, chrSize() );
		
			System.arraycopy(saveState.getRom().sram, 0, sram, 0, 0x2000);
		
			System.arraycopy(saveState.getRom().vram, 0, vram, 0, 0x2000);
		*/
		
	}
	

	public byte[] getRom() {
		return romDat;
	}

	public byte[] getChr() {
		return chrDat;
	}

	public byte[] getSram() {
		return sram;
	}

	public byte[] getVram() {
		return vram;
	}

	public int romSize() {
		return prgPageCnt;
	}

	public int chrSize() {
		return chrPageCnt;
	}

	public int mapperNo() {
		return mapper;
	}

	public boolean hasSram() {
		return sramEnable;
	}

	public boolean hasTrainer() {
		return trainerEnable;
	}

	public boolean isFourScreen() {
		return fourScreen;
	}

	public enum MirrorType {
		HORIZONTAL, VERTICAL,
	}

	public MirrorType mirror() {
		return mirroring;
	}

	private int prgPageCnt, chrPageCnt;
	private MirrorType mirroring;
	private boolean sramEnable, trainerEnable;
	private boolean fourScreen;
	private int mapper;
	private byte[] romDat, chrDat, sram, vram;
	
	public String getROMName()
	{
		return filename;
	}

	public Rom getCopy()
	{
		Rom copy = new Rom();
		

		
		copy.prgPageCnt = this.prgPageCnt; 
		copy.chrPageCnt = this.chrPageCnt;  
		copy.mirroring = this.mirroring;  
		 
		copy.sramEnable = this.sramEnable;
		copy.trainerEnable = this.trainerEnable;
		copy.fourScreen = this.fourScreen;
		
		copy.mapper = this.mapper;
 
		int romSize = 0x4000 * copy.prgPageCnt;
		int chrSize = 0x2000 * copy.chrPageCnt;

		copy.romDat = new byte[romSize];		
		if (chrSize != 0)
			copy.chrDat = new byte[chrSize];
		copy.sram = new byte[0x2000];
		copy.vram = new byte[0x2000];

		if (romSize > 0)
			System.arraycopy(this.romDat, 0, copy.romDat, 0, romSize);
		if (chrSize > 0)
			System.arraycopy(this.chrDat, 0 , copy.chrDat, 0, chrSize);
		
			System.arraycopy(this.sram, 0, copy.sram, 0, 0x2000);
		
			System.arraycopy(this.vram, 0, copy.vram, 0, 0x2000);
		
		return copy;		
	}
}

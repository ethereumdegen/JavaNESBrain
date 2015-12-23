package jp.tanakh.bjne.nes;

import java.util.Arrays;

public class Mbc {
	public Mbc(Nes n) {
		nes = n;
		rom = vrom = null;
		isVram = false;
	}

	public void reset() {
		Arrays.fill(ram, (byte) 0x00);

		rom = nes.getRom().getRom();
		vrom = nes.getRom().getChr();
		sram = nes.getRom().getSram();
		vram = nes.getRom().getVram();

		for (int i = 0; i < 4; i++)
			mapRom(i, i);
		if (vrom != null)
			for (int i = 0; i < 8; i++)
				mapVrom(i, i);
		else
			for (int i = 0; i < 8; i++)
				mapVram(i, i);

		sramEnabled = true;
	}

	public void mapRom(int page, int val) {
		romPage[page] = (val % (nes.getRom().romSize() * 2)) * 0x2000;
	}

	public void mapVrom(int page, int val) {
		if (vrom != null) {
			chrPage[page] = (val % (nes.getRom().chrSize() * 8)) * 0x0400;
			isVram = false;
		}
	}

	public void mapVram(int page, int val) {
		// TODO : VRAM size
		chrPage[page] = (val % 8) * 0x400;
		isVram = true;
	}

	public byte read(short adr) {
		
		//this ram dump is for debugging only
				boolean printRAM = false;
				
				if(printRAM)
				{
					printRAM = false;
					System.out.println("--RAM DUMP BEGIN--");
					for(int i=0;i<ram.length;i++)
					{
						System.out.println(ram[i]);
					}
					System.out.println("--RAM DUMP END--");
				}
				
				
		
		switch ((adr & 0xffff) >> 11) {
		case 0x00:
		case 0x01:
		case 0x02:
		case 0x03: // 0x0000 - 0x1FFF
			return ram[adr & 0x7ff];
		case 0x04:
		case 0x05:
		case 0x06:
		case 0x07: // 0x2000 - 0x3FFF
			return nes.getRegs().read(adr);
		case 0x08:
		case 0x09:
		case 0x0A:
		case 0x0B: // 0x4000 - 0x5FFF
			if (adr < 0x4020)
				return nes.getRegs().read(adr);
			return 0; // TODO : Expanision ROM
		case 0x0C:
		case 0x0D:
		case 0x0E:
		case 0x0F: // 0x6000 - 0x7FFF
			if (sramEnabled)
				return sram[adr & 0x1fff];
			else
				return 0x00;
		case 0x10:
		case 0x11:
		case 0x12:
		case 0x13: // 0x8000 - 0x9FFF
			return rom[romPage[0] + (adr & 0x1fff)];
		case 0x14:
		case 0x15:
		case 0x16:
		case 0x17: // 0xA000 - 0xBFFF
			return rom[romPage[1] + (adr & 0x1fff)];
		case 0x18:
		case 0x19:
		case 0x1A:
		case 0x1B: // 0xC000?0xDFFF
			return rom[romPage[2] + (adr & 0x1fff)];
		case 0x1C:
		case 0x1D:
		case 0x1E:
		case 0x1F: // 0xE000?0xFFFF
			return rom[romPage[3] + (adr & 0x1fff)];
		}
		return 0x00;
	}

	public void write(short adr, byte dat) {
		switch ((adr & 0xffff) >> 11) {
		case 0x00:
		case 0x01:
		case 0x02:
		case 0x03: // 0x0000 - 0x1FFF
			ram[adr & 0x7ff] = dat;
			break;
		case 0x04:
		case 0x05:
		case 0x06:
		case 0x07: // 0x2000 - 0x3FFF
			nes.getRegs().write(adr, dat);
			break;
		case 0x08:
		case 0x09:
		case 0x0A:
		case 0x0B: // 0x4000 - 0x5FFF
			if (adr < 0x4020)
				nes.getRegs().write(adr, dat);
			else if (nes.getMapper() != null)
				nes.getMapper().write(adr, dat);
			break;
		case 0x0C:
		case 0x0D:
		case 0x0E:
		case 0x0F: // 0x6000 - 0x7FFF
			if (sramEnabled)
				sram[adr & 0x1fff] = dat;
			else if (nes.getMapper() != null)
				nes.getMapper().write(adr, dat);
			break;

		case 0x10:
		case 0x11:
		case 0x12:
		case 0x13: // 0x8000 - 0xFFFF
		case 0x14:
		case 0x15:
		case 0x16:
		case 0x17:
		case 0x18:
		case 0x19:
		case 0x1A:
		case 0x1B:
		case 0x1C:
		case 0x1D:
		case 0x1E:
		case 0x1F:
			if (nes.getMapper() != null)
				nes.getMapper().write(adr, dat);
			break;
		}
	}

	public byte readChrRom(short adr) {
		if (isVram)
			return vram[chrPage[(adr >> 10) & 7] + (adr & 0x03ff)];
		else
			return vrom[chrPage[(adr >> 10) & 7] + (adr & 0x03ff)];
	}

	public void writeChrRom(short adr, byte dat) {
		if (isVram)
			vram[chrPage[(adr >> 10) & 7] + (adr & 0x03ff)] = dat;
		else
			vrom[chrPage[(adr >> 10) & 7] + (adr & 0x03ff)] = dat;
	}

	private byte[] rom, vrom, sram, vram;
	private byte[] ram = new byte[0x800];

	// byte[][] romPage = new byte[4][];
	// byte[][] chrPage = new byte[8][];
	private int[] romPage = new int[4];
	private int[] chrPage = new int[8];

	private boolean isVram;
	private boolean sramEnabled;

	private Nes nes;

	
	
	public Mbc getCopy() {
		Mbc copy = new Mbc(nes);
		
		 
		
		System.arraycopy(ram, 0, copy.ram, 0, copy.ram.length);
		System.arraycopy(romPage, 0, copy.romPage, 0, copy.romPage.length);
		System.arraycopy(chrPage, 0, copy.chrPage, 0, copy.chrPage.length);
		
		
		copy.rom = new byte[rom.length];		
		System.arraycopy(rom, 0, copy.rom, 0, copy.rom.length);
		
		copy.vrom = new byte[vrom.length];		
		System.arraycopy(vrom, 0, copy.vrom, 0, copy.vrom.length);
		
		copy.sram = new byte[sram.length];		
		System.arraycopy(sram, 0, copy.sram, 0, copy.sram.length);
		
		copy.vram = new byte[vram.length];		
		System.arraycopy(vram, 0, copy.vram, 0, copy.vram.length);
		 
		copy.isVram = this.isVram;
		copy.sramEnabled = this.sramEnabled;
		 
		
		return copy;
	}

	public void loadState(SaveState saveState) {
		Mbc copy = saveState.getMbc();
		
		System.arraycopy(copy.ram, 0, ram, 0, copy.ram.length);
		System.arraycopy(copy.romPage, 0, romPage, 0, copy.romPage.length);
		System.arraycopy(copy.chrPage, 0, chrPage, 0, copy.chrPage.length);
		
		
		
		System.arraycopy(copy.rom, 0, rom, 0, copy.rom.length);
		System.arraycopy(copy.vrom, 0, vrom, 0, copy.vrom.length);
		System.arraycopy(copy.sram, 0, sram, 0, copy.sram.length);
		System.arraycopy(copy.vram, 0, vram, 0, copy.vram.length);
		 
		
		for (int i = 0; i < 4; i++)
			mapRom(i, i);
		if (vrom != null)
			for (int i = 0; i < 8; i++)
				mapVrom(i, i);
		else
			for (int i = 0; i < 8; i++)
				mapVram(i, i);
		
		
		this.isVram = copy.isVram;
		this.sramEnabled = copy.sramEnabled;
	}
}

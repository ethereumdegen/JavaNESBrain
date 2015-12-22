package jp.tanakh.bjne.nes.mapper;

import jp.tanakh.bjne.nes.MapperAdapter;
import jp.tanakh.bjne.nes.Nes;
import jp.tanakh.bjne.nes.Ppu;

/**
 * Mapper 64: Tengen Rambo
 */
public class Rambo1 extends MapperAdapter {
	public Rambo1(Nes n) {
		nes = n;
	}

	@Override
	public int mapperNo() {
		return 64;
	}

	@Override
	public void reset() {
		romSize = nes.getRom().romSize();
		prgPage[0] = 0;
		prgPage[1] = 1;
		prgPage[2] = 2;
		chrPage[0] = 0;
		chrPage[1] = 1;
		chrPage[2] = 2;
		chrPage[3] = 3;
		chrPage[4] = 4;
		chrPage[5] = 5;
		chrPage[6] = 6;
		chrPage[7] = 7;
		chrPage[8] = 8;
		chrPage[9] = 9;
		chrPage[15] = 15;  //is this right?

		prgSwap = false;
		chrSwap = false;

		setRom();
		setVrom();
	}

	private static boolean _bit(int x, int n) {
		return ((x >> n) & 1) != 0;
	}

	@Override
	public void write(short adr, byte bdat) {
		System.out.println("addr" + adr);
		System.out.println("bdat" + bdat);
		
		int dat = bdat & 0xff;
		switch (adr & 0xE001) {
		case 0x8000:
			cmd = dat & 7;
			prgSwap = _bit(dat, 6);
			chrSwap = _bit(dat, 7);
			break;

		 
			
		//BANK SELECT
		case 0x8001:
			switch (cmd) {
			case 0:   // Select 2 1K VROM pages at PPU $0000
				chrPage[0] = dat & 0xfe;
				chrPage[1] = (dat & 0xfe) + 1;
				setVrom();
				break;
			case 1: // Select 2 1K VROM pages at PPU $0800
				chrPage[2] = dat & 0xfe;
				chrPage[3] = (dat & 0xfe) + 1;
				setVrom();
				break;
			case 2: // Select 1K VROM pages at PPU $1000
				chrPage[4] = dat;
				setVrom();
				break;
			case 3: // Select 1K VROM pages at PPU $1400
				chrPage[5] = dat;
				setVrom();
				break;
			case 4: // Select 1K VROM pages at PPU $1800
				chrPage[6] = dat;
				setVrom();
				break;
			case 5: // Select 1K VROM pages at PPU $1C00
				chrPage[7] = dat;
				setVrom();
				break;
			case 6: // Select first switchable ROM page
				prgPage[0] = dat;
				setRom();
				break;
			case 7: // Select second switchable ROM page
				prgPage[1] = dat;
				setRom();
				break;
			case 8: //Select 1K VROM page at PPU $0400 
				chrPage[8] = dat;
				setVrom();
				break;
			case 9: //Select 1K VROM page at PPU $0C00 
				chrPage[9] = dat;
				setVrom();
				break;
			case 15: //Select third switchable ROM page     
				prgPage[2] = dat;
				setRom();
				break;
			}
			break;

		case 0xA000:
			if (!nes.getRom().isFourScreen())
				nes.getPpu().setMirroring(
						(dat & 1) != 0 ? Ppu.MirrorType.HOLIZONTAL
								: Ppu.MirrorType.VERTICAL);
			break;
		case 0xA001:
			if ((dat & 0x80) != 0)
				; // enable SRAM
			else
				; // disable SRAM
			break;

		case 0xC000:
			irqCounter = dat;
			break;
		case 0xC001:
			irqLatch = dat;
			break;

		case 0xE000:
			irqEnable = false;
			irqCounter = irqLatch;
			break;
		case 0xE001:
			irqEnable = true;
			break;
		}
	}

	@Override
	public void hblank(int line) {
		if (irqEnable && line >= 0 && line < 239 && nes.getRegs().drawEnabled()) {

			if ((irqCounter--) == 0) {
				irqCounter = irqLatch;
				nes.getCpu().setIrq(true);
			}
		}
	}

	//fix me!
	private void setRom() {
		if (prgSwap) {
			nes.getMbc().mapRom(0, (romSize - 1) * 2);
			nes.getMbc().mapRom(1, prgPage[1]);
			nes.getMbc().mapRom(2, prgPage[0]);
			nes.getMbc().mapRom(3, (romSize - 1) * 2 + 1);
		} else {
			nes.getMbc().mapRom(0, prgPage[0]);
			nes.getMbc().mapRom(1, prgPage[1]);
			nes.getMbc().mapRom(2, (romSize - 1) * 2);
			nes.getMbc().mapRom(3, (romSize - 1) * 2 + 1);
		}
	}

	private void setVrom() {
		for (int i = 0; i < 8; i++)
			nes.getMbc().mapVrom((i + (chrSwap ? 4 : 0)) % 8, chrPage[i]);
	}

	private int romSize;

	private int cmd;
	private boolean prgSwap, chrSwap;

	private int irqCounter, irqLatch;
	private boolean irqEnable;

	private int[] prgPage = new int[3];
	private int[] chrPage = new int[16];

	private Nes nes;
}

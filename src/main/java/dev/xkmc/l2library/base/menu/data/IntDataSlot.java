package dev.xkmc.l2library.base.menu.data;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;

public class IntDataSlot {

	private final DataSlot hi, lo;

	public IntDataSlot(AbstractContainerMenu menu) {
		hi = menu.addDataSlot(DataSlot.standalone());
		lo = menu.addDataSlot(DataSlot.standalone());
	}

	public int get() {
		return hi.get() << 16 | lo.get();
	}

	public void set(int pc) {
		lo.set(pc & 0xFFFF);
		hi.set(pc >> 16);
	}

}

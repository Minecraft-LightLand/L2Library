package dev.xkmc.l2library.base.menu.data;

import net.minecraft.world.inventory.AbstractContainerMenu;

public class LongDataSlot {

	private final IntDataSlot lo, hi;

	public LongDataSlot(AbstractContainerMenu menu) {
		lo = new IntDataSlot(menu);
		hi = new IntDataSlot(menu);
	}

	public long get() {
		return ((long) hi.get()) << 32 | Integer.toUnsignedLong(lo.get());
	}

	public void set(long pc) {
		lo.set((int) (pc));
		hi.set((int) (pc >> 32));
	}

}

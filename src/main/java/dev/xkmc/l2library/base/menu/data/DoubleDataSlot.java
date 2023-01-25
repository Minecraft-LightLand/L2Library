package dev.xkmc.l2library.base.menu.data;

import net.minecraft.world.inventory.AbstractContainerMenu;

public class DoubleDataSlot {

	private final LongDataSlot data;

	public DoubleDataSlot(AbstractContainerMenu menu) {
		data = new LongDataSlot(menu);
	}

	public double get() {
		return Double.longBitsToDouble(data.get());
	}

	public void set(double pc) {
		data.set(Double.doubleToLongBits(pc));
	}

}

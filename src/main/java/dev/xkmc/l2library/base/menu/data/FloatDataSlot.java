package dev.xkmc.l2library.base.menu.data;

import net.minecraft.world.inventory.AbstractContainerMenu;

public class FloatDataSlot {

	private final IntDataSlot data;

	public FloatDataSlot(AbstractContainerMenu menu) {
		data = new IntDataSlot(menu);
	}

	public float get() {
		return Float.intBitsToFloat(data.get());
	}

	public void set(float pc) {
		data.set(Float.floatToIntBits(pc));
	}

}

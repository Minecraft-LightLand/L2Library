package dev.xkmc.l2library.base.menu.data;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;

public class BoolArrayDataSlot {

	private final DataSlot[] array;

	public BoolArrayDataSlot(AbstractContainerMenu menu, int size) {
		int n = size / 16 + (size % 16 == 0 ? 0 : 1);
		array = new DataSlot[n];
		for (int i = 0; i < n; i++) {
			array[i] = DataSlot.standalone();
		}
	}

	public boolean get(int i) {
		return (array[i >> 4].get() & (1 << (i & 0xf))) != 0;
	}

	public void set(boolean pc, int i) {
		int val = array[i >> 4].get();
		int mask = 1 << (i & 0xf);
		boolean old = (val & mask) != 0;
		if (old != pc) {
			val ^= mask;
			array[i >> 4].set(val);
		}
	}

}

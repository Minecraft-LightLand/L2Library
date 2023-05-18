package dev.xkmc.l2library.init.events.screen.source;

import dev.xkmc.l2serial.serialization.SerialClass;

@SerialClass
public class SimpleSlotData extends ItemSourceData<SimpleSlotData> {

	@SerialClass.SerialField
	private int slot;

	@Deprecated
	public SimpleSlotData() {

	}

	public SimpleSlotData(int slot) {
		this.slot = slot;
	}

	public int slot() {
		return slot;
	}

	@Override
	public boolean canReplace(SimpleSlotData other) {
		return slot == other.slot;
	}
}

package dev.xkmc.l2library.init.events.screen.source;

import dev.xkmc.l2serial.serialization.SerialClass;

@SerialClass
public final class SimpleSlotData extends ItemSourceData<SimpleSlotData> {

	@SerialClass.SerialField
	private final int slot;

	public SimpleSlotData(int slot) {
		this.slot = slot;
	}

	public int slot() {
		return slot;
	}

}

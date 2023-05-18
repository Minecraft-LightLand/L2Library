package dev.xkmc.l2library.init.events.screen.track;

import dev.xkmc.l2library.init.events.screen.source.PlayerSlot;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.item.Item;

@SerialClass
public class ItemBasedTraceData extends TrackedEntryData<ItemBasedTraceData> {

	@SerialClass.SerialField
	public PlayerSlot<?> parent;

	@SerialClass.SerialField
	public Item verifier;

	@Deprecated
	public ItemBasedTraceData() {

	}

	public ItemBasedTraceData(PlayerSlot<?> parent, Item verifier) {
		this.parent = parent;
		this.verifier = verifier;
	}

}

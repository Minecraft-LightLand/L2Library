package dev.xkmc.l2library.init.events.screen.track;

import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.item.ItemStack;

@SerialClass
public class QuickAccessTraceData extends TrackedEntryData<QuickAccessTraceData> {

	@SerialClass.SerialField
	public ItemStack stack;

	@Deprecated
	public QuickAccessTraceData() {

	}

	public QuickAccessTraceData(ItemStack stack) {
		this.stack = stack;
	}
}

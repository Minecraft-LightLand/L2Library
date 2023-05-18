package dev.xkmc.l2library.init.events.screen.track;

import net.minecraft.world.item.ItemStack;

public record QuickAccessTraceData(ItemStack stack)
		implements TrackedEntryData<QuickAccessTraceData> {

}

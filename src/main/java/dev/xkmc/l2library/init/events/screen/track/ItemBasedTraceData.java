package dev.xkmc.l2library.init.events.screen.track;

import dev.xkmc.l2library.init.events.screen.source.PlayerSlot;
import net.minecraft.world.item.Item;

public record ItemBasedTraceData(PlayerSlot<?> parent, Item verifier)
		implements TrackedEntryData<ItemBasedTraceData> {

}

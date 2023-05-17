package dev.xkmc.l2library.init.events.screen.track;

import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.l2library.init.events.screen.base.LayerPopType;
import dev.xkmc.l2library.init.events.screen.base.ScreenTrackerRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public abstract class TrackedEntryType<T extends TrackedEntryData<T>> extends NamedEntry<TrackedEntryType<?>> {

	public TrackedEntryType() {
		super(ScreenTrackerRegistry.TRACKED_ENTRY_TYPE);
	}

	public abstract LayerPopType restoreMenuNotifyClient(ServerPlayer player, T data, @Nullable Component comp);

	public abstract boolean match(AbstractContainerMenu current, T data);

}

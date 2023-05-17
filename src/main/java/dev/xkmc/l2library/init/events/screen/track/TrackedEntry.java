package dev.xkmc.l2library.init.events.screen.track;

import dev.xkmc.l2library.init.events.screen.base.LayerPopType;
import dev.xkmc.l2library.util.annotation.ServerOnly;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public record TrackedEntry<T extends TrackedEntryData<T>>(TrackedEntryType<T> type, T data, String title) {

	public static <T extends TrackedEntryData<T>> TrackedEntry<T> of(TrackedEntryType<T> type, T data, @Nullable Component title) {
		String str = title == null ? "" : Component.Serializer.toJson(title);
		return new TrackedEntry<>(type, data, str);
	}

	@ServerOnly
	public boolean match(AbstractContainerMenu current) {
		return type.match(current, data);
	}

	public LayerPopType restoreServerMenu(ServerPlayer player) {
		Component comp = Component.Serializer.fromJson(title);
		return type.restoreMenuNotifyClient(player, data, comp);
	}

}

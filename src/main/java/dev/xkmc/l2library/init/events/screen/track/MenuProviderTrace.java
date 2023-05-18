package dev.xkmc.l2library.init.events.screen.track;

import dev.xkmc.l2library.init.events.screen.base.LayerPopType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class MenuProviderTrace extends TrackedEntryType<MenuProviderTraceData> {
	@Override
	public LayerPopType restoreMenuNotifyClient(ServerPlayer player, MenuProviderTraceData data, @Nullable Component comp) {
		return data.cache().restore(player) ? LayerPopType.CLEAR : LayerPopType.FAIL;
	}
}

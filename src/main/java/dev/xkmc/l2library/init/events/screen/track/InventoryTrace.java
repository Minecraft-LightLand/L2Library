package dev.xkmc.l2library.init.events.screen.track;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.events.screen.base.LayerPopType;
import dev.xkmc.l2library.init.events.screen.packets.ScreenType;
import dev.xkmc.l2library.init.events.screen.packets.SetScreenToClient;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class InventoryTrace extends TrackedEntryType<NoData> {

	@Override
	public LayerPopType restoreMenuNotifyClient(ServerPlayer player, NoData data, @Nullable Component comp) {
		player.doCloseContainer();
		L2Library.PACKET_HANDLER.toClientPlayer(new SetScreenToClient(ScreenType.PLAYER), player);
		return LayerPopType.CLEAR;
	}

	@Override
	public boolean match(NoData self, NoData other) {
		return true;
	}

}

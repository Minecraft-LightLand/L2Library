package dev.xkmc.l2library.init.compat.track;

import dev.xkmc.l2library.init.compat.L2CuriosCompat;
import dev.xkmc.l2library.init.events.screen.base.LayerPopType;
import dev.xkmc.l2library.init.events.screen.track.NoData;
import dev.xkmc.l2library.init.events.screen.track.TrackedEntryType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class CurioTabTrace extends TrackedEntryType<NoData> {

	@Override
	public LayerPopType restoreMenuNotifyClient(ServerPlayer player, NoData data, @Nullable Component comp) {
		L2CuriosCompat.openCuriosTab(player);
		return LayerPopType.CLEAR;
	}

	@Override
	public boolean match(NoData self, NoData other) {
		return true;
	}

}

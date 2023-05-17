package dev.xkmc.l2library.init.events.screen.track;

import dev.xkmc.l2library.init.events.click.quickaccess.QuickAccessClickHandler;
import dev.xkmc.l2library.init.events.screen.base.LayerPopType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class QuickAccessTrace extends TrackedEntryType<QuickAccessTraceData> {

	@Override
	public LayerPopType restoreMenuNotifyClient(ServerPlayer player, QuickAccessTraceData data, @Nullable Component comp) {
		QuickAccessClickHandler.INS.handle(player, data.stack);
		return LayerPopType.REMAIN;
	}

	@Override
	public boolean match(QuickAccessTraceData self, QuickAccessTraceData other) {
		return self.stack.getItem() == other.stack.getItem();
	}

}

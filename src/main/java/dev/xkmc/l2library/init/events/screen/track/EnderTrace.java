package dev.xkmc.l2library.init.events.screen.track;

import dev.xkmc.l2library.init.events.screen.base.LayerPopType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class EnderTrace extends TrackedEntryType<NoData> {

	@Override
	public LayerPopType restoreMenuNotifyClient(ServerPlayer player, NoData data, @Nullable Component comp) {
		if (comp == null) comp = Component.translatable("container.enderchest");
		NetworkHooks.openScreen(player, new SimpleMenuProvider((wid, inv, pl) ->
				ChestMenu.threeRows(wid, inv, pl.getEnderChestInventory()), comp));
		return LayerPopType.REMAIN;
	}

	@Override
	public boolean match(NoData self, NoData other) {
		return true;
	}

}

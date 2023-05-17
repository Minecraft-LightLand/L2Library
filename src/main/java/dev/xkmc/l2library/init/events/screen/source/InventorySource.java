package dev.xkmc.l2library.init.events.screen.source;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class InventorySource extends ItemSource<SimpleSlotData> {

	@Override
	public ItemStack getItem(Player player, SimpleSlotData data) {
		return player.getInventory().getItem(data.slot());
	}

}

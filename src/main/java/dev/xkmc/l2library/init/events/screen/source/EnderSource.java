package dev.xkmc.l2library.init.events.screen.source;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class EnderSource extends ItemSource<SimpleSlotData> {

	@Override
	public ItemStack getItem(Player player, SimpleSlotData data) {
		return player.getEnderChestInventory().getItem(data.slot());
	}

}

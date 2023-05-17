package dev.xkmc.l2library.init.compat.track;

import dev.xkmc.l2library.init.compat.L2CuriosCompat;
import dev.xkmc.l2library.init.events.screen.source.ItemSource;
import dev.xkmc.l2library.init.events.screen.source.SimpleSlotData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CurioSource extends ItemSource<SimpleSlotData> {

	@Override
	public ItemStack getItem(Player player, SimpleSlotData data) {
		return L2CuriosCompat.getItemFromSlot(player, data.slot());
	}

}

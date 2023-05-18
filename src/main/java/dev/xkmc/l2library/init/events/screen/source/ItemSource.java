package dev.xkmc.l2library.init.events.screen.source;

import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.l2library.init.events.screen.base.ScreenTrackerRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class ItemSource<T extends Record & ItemSourceData<T>> extends NamedEntry<ItemSource<?>> {

	public ItemSource() {
		super(ScreenTrackerRegistry.ITEM_SOURCE);
	}

	public abstract ItemStack getItem(Player player, T data);

}

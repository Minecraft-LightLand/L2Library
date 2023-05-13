package dev.xkmc.l2library.init.events.click.quickaccess;

import dev.xkmc.l2library.init.data.L2TagGen;
import dev.xkmc.l2library.init.events.click.SlotClickHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class QuickAccessClickHandler extends SlotClickHandler {

	private static final Map<Item, QuickAccessAction> MAP = new HashMap<>();

	public static synchronized void register(Item item, QuickAccessAction action) {
		MAP.put(item, action);
	}

	public QuickAccessClickHandler(ResourceLocation rl) {
		super(rl);
	}

	@Override
	public boolean isAllowed(ItemStack stack) {
		return stack.is(L2TagGen.QUICK_ACCESS_VANILLA);
	}

	@Override
	protected void handle(ServerPlayer player, ItemStack stack) {
		QuickAccessAction accessAction = MAP.get(stack.getItem());
		if (accessAction != null) {
			accessAction.perform(player, stack);
		}
	}
}

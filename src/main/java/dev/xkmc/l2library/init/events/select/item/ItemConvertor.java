package dev.xkmc.l2library.init.events.select.item;

import dev.xkmc.l2library.init.data.L2TagGen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ItemConvertor {

	private static ItemStack find(List<ItemStack> ans, Inventory inv) {
		for (ItemStack choice : ans) {
			if (inv.hasRemainingSpaceForItem(inv.getItem(inv.selected), choice)) {
				return choice;
			}
		}
		for (ItemStack choice : ans) {
			if (inv.hasRemainingSpaceForItem(inv.getItem(40), choice)) {
				return choice;
			}
		}
		for (ItemStack choice : ans) {
			for (int i = 0; i < inv.items.size(); ++i) {
				if (inv.hasRemainingSpaceForItem(inv.items.get(i), choice)) {
					return choice;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack convert(ItemStack stack, Player player) {
		if (!stack.is(L2TagGen.SELECTABLE)) {
			return stack;
		}
		ItemSelector ans = null;
		for (ItemSelector selector : ItemSelector.SELECTORS) {
			if (selector.test(stack)) {
				ans = selector;
				break;
			}
		}
		if (ans == null) {
			return stack;
		}
		Inventory inv = player.getInventory();
		List<ItemStack> list = ans.getList();
		ItemStack old = find(List.of(stack), inv);
		if (!old.isEmpty()) {
			return stack;
		}
		ItemStack found = find(list, inv);
		if (found.isEmpty()) {
			return stack;
		}
		ItemStack result = found.copy();
		result.setCount(stack.getCount());
		return result;
	}

	@SubscribeEvent
	public static void addItemToInventory(EntityItemPickupEvent event) {
		ItemStack prev = event.getItem().getItem();
		ItemStack next = convert(prev, event.getEntity());
		if (next != prev) {
			event.getItem().setItem(next);
			event.setCanceled(true);
		}
	}

}

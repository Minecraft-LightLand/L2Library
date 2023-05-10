package dev.xkmc.l2library.init.events.select.item;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.data.L2TagGen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemSelector extends IItemSelector {

	public static final List<ItemSelector> SELECTORS = new ArrayList<>();

	private final List<ItemStack> list;
	private final Set<Item> set = new HashSet<>();

	public ItemSelector(ResourceLocation rl, ItemStack... stacks) {
		super(rl);
		list = List.of(stacks);
		for (ItemStack stack : stacks) {
			if (!stack.is(L2TagGen.SELECTABLE)) {
				L2Library.LOGGER.error("item " + stack.getItem() + " is not marked as selectable");
			}
			set.add(stack.getItem());
		}
		SELECTORS.add(this);
	}

	public boolean test(ItemStack stack) {
		return set.contains(stack.getItem());
	}

	public int getIndex(Player player) {
		for (int i = 0; i < list.size(); i++) {
			if (test(player.getMainHandItem())) {
				if (list.get(i).getItem() == player.getMainHandItem().getItem()) return i;
			} else if (test(player.getOffhandItem())) {
				if (list.get(i).getItem() == player.getMainHandItem().getItem()) return i;
			}
		}
		return 0;
	}

	public List<ItemStack> getList() {
		return list;
	}

}

package dev.xkmc.l2library.init.events.select.item;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.data.L2TagGen;
import dev.xkmc.l2library.serial.config.BaseConfig;
import dev.xkmc.l2library.serial.config.CollectType;
import dev.xkmc.l2library.serial.config.ConfigCollect;
import dev.xkmc.l2library.util.annotation.DataGenOnly;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SerialClass
public class SimpleItemSelectConfig extends BaseConfig {

	@Nullable
	public static ItemSelector get(ItemStack stack) {
		SimpleItemSelectConfig config = L2Library.ITEM_SELECTOR.getMerged();
		return config.find(stack);
	}

	@ConfigCollect(CollectType.MAP_COLLECT)
	@SerialClass.SerialField
	public final HashMap<ResourceLocation, ArrayList<Item>> map = new HashMap<>();

	private HashMap<Item, ItemSelector> cache;

	@Override
	protected void postMerge() {
		cache = new HashMap<>();
		for (var ent : map.entrySet()) {
			ItemSelector sel = new ItemSelector(ent.getKey(), ent.getValue()
					.stream().map(Item::getDefaultInstance)
					.toArray(ItemStack[]::new));
			for (Item item : ent.getValue()) {
				cache.put(item, sel);
			}
		}
	}

	@Nullable
	private ItemSelector find(ItemStack stack) {
		if (!stack.is(L2TagGen.SELECTABLE)) return null;
		return cache.get(stack.getItem());
	}

	@DataGenOnly
	public SimpleItemSelectConfig add(ResourceLocation id, Item... items) {
		map.put(id, new ArrayList<>(List.of(items)));
		return this;
	}

}

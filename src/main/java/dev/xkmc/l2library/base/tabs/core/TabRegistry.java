package dev.xkmc.l2library.base.tabs.core;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class TabRegistry {

	private static final Map<Integer, TabToken<?>> TABS = new TreeMap<>();

	private static List<TabToken<?>> cache;

	public TabRegistry() {
	}

	/**
	 * 0 - Inventory
	 * 1000 - Attributes
	 * 2000 - Curios
	 * 3000 - Artifacts
	 */
	public static synchronized <T extends BaseTab<T>> TabToken<T> registerTab(int priority, TabToken.TabFactory<T> sup, Supplier<Item> item, Component title) {
		cache = null;
		TabToken<T> ans = new TabToken<>(sup, item, title);
		while (TABS.containsKey(priority)) {
			priority++;
		}
		TABS.put(priority, ans);
		return ans;
	}

	public static List<TabToken<?>> getTabs() {
		refreshIndex();
		return cache;
	}

	public static void refreshIndex() {
		if (cache != null) return;
		cache = new ArrayList<>(TABS.values());
		for (int i = 0; i < cache.size(); i++) {
			cache.get(i).index = i;
		}
	}
}

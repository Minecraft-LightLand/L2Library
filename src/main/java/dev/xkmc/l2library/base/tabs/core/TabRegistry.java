package dev.xkmc.l2library.base.tabs.core;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class TabRegistry {

	private static final ArrayList<TabToken<?>> TABS = new ArrayList<>();

	public TabRegistry() {
	}

	public static <T extends BaseTab<T>> TabToken<T> registerTab(TabToken.TabFactory<T> sup, Supplier<Item> item, Component title) {
		TabToken<T> ans = new TabToken<>(TABS.size(), sup, item, title);
		TABS.add(ans);
		return ans;
	}

	public static ArrayList<TabToken<?>> getTabs() {
		return TABS;
	}

}

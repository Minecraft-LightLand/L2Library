package dev.xkmc.l2library.menu.tabs.core;

import dev.xkmc.l2library.init.L2Client;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.function.Predicate;
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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.xkmc.l2library.menu.tabs.contents;

import dev.xkmc.l2library.init.L2Client;
import dev.xkmc.l2library.menu.tabs.core.BaseTab;
import dev.xkmc.l2library.menu.tabs.core.TabManager;
import dev.xkmc.l2library.menu.tabs.core.TabToken;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;

import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class TabInventory extends BaseTab<TabInventory> {

	public static Predicate<Screen> inventoryTest = e -> e instanceof InventoryScreen;
	public static Runnable openInventory = () -> Minecraft.getInstance().setScreen(new InventoryScreen(Proxy.getClientPlayer()));

	public static void guiPostInit(ScreenEvent.InitScreenEvent.Post event) {
		if (inventoryTest.test(event.getScreen())) {
			TabManager manager = new TabManager(event.getScreen());
			manager.init(event::addListener, L2Client.TAB_INVENTORY);
		}

	}

	public TabInventory(TabToken<TabInventory> token, TabManager manager, ItemStack stack, Component title) {
		super(token, manager, stack, title);
	}

	public void onTabClicked() {
		openInventory.run();
	}

}

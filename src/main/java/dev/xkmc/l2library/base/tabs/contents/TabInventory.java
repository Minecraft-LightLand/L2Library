//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.xkmc.l2library.base.tabs.contents;

import dev.xkmc.l2library.base.tabs.core.BaseTab;
import dev.xkmc.l2library.base.tabs.core.TabManager;
import dev.xkmc.l2library.base.tabs.core.TabToken;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.L2LibraryClient;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Predicate;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TabInventory extends BaseTab<TabInventory> {

	public static Predicate<Screen> inventoryTest = e -> e instanceof InventoryScreen;
	public static Runnable openInventory = () -> Minecraft.getInstance().setScreen(new InventoryScreen(Proxy.getClientPlayer()));

	@SubscribeEvent
	public static void guiPostInit(ScreenEvent.Init.Post event) {
		if (inventoryTest.test(event.getScreen())) {
			TabManager manager = new TabManager(event.getScreen());
			manager.init(event::addListener, L2LibraryClient.TAB_INVENTORY);
		}

	}

	public TabInventory(TabToken<TabInventory> token, TabManager manager, ItemStack stack, Component title) {
		super(token, manager, stack, title);
	}

	public void onTabClicked() {
		openInventory.run();
	}

}

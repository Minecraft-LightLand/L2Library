//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.xkmc.l2library.menu.tabs.contents;

import dev.xkmc.l2library.menu.tabs.core.BaseTab;
import dev.xkmc.l2library.menu.tabs.core.TabManager;
import dev.xkmc.l2library.menu.tabs.core.TabToken;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TabInventory extends BaseTab<TabInventory> {

	public TabInventory(TabToken<TabInventory> token, TabManager manager, ItemStack stack, Component title) {
		super(token, manager, stack, title);
	}

	public void onTabClicked() {
		Minecraft.getInstance().setScreen(new InventoryScreen(Proxy.getClientPlayer()));
	}

}

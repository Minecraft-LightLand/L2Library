package dev.xkmc.l2library.base.tabs.contents;

import dev.xkmc.l2library.base.tabs.core.BaseTab;
import dev.xkmc.l2library.base.tabs.core.TabManager;
import dev.xkmc.l2library.base.tabs.core.TabToken;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class TabAttributes extends BaseTab<TabAttributes> {

	public TabAttributes(TabToken<TabAttributes> token, TabManager manager, ItemStack stack, Component title) {
		super(token, manager, stack, title);
	}

	@Override
	public void onTabClicked() {
		Minecraft.getInstance().setScreen(new AttributeScreen(this.getMessage()));
	}

}

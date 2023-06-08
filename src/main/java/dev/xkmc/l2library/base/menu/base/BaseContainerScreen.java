package dev.xkmc.l2library.base.menu.base;

import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class BaseContainerScreen<T extends BaseContainerMenu<T>> extends AbstractContainerScreen<T> {

	public BaseContainerScreen(T cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
		this.imageHeight = menu.sprite.get().getHeight();
		this.inventoryLabelY = menu.sprite.get().getPlInvY() - 11;
	}

	@Override
	public void render(GuiGraphics g, int mx, int my, float partial) {
		super.render(g, mx, my, partial);
		renderTooltip(g, mx, my);
	}

	protected boolean click(int btn) {
		if (menu.clickMenuButton(Proxy.getClientPlayer(), btn) && Minecraft.getInstance().gameMode != null) {
			Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, btn);
			return true;
		}
		return false;
	}

}

package dev.xkmc.l2library.base.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class BaseContainerScreen<T extends BaseContainerMenu<T>> extends AbstractContainerScreen<T> {

	public BaseContainerScreen(T cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
		this.imageHeight = menu.sprite.getHeight();
		this.inventoryLabelY = menu.sprite.getPlInvY() - 11;
	}

	@Override
	public void render(PoseStack stack, int mx, int my, float partial) {
		super.render(stack, mx, my, partial);
		renderTooltip(stack, mx, my);
	}

	protected boolean click(int btn) {
		if (menu.clickMenuButton(Proxy.getClientPlayer(), btn)) {
			Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, btn);
			return true;
		}
		return false;
	}

}

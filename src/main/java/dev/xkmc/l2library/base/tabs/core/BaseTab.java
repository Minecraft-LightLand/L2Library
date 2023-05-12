package dev.xkmc.l2library.base.tabs.core;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public abstract class BaseTab<T extends BaseTab<T>> extends Button {

	protected final static ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");

	public final ItemStack stack;
	public final TabToken<T> token;
	public final TabManager manager;

	@SuppressWarnings("unchecked")
	public BaseTab(TabToken<T> token, TabManager manager, ItemStack stack, Component title) {
		super(0, 0, 26, 32, title, b -> ((T) b).onTabClicked(), Supplier::get);
		this.stack = stack;
		this.token = token;
		this.manager = manager;
	}

	public abstract void onTabClicked();

	public void onTooltip(PoseStack stack, int x, int y) {
		manager.getScreen().renderTooltip(stack, getMessage(), x, y);
	}

	public void renderBackground(PoseStack stack) {
		if (this.visible) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableBlend();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, TEXTURE);
			token.type.draw(stack, manager.getScreen(), getX(), getY(), manager.selected == token, token.getIndex());
			RenderSystem.defaultBlendFunc();
			if (!this.stack.isEmpty())
				token.type.drawIcon(stack, getX(), getY(), token.getIndex(), Minecraft.getInstance().getItemRenderer(), this.stack);
		}
	}

	public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		if (manager.selected == token) {
			renderBackground(stack);
		}
		if (this.token.getIndex() == TabRegistry.getTabs().size() - 1) { // draw on last
			manager.onToolTipRender(stack, mouseX, mouseY);
		}
	}

}

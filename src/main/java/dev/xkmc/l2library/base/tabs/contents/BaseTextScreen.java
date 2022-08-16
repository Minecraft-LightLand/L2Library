package dev.xkmc.l2library.base.tabs.contents;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class BaseTextScreen extends Screen {

	private final ResourceLocation texture;

	public int imageWidth, imageHeight, leftPos, topPos;

	protected BaseTextScreen(Component title, ResourceLocation texture) {
		super(title);
		this.texture = texture;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	public void init() {
		this.leftPos = (this.width - this.imageWidth) / 2;
		this.topPos = (this.height - this.imageHeight) / 2;
	}

	@Override
	public void render(PoseStack stack, int mx, int my, float ptick) {
		this.fillGradient(stack, 0, 0, this.width, this.height, -1072689136, -804253680);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, this.texture);
		int i = this.leftPos;
		int j = this.topPos;
		this.blit(stack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		super.render(stack, mx, my, ptick);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

}

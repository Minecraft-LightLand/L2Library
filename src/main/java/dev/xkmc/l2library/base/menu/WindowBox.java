package dev.xkmc.l2library.base.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;

public class WindowBox extends GuiComponent {

	private Screen parent;
	public int x, y, w, h, margin;

	public void setSize(Screen parent, int x, int y, int w, int h, int sh) {
		this.x = x + sh;
		this.y = y + sh;
		this.w = w - sh * 2;
		this.h = h - sh * 2;
		this.margin = sh;
		this.parent = parent;
	}

	public boolean isMouseIn(double mx, double my, int sh) {
		return mx > x - sh && mx < x + w + sh && my > y - sh && my < y + h + sh;
	}

	public void render(PoseStack matrix, int sh, int color, RenderType type) {
		if (type == RenderType.FILL) {
			fill(matrix, x - sh, y - sh, x + w + sh, y + h + sh, color);
		} else if (type == RenderType.MARGIN) {
			fill(matrix, x - sh, y - sh, x + w + sh, y, color);
			fill(matrix, x - sh, y + h, x + w + sh, y + h + sh, color);
			fill(matrix, x - sh, y, x, y + h, color);
			fill(matrix, x + w, y, x + w + sh, y + h, color);
		} else {
			fill(matrix, 0, 0, parent.width, y, color);
			fill(matrix, 0, y + h, parent.width, parent.height, color);
			fill(matrix, 0, y, x, y + h, color);
			fill(matrix, x + w, y, parent.width, y + h, color);
		}
	}

	public void blit(PoseStack matrix, int sh, int tx, int ty) {
		blit(matrix, x - sh, y - sh, tx, ty, w + 2 * sh, h + 2 * sh);
	}

	public void startClip(PoseStack matrix) {
		matrix.pushPose();
		RenderSystem.enableDepthTest();
		matrix.translate(0.0F, 0.0F, 950.0F);
		RenderSystem.colorMask(false, false, false, false);
		fill(matrix, 4680, 2260, -4680, -2260, -16777216);
		RenderSystem.colorMask(true, true, true, true);
		matrix.translate(0.0F, 0.0F, -950.0F);
		RenderSystem.depthFunc(518);
		fill(matrix, x, y, x + w, y + h, -16777216);
		RenderSystem.depthFunc(515);
	}

	public void endClip(PoseStack matrix) {
		RenderSystem.depthFunc(518);
		matrix.translate(0.0F, 0.0F, -950.0F);
		RenderSystem.colorMask(false, false, false, false);
		fill(matrix, 4680, 2260, -4680, -2260, -16777216);
		RenderSystem.colorMask(true, true, true, true);
		matrix.translate(0.0F, 0.0F, 950.0F);
		RenderSystem.depthFunc(515);
		matrix.popPose();
	}

	public enum RenderType {
		MARGIN, FILL, MARGIN_ALL
	}

}

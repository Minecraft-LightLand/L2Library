package dev.xkmc.l2library.base.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.List;

public class OverlayUtils extends GuiComponent {

	public final int screenWidth, screenHeight;

	public int bg = 0xf0100010;
	public int bs = 0x505000FF;
	public int be = 0x5028007f;
	public int tc = 0xFFFFFFFF;

	public OverlayUtils(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	public int getX(int w) {
		return Math.round(screenWidth / 8f);
	}

	public int getY(int h) {
		return Math.round((screenHeight - h) / 2f);
	}

	public void renderLongText(ForgeGui gui, PoseStack stack, List<Component> list) {
		Font font = gui.getFont();
		int tooltipTextWidth = list.stream().mapToInt(font::width).max().orElse(0);
		int maxWidth = screenWidth / 4;
		List<FormattedCharSequence> ans = list.stream().flatMap(text -> font.split(text, maxWidth).stream()).toList();
		int h = ans.size() * 12;
		int w = Math.min(tooltipTextWidth, maxWidth);
		int x0 = getX(w);
		int y0 = getY(h);

		int y1 = y0;
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		Matrix4f matrix4f = stack.last().pose();
		fillGradient(matrix4f, bufferbuilder, x0 - 3, y1 - 4, x0 + w + 3, y1 - 3, 400, bg, bg);
		fillGradient(matrix4f, bufferbuilder, x0 - 3, y1 + h + 3, x0 + w + 3, y1 + h + 4, 400, bg, bg);
		fillGradient(matrix4f, bufferbuilder, x0 - 3, y1 - 3, x0 + w + 3, y1 + h + 3, 400, bg, bg);
		fillGradient(matrix4f, bufferbuilder, x0 - 4, y1 - 3, x0 - 3, y1 + h + 3, 400, bg, bg);
		fillGradient(matrix4f, bufferbuilder, x0 + w + 3, y1 - 3, x0 + w + 4, y1 + h + 3, 400, bg, bg);
		fillGradient(matrix4f, bufferbuilder, x0 - 3, y1 - 3 + 1, x0 - 3 + 1, y1 + h + 3 - 1, 400, bs, be);
		fillGradient(matrix4f, bufferbuilder, x0 + w + 2, y1 - 3 + 1, x0 + w + 3, y1 + h + 3 - 1, 400, bs, be);
		fillGradient(matrix4f, bufferbuilder, x0 - 3, y1 - 3, x0 + w + 3, y1 - 3 + 1, 400, bs, bs);
		fillGradient(matrix4f, bufferbuilder, x0 - 3, y1 + h + 2, x0 + w + 3, y1 + h + 3, 400, be, be);
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		BufferUploader.drawWithShader(bufferbuilder.end());
		RenderSystem.enableTexture();
		for (FormattedCharSequence text : ans) {
			font.draw(stack, text, x0, y0, tc);
			y0 += 12;
		}
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}

}

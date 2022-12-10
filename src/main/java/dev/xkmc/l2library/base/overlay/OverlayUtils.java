package dev.xkmc.l2library.base.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import dev.xkmc.l2library.init.L2LibraryConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.List;

public class OverlayUtils extends GuiComponent {

	private static int getBGColor() {
		return (int) (Math.round(L2LibraryConfig.CLIENT.infoAlpha.get() * 255)) << 24 | 0x100010;
	}

	public final int screenWidth, screenHeight;

	public int bg = getBGColor();
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

	public int getMaxWidth() {
		return screenWidth / 4;
	}

	/**
	 * x margin: 4 on either side
	 * y margin: 4 on either side
	 * row height: 10
	 * row spacing: 2
	 */
	public void renderLongText(ForgeGui gui, PoseStack stack, int x0, int y0, int maxWidth, List<Component> list) {
		Font font = gui.getFont();
		int tooltipTextWidth = list.stream().mapToInt(font::width).max().orElse(0);
		if (maxWidth < 0) maxWidth = getMaxWidth();
		int finalMaxWidth = maxWidth;
		List<FormattedCharSequence> ans = list.stream().flatMap(text -> font.split(text, finalMaxWidth).stream()).toList();
		int h = ans.size() * 12 - 2;
		int w = Math.min(tooltipTextWidth, maxWidth);
		if (x0 < 0) x0 = getX(w);
		if (y0 < 0) y0 = getY(h);
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
		RenderSystem.disableDepthTest();
		BufferUploader.drawWithShader(bufferbuilder.end());
		RenderSystem.enableTexture();
		for (FormattedCharSequence text : ans) {
			font.draw(stack, text, x0, y0, tc);
			y0 += 12;
		}
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}

	public void renderLongText(ForgeGui gui, PoseStack stack, List<Component> list) {
		renderLongText(gui, stack, -1, -1, -1, list);
	}

	public static void drawRect(BufferBuilder builder, int x, int y, int w, int h, int r, int g, int b, int a) {
		fillRect(builder, x - 1, y - 1, w + 2, 1, r, g, b, a);
		fillRect(builder, x - 1, y - 1, 1, h + 2, r, g, b, a);
		fillRect(builder, x - 1, y + h, w + 2, 1, r, g, b, a);
		fillRect(builder, x + w, y - 1, 1, h + 2, r, g, b, a);
	}

	public static void fillRect(BufferBuilder builder, int x, int y, int w, int h, int r, int g, int b, int a) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		builder.vertex(x, y, 0.0D).color(r, g, b, a).endVertex();
		builder.vertex(x, y + h, 0.0D).color(r, g, b, a).endVertex();
		builder.vertex(x + w, y + h, 0.0D).color(r, g, b, a).endVertex();
		builder.vertex(x + w, y, 0.0D).color(r, g, b, a).endVertex();
		BufferUploader.drawWithShader(builder.end());
	}

}

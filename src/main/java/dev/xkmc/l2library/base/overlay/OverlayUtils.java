package dev.xkmc.l2library.base.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.xkmc.l2library.init.L2LibraryConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.joml.Matrix4f;

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
	 * FIXME add client tooltip
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
		TooltipRenderUtil.renderTooltipBackground(GuiComponent::fillGradient, matrix4f, bufferbuilder, x0, y0, w, h, 400);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableDepthTest();
		BufferUploader.drawWithShader(bufferbuilder.end());
		for (FormattedCharSequence text : ans) {
			font.draw(stack, text, x0, y0, tc);
			y0 += 12;
		}
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}

	public void renderTooltipInternal(PoseStack pose, List<ClientTooltipComponent> list, int p_263065_, int p_262996_, ClientTooltipPositioner p_262920_) {
		if (list.isEmpty()) return;
		int w = 0;
		int h = list.size() == 1 ? -2 : 0;

		Font font = Minecraft.getInstance().font;
		ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
		for (ClientTooltipComponent clienttooltipcomponent : list) {
			int k = clienttooltipcomponent.getWidth(font);
			if (k > w) {
				w = k;
			}

			h += clienttooltipcomponent.getHeight();
		}

		int x = getX(w);
		int y = getY(h);
		pose.pushPose();
		int z = 400;
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		Matrix4f matrix4f = pose.last().pose();
		TooltipRenderUtil.renderTooltipBackground(GuiComponent::fillGradient, matrix4f, bufferbuilder, x, y, w, h, z);
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		BufferUploader.drawWithShader(bufferbuilder.end());
		MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		pose.translate(0.0F, 0.0F, z);
		int iy = y;

		for (int i = 0; i < list.size(); ++i) {
			ClientTooltipComponent comp = list.get(i);
			comp.renderText(font, x, iy, matrix4f, buffer);
			iy += comp.getHeight() + (i == 0 ? 2 : 0);
		}

		buffer.endBatch();
		iy = y;

		for (int i = 0; i < list.size(); ++i) {
			ClientTooltipComponent comp = list.get(i);
			comp.renderImage(font, x, iy, pose, ir);
			iy += comp.getHeight() + (i == 0 ? 2 : 0);
		}

		pose.popPose();

	}


	public void renderLongText(ForgeGui gui, PoseStack stack, List<Component> list) {
		renderLongText(gui, stack, -1, -1, -1, list);
	}

	public static void drawRect(BufferBuilder builder, float x, float y, float w, float h, int r, int g, int b, int a) {
		fillRect(builder, x - 1, y - 1, w + 2, 1, r, g, b, a);
		fillRect(builder, x - 1, y - 1, 1, h + 2, r, g, b, a);
		fillRect(builder, x - 1, y + h, w + 2, 1, r, g, b, a);
		fillRect(builder, x + w, y - 1, 1, h + 2, r, g, b, a);
	}

	public static void fillRect(BufferBuilder builder, float x, float y, float w, float h, int r, int g, int b, int a) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		builder.vertex(x, y, 0.0D).color(r, g, b, a).endVertex();
		builder.vertex(x, y + h, 0.0D).color(r, g, b, a).endVertex();
		builder.vertex(x + w, y + h, 0.0D).color(r, g, b, a).endVertex();
		builder.vertex(x + w, y, 0.0D).color(r, g, b, a).endVertex();
		BufferUploader.drawWithShader(builder.end());
	}

}

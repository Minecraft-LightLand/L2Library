package dev.xkmc.l2library.base.overlay;

import dev.xkmc.l2library.init.L2LibraryConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.List;

public class OverlayUtil implements ClientTooltipPositioner {

	private static int getBGColor() {
		return (int) (Math.round(L2LibraryConfig.CLIENT.infoAlpha.get() * 255)) << 24 | 0x100010;
	}

	public int bg = getBGColor();
	public int bs = 0x505000FF;
	public int be = 0x5028007f;
	public int tc = 0xFFFFFFFF;

	protected final GuiGraphics g;
	protected final int x0, y0, maxW;

	public OverlayUtil(GuiGraphics g, int x0, int y0, int maxW) {
		this.g = g;
		this.x0 = x0;
		this.y0 = y0;
		this.maxW = maxW < 0 ? getMaxWidth() : maxW;
	}

	public int getMaxWidth() {
		return g.guiWidth() / 4;
	}

	public void renderLongText(Font font, List<Component> list) {
		List<ClientTooltipComponent> ans = list.stream().flatMap(text -> font.split(text, maxW).stream())
				.map(ClientTooltipComponent::create).toList();
		renderTooltipInternal(font, ans);
	}

	public void renderTooltipInternal(Font font, List<ClientTooltipComponent> list) {
		if (list.isEmpty()) return;
		int w = 0;
		int h = list.size() == 1 ? -2 : 0;
		for (ClientTooltipComponent c : list) {
			int wi = c.getWidth(font);
			if (wi > w) {
				w = wi;
			}
			h += c.getHeight();
		}
		int wf = w;
		int hf = h;
		Vector2ic pos = positionTooltip(g.guiWidth(), g.guiHeight(), x0, y0, wf, hf);
		int xf = pos.x();
		int yf = pos.y();
		g.pose().pushPose();
		int z = 400;
		g.drawManaged(() -> TooltipRenderUtil.renderTooltipBackground(g, xf, yf, wf, hf, z, bg, bg, bs, be));
		g.pose().translate(0.0F, 0.0F, z);
		int yi = yf;
		for (int i = 0; i < list.size(); ++i) {
			ClientTooltipComponent c = list.get(i);
			c.renderText(font, xf, yi, g.pose().last().pose(), g.bufferSource());
			yi += c.getHeight() + (i == 0 ? 2 : 0);
		}
		yi = yf;
		for (int i = 0; i < list.size(); ++i) {
			ClientTooltipComponent c = list.get(i);
			c.renderImage(font, xf, yi, g);
			yi += c.getHeight() + (i == 0 ? 2 : 0);
		}
		g.pose().popPose();
	}


	@Override
	public Vector2ic positionTooltip(int gw, int gh, int x, int y, int tw, int th) {
		if (x < 0) x = Math.round(gw / 8f);
		if (y < 0) y = Math.round((gh - th) / 2f);
		return new Vector2i(x, y);
	}

}

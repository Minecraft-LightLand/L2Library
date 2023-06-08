package dev.xkmc.l2library.base.overlay;

import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class TextBox extends OverlayUtil {

	private final int anchorX, anchorY;

	public TextBox(GuiGraphics g, int anchorX, int anchorY, int x, int y, int width) {
		super(g, x, y, width);
		this.anchorX = anchorX;
		this.anchorY = anchorY;
	}

	@Override
	public Vector2ic positionTooltip(int gw, int gh, int x, int y, int tw, int th) {
		return new Vector2i(x - tw * anchorX / 2, y - th * anchorY / 2);
	}

	@Override
	public int getMaxWidth() {
		if (anchorX == 0) return g.guiWidth() - x0 - 8;
		if (anchorX == 1) return Math.max(x0 / 2 - 4, g.guiWidth() - x0 / 2 - 4);
		if (anchorX == 2) return x0 - 8;
		return g.guiWidth();
	}

}

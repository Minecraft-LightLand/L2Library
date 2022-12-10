package dev.xkmc.l2library.base.overlay;

public class TextBox extends OverlayUtils {

	private final int anchorX, anchorY;
	private final int x, y, width;

	public TextBox(int screenWidth, int screenHeight, int anchorX, int anchorY, int x, int y, int width) {
		super(screenWidth, screenHeight);
		this.anchorX = anchorX;
		this.anchorY = anchorY;
		this.x = x;
		this.y = y;
		this.width = width;
	}

	@Override
	public int getX(int w) {
		return x - w * anchorX / 2;
	}

	@Override
	public int getY(int h) {
		return y - h * anchorY / 2;
	}

	@Override
	public int getMaxWidth() {
		if (width > 0) return width;
		if (anchorX == 0) return screenWidth - x - 8;
		if (anchorX == 1) return Math.max(x / 2 - 4, screenWidth - x / 2 - 4);
		if (anchorX == 2) return x - 8;
		return screenWidth;
	}

}

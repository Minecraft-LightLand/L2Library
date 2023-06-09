package dev.xkmc.l2library.base.effects.api;

public record IconRenderRegion(float x, float y, float scale) {

	public static IconRenderRegion identity() {
		return new IconRenderRegion(0, 0, 1);
	}

	public static IconRenderRegion of(int r, int ix, int iy, int w, int h) {
		float y = ((r - h) / 2f + iy) / r;
		float x = ((r - w) / 2f + ix) / r;
		return new IconRenderRegion(x, y, 1f / r);
	}

	public IconRenderRegion resize(IconRenderRegion inner) {
		return new IconRenderRegion(x + inner.x() * scale, y + inner.y() * scale, scale * inner.scale);
	}

}

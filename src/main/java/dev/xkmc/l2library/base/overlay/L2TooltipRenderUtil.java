package dev.xkmc.l2library.base.overlay;

import com.mojang.blaze3d.vertex.BufferBuilder;
import org.joml.Matrix4f;

public record L2TooltipRenderUtil(L2TooltipRenderUtil.BlitPainter fill, Matrix4f mat, BufferBuilder buffer,
								  int bg, int bs, int be) {

	public void renderTooltipBackground(int x, int y, int w, int h, int z) {
		int x1 = x - 3;
		int y1 = y - 3;
		int w1 = w + 3 + 3;
		int h1 = h + 3 + 3;
		renderHorizontalLine(x1, y1 - 1, w1, z, bg);
		renderHorizontalLine(x1, y1 + h1, w1, z, bg);
		renderRectangle(x1, y1, w1, h1, z, bg);
		renderVerticalLine(x1 - 1, y1, h1, z, bg);
		renderVerticalLine(x1 + w1, y1, h1, z, bg);
		renderFrameGradient(x1, y1 + 1, w1, h1, z, bs, be);
	}

	private void renderFrameGradient(int x, int y, int w, int h, int z, int c0, int c1) {
		renderVerticalLineGradient(x, y, h - 2, z, c0, c1);
		renderVerticalLineGradient(x + w - 1, y, h - 2, z, c0, c1);
		renderHorizontalLine(x, y - 1, w, z, c0);
		renderHorizontalLine(x, y - 1 + h - 1, w, z, c1);
	}

	private void renderVerticalLine(int x, int y, int h, int z, int c) {
		fill.blit(mat, buffer, x, y, x + 1, y + h, z, c, c);
	}

	private void renderVerticalLineGradient(int x, int y, int h, int z, int c0, int c1) {
		fill.blit(mat, buffer, x, y, x + 1, y + h, z, c0, c1);
	}

	private void renderHorizontalLine(int x, int y, int w, int z, int c) {
		fill.blit(mat, buffer, x, y, x + w, y + 1, z, c, c);
	}

	private void renderRectangle(int x, int y, int w, int h, int z, int c) {
		fill.blit(mat, buffer, x, y, x + w, y + h, z, c, c);
	}


	@FunctionalInterface
	public interface BlitPainter {
		void blit(Matrix4f matrix4f, BufferBuilder builder, int x0, int y0, int x1, int y1, int z, int c0, int c1);
	}

}
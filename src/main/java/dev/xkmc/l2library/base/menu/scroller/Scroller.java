package dev.xkmc.l2library.base.menu.scroller;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2library.base.menu.base.MenuLayoutConfig;
import net.minecraft.util.Mth;

public class Scroller {

	private final ScrollerScreen screen;
	private final MenuLayoutConfig sprite;
	private final String box, light, dark;
	private final int bx, by, bw, bh, sh;

	private boolean scrolling;
	private double percentage;

	public Scroller(ScrollerScreen screen, MenuLayoutConfig sprite, String slider_middle, String slider_light, String slider_dark) {
		this.screen = screen;
		this.sprite = sprite;
		this.box = slider_middle;
		this.light = slider_light;
		this.dark = slider_dark;
		var scroller = sprite.getComp(box);
		bx = scroller.x;
		by = scroller.y;
		bw = scroller.w;
		bh = scroller.ry;
		var slider = sprite.getSide(light);
		sh = slider.h;
	}

	public boolean mouseClicked(double mx, double my, int btn) {
		this.scrolling = false;
		int cx = screen.getGuiLeft() + bx;
		int cy = screen.getGuiTop() + by;
		if (mx >= cx && mx < cx + bw && my >= cy && my < cy + bh) {
			this.scrolling = true;
			return true;
		}
		return false;
	}

	public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
		if (this.scrolling && screen.getMenu().getMaxScroll() > 0) {
			int y0 = screen.getGuiTop() + by;
			int y1 = y0 + bh;
			percentage = (my - y0 - sh * 0.5) / ((y1 - y0) - 15.0F);
			percentage = Mth.clamp(percentage, 0.0F, 1.0F);
			updateIndex();
			return true;
		}
		return false;
	}

	public boolean mouseScrolled(double mx, double my, double d) {
		if (screen.getMenu().getMaxScroll() > 0) {
			int i = screen.getMenu().getMaxScroll();
			double f = d / i;
			percentage = Mth.clamp(percentage - f, 0, 1);
			updateIndex();
		}
		return true;
	}

	private void updateIndex() {
		screen.scrollTo((int) ((percentage * screen.getMenu().getMaxScroll()) + 0.5D));
	}

	public void render(PoseStack stack, MenuLayoutConfig.ScreenRenderer sr) {
		if (screen.getMenu().getMaxScroll() == 0) {
			sr.draw(stack, box, dark);
		} else {
			int off = (int) Math.round((bh - sh) * percentage);
			sr.draw(stack, box, light, 0, off);
		}
	}
}

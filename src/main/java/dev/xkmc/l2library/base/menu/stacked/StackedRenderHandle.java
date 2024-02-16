package dev.xkmc.l2library.base.menu.stacked;

import dev.xkmc.l2library.base.menu.base.MenuLayoutConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class StackedRenderHandle {

	static final int BTN_X_OFFSET = 3;
	static final int TEXT_BASE_HEIGHT = 8;

	private static final int SLOT_X_OFFSET = 7, SLOT_SIZE = 18, SPRITE_OFFSET = 176;

	final Screen scr;
	final GuiGraphics g;
	final MenuLayoutConfig.ScreenRenderer sm;
	final Font font;
	final int text_color;
	private final int TEXT_Y_OFFSET;
	private final int TEXT_HEIGHT;
	private final int text_x_offset;

	private int current_y = 3;
	private int current_x = 0;

	final List<TextEntry> textList = new ArrayList<>();

	public StackedRenderHandle(Screen scr, GuiGraphics g, MenuLayoutConfig.ScreenRenderer sm) {
		this(scr, g, sm, 3);
	}

	public StackedRenderHandle(Screen scr, GuiGraphics g, MenuLayoutConfig.ScreenRenderer sm, int ty) {
		this(scr, g, 8, 4210752, sm, ty);
	}

	public StackedRenderHandle(Screen scr, GuiGraphics g, int x_offset, int color, MenuLayoutConfig.ScreenRenderer sm) {
		this(scr, g, x_offset, color, sm, 3);
	}

	public StackedRenderHandle(Screen scr, GuiGraphics g, int x_offset, int color, MenuLayoutConfig.ScreenRenderer sm, int ty) {
		this.font = Minecraft.getInstance().font;
		this.g = g;
		this.scr = scr;
		this.sm = sm;
		this.text_color = color;
		this.text_x_offset = x_offset;
		this.TEXT_Y_OFFSET = ty;
		this.TEXT_HEIGHT = font.lineHeight + ty + 1;
	}

	public void drawText(Component text, boolean shadow) {
		endCell();
		int y = current_y + TEXT_Y_OFFSET;
		textList.add(new TextEntry(text, text_x_offset, y, text_color, shadow));
		current_y += TEXT_HEIGHT;
	}

	public void drawTable(Component[][] table, int x_max, boolean shadow) {
		endCell();
		int w = table[0].length;
		int w1 = 0;
		int ws = 0;
		for (Component[] c : table) {
			w1 = Math.max(w1, font.width(c[0]));
			for (int i = 1; i < w; i++) {
				ws = Math.max(ws, font.width(c[i]));
			}
		}
		int sumw = w1 + ws * (w - 1);
		int x0 = text_x_offset;
		int x1 = x_max - text_x_offset;
		float space = (x1 - x0 - sumw) * 1.0f / (w - 1);
		for (Component[] c : table) {
			int y = current_y + TEXT_Y_OFFSET;
			float x_start = x0;
			for (int i = 0; i < w; i++) {
				float wi = i == 0 ? w1 : ws;
				int x = Math.round(x_start);
				textList.add(new TextEntry(c[i], x, y, text_color, shadow));
				x_start += wi + space;
			}
			current_y += TEXT_HEIGHT;
		}
	}

	public TextButtonHandle drawTextWithButtons(Component text, boolean shadow) {
		endCell();
		int y = current_y + TEXT_Y_OFFSET;
		textList.add(new TextEntry(text, text_x_offset, y, text_color, shadow));
		int x_off = text_x_offset + font.width(text) + BTN_X_OFFSET;
		TextButtonHandle ans = new TextButtonHandle(this, x_off, y + font.lineHeight / 2);
		current_y += TEXT_HEIGHT;
		return ans;
	}

	public CellEntry addCell(boolean toggled, boolean disabled) {
		startCell();
		int index = toggled ? 1 : disabled ? 2 : 0;
		int x = SLOT_X_OFFSET + current_x * SLOT_SIZE;
		int u = SPRITE_OFFSET + index * SLOT_SIZE;
		g.blit(MenuLayoutConfig.getTexture(sm.id), x, current_y, u, 0, SLOT_SIZE, SLOT_SIZE);
		var ans = new CellEntry(x + 1, current_y + 1, 16, 16);
		current_x++;
		if (current_x == 9) {
			endCell();
		}
		return ans;
	}

	private void startCell() {
		if (current_x < 0) {
			current_x = 0;
		}
	}

	private void endCell() {
		if (current_x > 0) {
			current_x = -1;
			current_y += SLOT_SIZE;
		}
	}

	public void flushText() {
		textList.forEach(e -> g.drawString(font, e.text(), e.x(), e.y(), e.color(), e.shadow()));
	}

}

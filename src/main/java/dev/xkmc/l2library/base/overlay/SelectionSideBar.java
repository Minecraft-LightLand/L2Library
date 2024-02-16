package dev.xkmc.l2library.base.overlay;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

import java.util.List;

public abstract class SelectionSideBar<T, S extends SideBar.Signature<S>> extends SideBar<S> implements IGuiOverlay {

	public SelectionSideBar(float duration, float ease) {
		super(duration, ease);
	}

	public abstract Pair<List<T>, Integer> getItems();

	public abstract boolean isAvailable(T t);

	public abstract boolean onCenter();

	public void initRender() {

	}

	@Override
	public void render(ExtendedGui gui, GuiGraphics g, float partialTick, int width, int height) {
		if (!ease(gui.getGuiTicks() + partialTick))
			return;
		initRender();
		gui.setupOverlayRenderState(true, false);
		int x0 = this.getXOffset(width);
		int y0 = this.getYOffset(height);
		Context ctx = new Context(gui, g, partialTick, Minecraft.getInstance().font, x0, y0);
		renderContent(ctx);
	}

	public void renderContent(Context ctx) {
		Pair<List<T>, Integer> content = getItems();
		var list = content.getFirst();
		for (int i = 0; i < list.size(); i++) {
			renderEntry(ctx, list.get(i), i, content.getSecond());
		}
	}

	protected abstract void renderEntry(Context ctx, T t, int index, int select);

	public record Context(ExtendedGui gui, GuiGraphics g, float pTick, Font font, int x0, int y0) {

		public void renderItem(ItemStack stack, int x, int y) {
			if (!stack.isEmpty()) {
				g.renderItem(stack, x, y);
				g.renderItemDecorations(font, stack, x, y);
			}
		}
	}

}

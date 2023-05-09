package dev.xkmc.l2library.base.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

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
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
		if (!ease(gui.getGuiTicks() + partialTick))
			return;
		initRender();
		gui.setupOverlayRenderState(true, false);
		float x0 = this.getXOffset(width);
		float y0 = this.getYOffset(height);
		Context ctx = new Context(gui, poseStack, partialTick, width, height, Minecraft.getInstance().font,
				Minecraft.getInstance().getItemRenderer(), x0, y0);
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

	public record Context(ForgeGui gui, PoseStack pose, float pTick, int width, int height, Font font,
						  ItemRenderer renderer, float x0, float y0) {

		public void renderItem(ItemStack stack, int x, int y) {
			if (!stack.isEmpty()) {
				renderer().renderAndDecorateItem(pose, stack, x, y);
				renderer().renderGuiItemDecorations(pose, font, stack, x, y);
			}
		}
	}

}

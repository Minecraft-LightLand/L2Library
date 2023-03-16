package dev.xkmc.l2library.base.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.List;

public abstract class SelectionSideBar extends SideBar implements IGuiOverlay {

	public SelectionSideBar(float duration, float ease) {
		super(duration, ease);
	}

	public abstract Pair<List<ItemStack>, Integer> getItems();

	public abstract boolean isAvailable(ItemStack stack);

	public abstract boolean onCenter();

	public void initRender() {

	}

	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
		if (!ease(gui.getGuiTicks() + partialTick))
			return;
		initRender();
		gui.setupOverlayRenderState(true, false);
		Pair<List<ItemStack>, Integer> content = getItems();
		var list = content.getFirst();
		int selected = content.getSecond();
		ItemRenderer renderer = gui.getMinecraft().getItemRenderer();
		Font font = gui.getMinecraft().font;
		int dx = getXOffset(width);
		int dy = getYOffset(height);
		boolean shift = Minecraft.getInstance().options.keyShift.isDown();
		for (int i = 0; i < list.size(); i++) {
			ItemStack stack = list.get(i);
			int y = 18 * i + dy;
			renderSelection(dx, y, shift ? 127 : 64, isAvailable(stack), selected == i);
			if (selected == i) {
				if (!stack.isEmpty() && ease_time == max_ease) {
					boolean onCenter = onCenter();
					TextBox box = new TextBox(width, height, onCenter ? 0 : 2, 1, onCenter ? dx + 22 : dx - 6, y + 8, -1);
					box.renderLongText(gui, poseStack, List.of(stack.getHoverName()));
				}
			}
			if (!stack.isEmpty()) {
				renderer.renderAndDecorateItem(poseStack, stack, dx, y);
				renderer.renderGuiItemDecorations(poseStack, font, stack, dx, y);
			}
		}
	}

	public void renderSelection(int x, int y, int a, boolean available, boolean selected) {
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		Tesselator tex = Tesselator.getInstance();
		BufferBuilder builder = tex.getBuilder();
		if (available) {
			OverlayUtils.fillRect(builder, x, y, 16, 16, 255, 255, 255, a);
		} else {
			OverlayUtils.fillRect(builder, x, y, 16, 16, 255, 0, 0, a);
		}
		if (selected) {
			OverlayUtils.drawRect(builder, x, y, 16, 16, 255, 170, 0, 255);
		}
		RenderSystem.enableDepthTest();
	}

}

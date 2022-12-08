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

	public SelectionSideBar(int duration, int ease) {
		super(duration, ease);
	}

	public abstract Pair<List<ItemStack>, Integer> getItems();

	public abstract boolean isAvailable(ItemStack stack);

	public void initRender() {

	}

	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
		if (!ease(partialTick))
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
		for (int i = 0; i < list.size(); i++) {
			ItemStack stack = list.get(i);
			int x = width / 2 + 18 * 3 + 1 + dx;
			int y = height / 2 - 81 + 18 * i + 1 + dy;
			if (selected == i) {
				boolean shift = Minecraft.getInstance().options.keyShift.isDown();
				renderSelection(x, y, shift ? 127 : 64, isAvailable(stack));
				if (!stack.isEmpty()) {
					new OverlayUtils(width, height).renderLongText(gui, poseStack, x + 22, y + 3, width,
							List.of(stack.getHoverName()));
				}
			}
			if (!stack.isEmpty()) {
				renderer.renderAndDecorateItem(stack, x, y);
				renderer.renderGuiItemDecorations(font, stack, x, y);
			}
		}
	}

	public void renderSelection(int x, int y, int a, boolean available) {
		RenderSystem.disableDepthTest();
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		Tesselator tex = Tesselator.getInstance();
		BufferBuilder builder = tex.getBuilder();
		if (available) {
			OverlayUtils.fillRect(builder, x, y, 16, 16, 255, 255, 255, a);
			OverlayUtils.drawRect(builder, x, y, 16, 16, 0xff, 0xaa, 0, 255);
		} else {
			OverlayUtils.fillRect(builder, x, y, 16, 16, 255, 0, 0, a);
		}
		RenderSystem.enableTexture();
		RenderSystem.enableDepthTest();
	}

}

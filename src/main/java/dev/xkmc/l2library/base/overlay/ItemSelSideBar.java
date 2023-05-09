package dev.xkmc.l2library.base.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class ItemSelSideBar<S extends SideBar.Signature<S>> extends SelectionSideBar<ItemStack, S> {

	public ItemSelSideBar(float duration, float ease) {
		super(duration, ease);
	}

	@Override
	protected void renderEntry(Context ctx, ItemStack stack, int i, int selected) {
		boolean shift = Minecraft.getInstance().options.keyShift.isDown();
		float y = 18 * i + ctx.y0();
		renderSelection(ctx.x0(), y, shift ? 127 : 64, isAvailable(stack), selected == i);
		if (selected == i) {
			if (!stack.isEmpty() && ease_time == max_ease) {
				boolean onCenter = onCenter();
				TextBox box = new TextBox(ctx.width(), ctx.height(), onCenter ? 0 : 2, 1, (int) (ctx.x0() + (onCenter ? 22 : -6)), (int) (y + 8), -1);
				box.renderLongText(ctx.gui(), ctx.pose(), List.of(stack.getHoverName()));
			}
		}
		ctx.renderItem(stack, (int) ctx.x0(), (int) y);
	}

	public void renderSelection(float x, float y, int a, boolean available, boolean selected) {
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

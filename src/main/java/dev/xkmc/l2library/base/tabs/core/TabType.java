package dev.xkmc.l2library.base.tabs.core;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
enum TabType {
	ABOVE(0, 0, 28, 32);

	public static final int MAX_TABS = 8;
	private final int textureX;
	private final int textureY;
	private final int width;
	private final int height;

	TabType(int tx, int ty, int w, int h) {
		this.textureX = tx;
		this.textureY = ty;
		this.width = w;
		this.height = h;
	}

	public void draw(PoseStack stack, GuiComponent screen, int x, int y, boolean selected, int index) {
		index = index % MAX_TABS;
		int tx = this.textureX;
		if (index > 0) {
			tx += this.width;
		}

		if (index == MAX_TABS - 1) {
			tx += this.width;
		}

		int ty = selected ? this.textureY + this.height : this.textureY;
		int h = selected ? this.height : this.height - 4;
		GuiComponent.blit(stack, x, y, tx, ty, this.width, h);
	}

	public void drawIcon(PoseStack poseStack, int x, int y, int index, ItemRenderer renderer, ItemStack stack) {
		renderer.renderAndDecorateFakeItem(poseStack, stack, x + 6, y + 9);
	}

	public int getX(int index) {
		return (this.width + 4) * index;
	}

	public int getY(int index) {
		return -this.height + 4;
	}

	public boolean isMouseOver(int p_97214_, int p_97215_, int p_97216_, double p_97217_, double p_97218_) {
		int i = p_97214_ + this.getX(p_97216_);
		int j = p_97215_ + this.getY(p_97216_);
		return p_97217_ > (double) i && p_97217_ < (double) (i + this.width) && p_97218_ > (double) j && p_97218_ < (double) (j + this.height);
	}
}

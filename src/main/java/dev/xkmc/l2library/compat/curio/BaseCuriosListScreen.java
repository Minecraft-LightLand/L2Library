package dev.xkmc.l2library.compat.curio;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2library.base.menu.BaseContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import top.theillusivec4.curios.common.inventory.CurioSlot;

public class BaseCuriosListScreen<T extends BaseCuriosListMenu<T>> extends BaseContainerScreen<T> {

	public BaseCuriosListScreen(T cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	protected void init() {
		super.init();
		if (topPos < 28) topPos = 28;
		int w = 10;
		int h = 11;
		int x = getGuiLeft() + titleLabelX + font.width(getTitle()) + 14,
				y = getGuiTop() + 4;
		if (menu.curios.page > 0) {
			addRenderableWidget(new Button(x - w - 1, y, w, h, Component.literal("<"), e -> click(1)));
		}
		if (menu.curios.page < menu.curios.total - 1) {
			addRenderableWidget(new Button(x, y, w, h, Component.literal(">"), e -> click(2)));
		}
	}

	@Override
	protected void renderBg(PoseStack pose, float pTick, int mx, int my) {
		var sr = menu.sprite.getRenderer(this);
		sr.start(pose);
		for (int i = 0; i < menu.curios.getRows() * 9; i++) {
			if (menu.curios.getSlotAtPosition(i) != null)
				sr.draw(pose, "grid", "slot", i % 9 * 18 - 1, i / 9 * 18 - 1);
		}
	}

	@Override
	protected void renderLabels(PoseStack p_281635_, int p_282681_, int p_283686_) {
		super.renderLabels(p_281635_, p_282681_, p_283686_);
	}

	@Override
	protected void renderTooltip(PoseStack pose, int mx, int my) {
		LocalPlayer clientPlayer = Minecraft.getInstance().player;
		if (clientPlayer != null && clientPlayer.inventoryMenu
				.getCarried().isEmpty() && this.getSlotUnderMouse() != null) {
			Slot slot = this.getSlotUnderMouse();

			if (slot instanceof CurioSlot slotCurio && !slot.hasItem()) {
				renderTooltip(pose, Component.translatable(slotCurio.getSlotName()), mx, my);
			}
		}
		super.renderTooltip(pose, mx, my);
	}

}

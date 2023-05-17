package dev.xkmc.l2library.init.compat.tab;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2library.base.menu.BaseContainerScreen;
import dev.xkmc.l2library.base.tabs.core.TabManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import top.theillusivec4.curios.common.inventory.CurioSlot;

public class CuriosListScreen extends BaseContainerScreen<CuriosListMenu> {

	public CuriosListScreen(CuriosListMenu cont, Inventory plInv, Component title) {
		super(cont, plInv, title);
	}

	@Override
	public void init() {
		super.init();
		new TabManager(this).init(this::addRenderableWidget, TabCurios.tab);
	}

	@Override
	protected void renderBg(PoseStack pose, float pTick, int mx, int my) {
		var sr = menu.sprite.getRenderer(this);
		sr.start(pose);
		for (int i = 0; i < menu.curios.getSize(); i++) {
			sr.draw(pose, "grid", "slot", i % 9 * 18 - 1, i / 9 * 18 - 1);
		}

	}

	@Override
	protected void renderTooltip(PoseStack poseStack, int mx, int my) {
		LocalPlayer clientPlayer = Minecraft.getInstance().player;
		if (clientPlayer != null && clientPlayer.inventoryMenu
				.getCarried().isEmpty() && this.getSlotUnderMouse() != null) {
			Slot slot = this.getSlotUnderMouse();

			if (slot instanceof CurioSlot slotCurio && !slot.hasItem()) {
				this.renderTooltip(poseStack, Component.translatable(slotCurio.getSlotName()), mx, my);
			}
		}
		super.renderTooltip(poseStack, mx, my);
	}
}

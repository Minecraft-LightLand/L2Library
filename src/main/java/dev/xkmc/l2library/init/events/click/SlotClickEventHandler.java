package dev.xkmc.l2library.init.events.click;

import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;

public class SlotClickEventHandler {

	@OnlyIn(Dist.CLIENT)
	public static void onScreenRightClick(ScreenEvent.MouseButtonPressed.Pre event) {
		Screen screen = event.getScreen();
		if (screen instanceof AbstractContainerScreen cont) {
			Slot slot = cont.getSlotUnderMouse();
			if (slot == null || slot.getItem().isStackable()) return;
			if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				boolean b1 = slot.container == Proxy.getClientPlayer().getInventory();
				boolean b2 = cont.getMenu().containerId > 0;
				if (b1 || b2) {
					int inv = b1 ? slot.getSlotIndex() : -1;
					int ind = inv == -1 ? slot.index : -1;
					int wid = cont.getMenu().containerId;
					if ((inv >= 0 || ind >= 0)) {
						for (var handler : SlotClickHandler.MAP.values()) {
							if (handler.isAllowed(slot.getItem())) {
								handler.slotClickToServer(ind, inv, wid);
								event.setCanceled(true);
								return;
							}
						}
					}
				}
			}
		}
	}

}

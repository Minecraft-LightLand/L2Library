package dev.xkmc.l2library.init.events.screen.packets;

import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public enum ScreenType {
	NONE(() -> Minecraft.getInstance().setScreen(null)),
	PLAYER(() -> Minecraft.getInstance().setScreen(new InventoryScreen(Proxy.getClientPlayer())));

	private final Runnable callback;

	ScreenType(Runnable callback) {
		this.callback = callback;
	}

	public void perform() {
		callback.run();
	}

}

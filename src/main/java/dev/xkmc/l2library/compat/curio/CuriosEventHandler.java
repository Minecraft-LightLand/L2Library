package dev.xkmc.l2library.compat.curio;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CuriosEventHandler {

	public static void openMenuWrapped(ServerPlayer player, Runnable run) {
		var menu = player.containerMenu;
		ItemStack stack = menu.getCarried();
		menu.setCarried(ItemStack.EMPTY);
		run.run();
		menu = player.containerMenu;
		menu.setCarried(stack);
	}

}
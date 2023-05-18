package dev.xkmc.l2library.init.events.screen.base;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public enum MenuTriggerType {
	OPEN_MENU, NETWORK_HOOK_SIMPLE, NETWORK_HOOK_OTHER, DISABLED;

	public boolean restore(ServerPlayer player, MenuProvider pvd, @Nullable FriendlyByteBuf buf) {
		switch (this) {
			case OPEN_MENU -> {
				player.openMenu(pvd);
				return true;
			}
			case NETWORK_HOOK_SIMPLE -> {
				NetworkHooks.openScreen(player, pvd);
				return true;
			}
			case NETWORK_HOOK_OTHER -> {
				if (buf == null) return false;
				NetworkHooks.openScreen(player, pvd, e -> e.writeBytes(buf));
				return true;
			}
			default -> {
				return false;
			}
		}
	}
}

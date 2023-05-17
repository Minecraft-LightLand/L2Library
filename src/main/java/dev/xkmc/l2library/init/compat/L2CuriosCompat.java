package dev.xkmc.l2library.init.compat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class L2CuriosCompat {

	public static void onStartup() {
		if (ModList.get().isLoaded("curios")) {
			CuriosScreenCompatImpl.get().onStartUp();
			CuriosTrackCompatImpl.get().onStartUp();
		}
	}

	public static void onClientInit() {
		if (ModList.get().isLoaded("curios")) {
			CuriosScreenCompatImpl.get().onClientInit();
		}
	}

	public static void openCuriosTab(ServerPlayer player) {
		if (ModList.get().isLoaded("curios")) {
			CuriosScreenCompatImpl.get().openScreen(player);
		}
	}

	public static void openCuriosInv(ServerPlayer player) {
		if (ModList.get().isLoaded("curios")) {
			CuriosScreenCompatImpl.get().openCurioImpl(player);
		}
	}

	public static ItemStack getItemFromSlot(Player player, int slot) {
		if (ModList.get().isLoaded("curios")) {
			return CuriosTrackCompatImpl.get().getItemFromSlotImpl(player, slot);
		}
		return ItemStack.EMPTY;
	}

	public static void commonSetup() {
		if (ModList.get().isLoaded("curios")) {
			CuriosTrackCompatImpl.get().onCommonSetup();
		}
	}
}

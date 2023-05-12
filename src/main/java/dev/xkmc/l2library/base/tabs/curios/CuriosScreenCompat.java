package dev.xkmc.l2library.base.tabs.curios;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;

public class CuriosScreenCompat {

	public static void onStartup(){
		if (ModList.get().isLoaded("curios")) {
			CuriosScreenCompatImpl.get().onStartUp();
		}
	}

	public static void onClientInit() {
		if (ModList.get().isLoaded("curios")) {
			CuriosScreenCompatImpl.get().onClientInit();
		}
	}

	public static void openScreen(ServerPlayer player) {
		if (ModList.get().isLoaded("curios")) {
			CuriosScreenCompatImpl.get().openScreen(player);
		}
	}
}

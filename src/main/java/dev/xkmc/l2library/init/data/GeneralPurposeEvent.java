package dev.xkmc.l2library.init.data;

import dev.xkmc.l2library.base.tabs.curios.CuriosScreenCompat;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public enum GeneralPurposeEvent {
	CURIO_OPEN(CuriosScreenCompat::openScreen);

	private final Consumer<ServerPlayer> task;

	GeneralPurposeEvent(Consumer<ServerPlayer> run) {
		this.task = run;
	}

	public void invoke(ServerPlayer player) {
		task.accept(player);
	}

}

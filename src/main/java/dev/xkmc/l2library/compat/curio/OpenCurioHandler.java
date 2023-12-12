package dev.xkmc.l2library.compat.curio;

import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public enum OpenCurioHandler {
	CURIO_OPEN(TabCuriosCompat::openCuriosTab);

	private final Consumer<ServerPlayer> task;

	OpenCurioHandler(Consumer<ServerPlayer> run) {
		this.task = run;
	}

	public void invoke(ServerPlayer player) {
		task.accept(player);
	}

}

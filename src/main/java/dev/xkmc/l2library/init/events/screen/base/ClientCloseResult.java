package dev.xkmc.l2library.init.events.screen.base;

public enum ClientCloseResult {
	FAIL(-2), POP_ALL(-1), REMAIN(0);

	public final int id;

	ClientCloseResult(int id) {
		this.id = id;
	}
}

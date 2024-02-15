package dev.xkmc.l2library.capability.player;

import dev.xkmc.l2library.init.L2Library;
import net.minecraft.server.level.ServerPlayer;

public class PlayerCapabilityNetworkHandler<T extends PlayerCapabilityTemplate<T>> {

	public final PlayerCapabilityHolder<T> holder;

	public PlayerCapabilityNetworkHandler(PlayerCapabilityHolder<T> holder) {
		this.holder = holder;
	}

	public void toClient(ServerPlayer e) {
		L2Library.PACKET_HANDLER.toClientPlayer(PlayerCapToClient.of(PlayerCapToClient.Action.CLIENT, holder, holder.get(e)), e);
	}

	public void toTracking(ServerPlayer e) {
		L2Library.PACKET_HANDLER.toTrackingOnly(PlayerCapToClient.of(PlayerCapToClient.Action.TRACK, holder, holder.get(e)), e);
	}

	public void startTracking(ServerPlayer tracker, ServerPlayer target) {
		L2Library.PACKET_HANDLER.toClientPlayer(PlayerCapToClient.of(PlayerCapToClient.Action.TRACK, holder, holder.get(target)), tracker);
	}

}

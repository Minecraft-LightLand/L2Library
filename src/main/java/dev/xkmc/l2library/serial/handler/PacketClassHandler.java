package dev.xkmc.l2library.serial.handler;

import net.minecraft.network.FriendlyByteBuf;

public interface PacketClassHandler<T> {

	void toPacket(FriendlyByteBuf buf, Object obj);

	T fromPacket(FriendlyByteBuf buf);
}

package dev.xkmc.l2library.capability.player;

import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;

import java.util.function.Predicate;

public class ClientSyncHandler {

	public static <T extends PlayerCapabilityTemplate<T>> void parse(byte[] tag, PlayerCapabilityHolder<T> holder, Predicate<SerialClass.SerialField> pred) {
		PacketCodec.fromBytes(tag, holder.cls(), Proxy.getClientPlayer().getData(holder.type()), pred);
	}

}

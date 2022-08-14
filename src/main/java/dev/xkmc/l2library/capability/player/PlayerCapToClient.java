package dev.xkmc.l2library.capability.player;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.codec.TagCodec;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SerialClass
public class PlayerCapToClient extends SerialPacketBase {

	@SerialClass.SerialField
	public Action action;

	@SerialClass.SerialField
	public ResourceLocation holderID;

	@SerialClass.SerialField
	public CompoundTag tag;

	@SerialClass.SerialField
	public UUID playerID;

	@Deprecated
	public PlayerCapToClient() {

	}

	public <T extends PlayerCapabilityTemplate<T>> PlayerCapToClient(Action action, PlayerCapabilityHolder<T> holder, T handler) {
		this.action = action;
		this.holderID = holder.id;
		this.tag = action.server.apply(handler);
		this.playerID = handler.player.getUUID();
	}

	public void handle(NetworkEvent.Context context) {
		if (action != Action.ALL && action != Action.CLONE && !Proxy.getClientPlayer().isAlive())
			return;
		PlayerCapabilityHolder<?> holder = PlayerCapabilityHolder.INTERNAL_MAP.get(holderID);
		action.client.accept(holder, this);
	}

	public enum Action {
		ALL((m) -> {
			return TagCodec.toTag(new CompoundTag(), m);
		}, (holder, packet) -> holder.cacheSet(packet.tag, false)),
		CLONE((m) -> {
			return TagCodec.toTag(new CompoundTag(), m);
		}, (holder, packet) -> holder.cacheSet(packet.tag, true)),
		TRACK((m) -> {
			return TagCodec.toTag(new CompoundTag(), m.getClass(), m, SerialClass.SerialField::toTracking);
		}, (holder, packet) -> holder.updateTracked(packet.tag, Proxy.getClientWorld().getPlayerByUUID(packet.playerID)));

		public final Function<Object, CompoundTag> server;
		public final BiConsumer<PlayerCapabilityHolder<?>, PlayerCapToClient> client;


		Action(Function<Object, CompoundTag> server, BiConsumer<PlayerCapabilityHolder<?>, PlayerCapToClient> client) {
			this.server = server;
			this.client = client;
		}
	}

}

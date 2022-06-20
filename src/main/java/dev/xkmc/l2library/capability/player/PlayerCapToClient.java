package dev.xkmc.l2library.capability.player;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.codec.TagCodec;
import dev.xkmc.l2library.serial.network.SerialPacketBase;
import dev.xkmc.l2library.util.Proxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

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

	@Deprecated
	public PlayerCapToClient() {

	}

	public <T extends PlayerCapabilityTemplate<T>> PlayerCapToClient(Action action, PlayerCapabilityHolder<T> holder, T handler) {
		this.action = action;
		this.holderID = holder.id;
		this.tag = action.server.apply(handler);
	}

	public void handle(NetworkEvent.Context context) {
		if (action != Action.ALL && action != Action.CLONE && !Proxy.getClientPlayer().isAlive())
			return;
		PlayerCapabilityHolder<?> holder = PlayerCapabilityHolder.INTERNAL_MAP.get(holderID);
		action.client.accept(holder, tag);
	}

	public enum Action {
		ALL((m) -> {
			return TagCodec.toTag(new CompoundTag(), m);
		}, (holder, tag) -> holder.cacheSet(tag, false)),
		CLONE((m) -> {
			return TagCodec.toTag(new CompoundTag(), m);
		}, (holder, tag) -> holder.cacheSet(tag, true));

		public final Function<Object, CompoundTag> server;
		public final BiConsumer<PlayerCapabilityHolder<?>, CompoundTag> client;


		Action(Function<Object, CompoundTag> server, BiConsumer<PlayerCapabilityHolder<?>, CompoundTag> client) {
			this.server = server;
			this.client = client;
		}
	}

}

package dev.xkmc.l2library.capability.player;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

@SerialClass
public record PlayerCapToClient(Action action, ResourceLocation holderID, byte[] data, UUID playerID)
		implements SerialPacketBase<PlayerCapToClient> {

	public static <T extends PlayerCapabilityTemplate<T>> PlayerCapToClient
	of(ServerPlayer player, Action action, PlayerCapabilityHolder<T> holder, T handler) {
		return new PlayerCapToClient(action, holder.id,
				PacketCodec.toBytes(handler, holder.cls(), action.pred),
				player.getUUID());
	}

	@Override
	public void handle(@Nullable Player player) {
		ClientSyncHandler.parse(data, PlayerCapabilityHolder.INTERNAL_MAP.get(holderID), action.pred);
	}

	public enum Action {
		ALL(e -> true),
		CLIENT(SerialClass.SerialField::toClient),
		TRACK(SerialClass.SerialField::toTracking),
		;
		public final Predicate<SerialClass.SerialField> pred;

		Action(Predicate<SerialClass.SerialField> pred) {
			this.pred = pred;
		}


	}

}

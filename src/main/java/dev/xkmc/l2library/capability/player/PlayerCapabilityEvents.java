package dev.xkmc.l2library.capability.player;

import dev.xkmc.l2library.serial.codec.TagCodec;
import dev.xkmc.l2library.util.code.Wrappers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerCapabilityEvents {

	@SubscribeEvent
	public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player player) {
			for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
				event.addCapability(holder.id, holder.generateSerializer(player, player.level));
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.player.isAlive() && event.phase == TickEvent.Phase.END)
			for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
				holder.get(event.player).tick();
			}
	}

	@SubscribeEvent
	public static void onServerPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		ServerPlayer e = (ServerPlayer) event.getEntity();
		if (e != null) {
			for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
				holder.network.toClientSyncAll(e);
				holder.network.toTracking(e);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			CompoundTag tag0 = TagCodec.toTag(new CompoundTag(), holder.get(event.getOriginal()));
			assert tag0 != null;
			Wrappers.run(() -> TagCodec.fromTag(tag0, holder.cls, holder.get(event.getEntity()), f -> true));
			holder.get(event.getEntity()).onClone(event.isWasDeath());
			ServerPlayer e = (ServerPlayer) event.getEntity();
			holder.network.toClientSyncClone(e);
			holder.network.toTracking(e);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onPlayerRespawn(ClientPlayerNetworkEvent.Clone event) {
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			CompoundTag tag0 = holder.getCache(event.getOldPlayer());
			Wrappers.run(() -> TagCodec.fromTag(tag0, holder.cls, holder.get(event.getNewPlayer()), f -> true));
			holder.get(event.getNewPlayer());
		}
	}

	@SubscribeEvent
	public static void onStartTracking(PlayerEvent.StartTracking event) {
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			if (!(event.getTarget() instanceof ServerPlayer e)) continue;
			holder.network.startTracking((ServerPlayer) event.getEntity(), e);
		}
	}

}

package dev.xkmc.l2library.init.events;

import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.init.L2Library;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@Mod.EventBusSubscriber(modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BaseCapabilityEvents {

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.player.isAlive() && event.phase == TickEvent.Phase.END) {
			for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
				holder.get(event.player).tick();
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onPlayerClone(PlayerEvent.Clone event) {
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			ServerPlayer e = (ServerPlayer) event.getEntity();
			holder.get(e).onClone(event.isWasDeath());
			holder.network.toClient(e);
			holder.network.toTracking(e);
		}
	}

	@SubscribeEvent
	public static void onServerPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		ServerPlayer e = (ServerPlayer) event.getEntity();
		if (e != null) {
			for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
				holder.network.toClient(e);
				holder.network.toTracking(e);
			}
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

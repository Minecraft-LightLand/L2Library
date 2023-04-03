package dev.xkmc.l2library.init.events.listeners;

import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.serial.codec.TagCodec;
import dev.xkmc.l2library.util.code.Wrappers;
import dev.xkmc.l2library.util.raytrace.EntityTarget;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientGeneralEventHandler {

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		for (EntityTarget target : EntityTarget.LIST) {
			target.tickRender();
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawn(ClientPlayerNetworkEvent.Clone event) {
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			CompoundTag tag0 = holder.getCache(event.getOldPlayer());
			Wrappers.run(() -> TagCodec.fromTag(tag0, holder.cls, holder.get(event.getNewPlayer()), f -> true));
			holder.get(event.getNewPlayer());
		}
	}

}

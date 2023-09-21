package dev.xkmc.l2library.init.events;

import dev.xkmc.l2library.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.init.L2LibraryConfig;
import dev.xkmc.l2library.util.raytrace.EntityTarget;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientGeneralEventHandler {

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		for (EntityTarget target : EntityTarget.LIST) {
			target.tickRender();
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawn(ClientPlayerNetworkEvent.Clone event) {
		for (PlayerCapabilityHolder<?> holder : PlayerCapabilityHolder.INTERNAL_MAP.values()) {
			CompoundTag tag0 = holder.getCache(event.getOldPlayer());
			Wrappers.run(() -> TagCodec.fromTag(tag0, holder.holder_class, holder.get(event.getNewPlayer()), f -> true));
			holder.get(event.getNewPlayer());
		}
	}

	private static double scroll;

	@SubscribeEvent
	public static void scrollEvent(InputEvent.MouseScrollingEvent event) {
		double scroll_tick = L2LibraryConfig.CLIENT.scrollTick.get();
		double delta = event.getScrollDelta();
		scroll += delta;
		int diff = 0;
		if (scroll > scroll_tick) {
			diff = (int) Math.floor(scroll / scroll_tick);
			scroll -= diff * scroll_tick;
		} else if (scroll < -scroll_tick) {
			diff = -(int) Math.floor(-scroll / scroll_tick);
			scroll -= diff * scroll_tick;
		}
		if (MinecraftForge.EVENT_BUS.post(new FineScrollEvent(diff))) {
			event.setCanceled(true);
		}
	}

}

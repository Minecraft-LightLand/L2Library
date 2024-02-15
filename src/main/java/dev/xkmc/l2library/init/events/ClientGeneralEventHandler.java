package dev.xkmc.l2library.init.events;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.util.raytrace.EntityTarget;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientGeneralEventHandler {

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		for (EntityTarget target : EntityTarget.LIST) {
			target.tickRender();
		}
	}

}

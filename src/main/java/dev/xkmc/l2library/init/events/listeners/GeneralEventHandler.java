package dev.xkmc.l2library.init.events.listeners;

import dev.xkmc.l2library.base.menu.SpriteManager;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.serial.network.PacketHandlerWithConfig;
import dev.xkmc.l2library.util.raytrace.RayTraceUtil;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GeneralEventHandler {

	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new BaseJsonReloadListener("gui/coords", map -> {
			SpriteManager.CACHE.clear();
			SpriteManager.CACHE.putAll(map);
		}));
		PacketHandlerWithConfig.addReloadListeners(event);
	}

	@SubscribeEvent
	public static void onDatapackSync(OnDatapackSyncEvent event) {
		PacketHandlerWithConfig.onDatapackSync(event);
	}

	@SubscribeEvent
	public static void serverTick(TickEvent.ServerTickEvent event) {
		RayTraceUtil.serverTick();
	}

}

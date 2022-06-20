package dev.xkmc.l2library.init.events;

import dev.xkmc.l2library.menu.OverlayManager;
import dev.xkmc.l2library.menu.SpriteManager;
import dev.xkmc.l2library.network.PacketHandlerWithConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("unused")
public class GenericEventHandler {

	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new BaseJsonReloadListener("gui/coords", map -> {
			SpriteManager.CACHE.clear();
			SpriteManager.CACHE.putAll(map);
		}));
		PacketHandlerWithConfig.addReloadListeners(event);
	}

	@OnlyIn(Dist.CLIENT)
	public static void clientReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(new BaseJsonReloadListener("textures/gui/overlays", map -> {
			map.forEach((k, v) -> {
				String modid = k.getNamespace();
				String[] paths = k.getPath().split("/");
				String path = paths[paths.length - 1];
				OverlayManager.get(modid, path).reset(v);
			});
		}));
	}

	@SubscribeEvent
	public static void onDatapackSync(OnDatapackSyncEvent event) {
		PacketHandlerWithConfig.onDatapackSync(event);
	}

}

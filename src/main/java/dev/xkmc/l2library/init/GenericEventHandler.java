package dev.xkmc.l2library.init;

import dev.xkmc.l2library.menu.OverlayManager;
import dev.xkmc.l2library.menu.SpriteManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("unused")
public class GenericEventHandler {

	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new BaseJsonReloadListener("gui/coords", map -> {
			SpriteManager.CACHE.clear();
			SpriteManager.CACHE.putAll(map);
		}));
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

}

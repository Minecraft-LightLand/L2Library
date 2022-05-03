package dev.xkmc.l2library.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class L2Client {

	public static void onCtorClient(IEventBus bus, IEventBus eventBus) {
		bus.addListener(GenericEventHandler::clientReloadListeners);
	}

}

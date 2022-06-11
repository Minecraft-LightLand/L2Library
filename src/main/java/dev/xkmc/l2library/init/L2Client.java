package dev.xkmc.l2library.init;

import dev.xkmc.l2library.init.events.GenericEventHandler;
import net.minecraftforge.eventbus.api.IEventBus;

public class L2Client {

	public static void onCtorClient(IEventBus bus, IEventBus eventBus) {
		bus.addListener(GenericEventHandler::clientReloadListeners);
	}

}

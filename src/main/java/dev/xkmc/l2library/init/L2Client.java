package dev.xkmc.l2library.init;

import dev.xkmc.l2library.base.tabs.contents.CuriosScreenCompat;
import dev.xkmc.l2library.base.tabs.contents.TabAttributes;
import dev.xkmc.l2library.base.tabs.contents.TabInventory;
import dev.xkmc.l2library.base.tabs.core.TabRegistry;
import dev.xkmc.l2library.base.tabs.core.TabToken;
import dev.xkmc.l2library.init.events.GenericEventHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class L2Client {

	public static TabToken<TabInventory> TAB_INVENTORY;
	public static TabToken<TabAttributes> TAB_ATTRIBUTE;

	public static void onCtorClient(IEventBus bus, IEventBus eventBus) {
		bus.addListener(L2Client::client);
		bus.addListener(GenericEventHandler::clientReloadListeners);
		eventBus.addListener(TabInventory::guiPostInit);
	}

	public static void client(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			TAB_INVENTORY = TabRegistry.registerTab(TabInventory::new, () -> Items.CRAFTING_TABLE,
					Component.translatable("menu.tabs.inventory"));
			TAB_ATTRIBUTE = TabRegistry.registerTab(TabAttributes::new, () -> Items.IRON_SWORD,
					Component.translatable("menu.tabs.attribute"));
		});
		CuriosScreenCompat.onClientInit();
	}

}

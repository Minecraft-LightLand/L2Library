package dev.xkmc.l2library.init;

import dev.xkmc.l2library.init.events.GenericEventHandler;
import dev.xkmc.l2library.menu.tabs.contents.AttributeEntry;
import dev.xkmc.l2library.menu.tabs.contents.TabAttributes;
import dev.xkmc.l2library.menu.tabs.contents.TabInventory;
import dev.xkmc.l2library.menu.tabs.core.TabRegistry;
import dev.xkmc.l2library.menu.tabs.core.TabToken;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeMod;
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
					new TranslatableComponent("menu.tabs.inventory"));
			TAB_ATTRIBUTE = TabRegistry.registerTab(TabAttributes::new, () -> Items.IRON_SWORD,
					new TranslatableComponent("menu.tabs.attribute"));
		});
	}

}

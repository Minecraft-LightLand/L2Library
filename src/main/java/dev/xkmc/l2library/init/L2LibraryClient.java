package dev.xkmc.l2library.init;

import dev.xkmc.l2library.base.overlay.OverlayManager;
import dev.xkmc.l2library.base.tabs.contents.CuriosScreenCompat;
import dev.xkmc.l2library.base.tabs.contents.TabAttributes;
import dev.xkmc.l2library.base.tabs.contents.TabInventory;
import dev.xkmc.l2library.base.tabs.core.TabRegistry;
import dev.xkmc.l2library.base.tabs.core.TabToken;
import dev.xkmc.l2library.init.events.listeners.BaseJsonReloadListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class L2LibraryClient {

	public static TabToken<TabInventory> TAB_INVENTORY;
	public static TabToken<TabAttributes> TAB_ATTRIBUTE;

	@SubscribeEvent
	public static void client(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			TAB_INVENTORY = TabRegistry.registerTab(TabInventory::new, () -> Items.CRAFTING_TABLE,
					Component.translatable("menu.tabs.inventory"));
			TAB_ATTRIBUTE = TabRegistry.registerTab(TabAttributes::new, () -> Items.IRON_SWORD,
					Component.translatable("menu.tabs.attribute"));
		});
		CuriosScreenCompat.onClientInit();
	}

	@SubscribeEvent
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

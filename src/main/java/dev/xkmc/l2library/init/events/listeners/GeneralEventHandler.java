package dev.xkmc.l2library.init.events.listeners;

import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.serial.config.PacketHandlerWithConfig;
import dev.xkmc.l2library.util.raytrace.RayTraceUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static dev.xkmc.l2library.init.events.select.item.ItemConvertor.convert;

@Mod.EventBusSubscriber(modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GeneralEventHandler {

	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
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

	@SubscribeEvent
	public static void addItemToInventory(EntityItemPickupEvent event) {
		ItemStack prev = event.getItem().getItem();
		ItemStack next = convert(prev, event.getEntity());
		if (next != prev) {
			event.getItem().setItem(next);
			event.setCanceled(true);
		}
	}

}

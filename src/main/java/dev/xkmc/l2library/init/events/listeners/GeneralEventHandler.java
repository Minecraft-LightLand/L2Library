package dev.xkmc.l2library.init.events.listeners;

import dev.xkmc.l2library.base.menu.SpriteManager;
import dev.xkmc.l2library.init.L2Library;
import dev.xkmc.l2library.serial.network.PacketHandlerWithConfig;
import dev.xkmc.l2library.util.raytrace.RayTraceUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = L2Library.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GeneralEventHandler {

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof AbstractArrow arrow) {
			if (arrow.getOwner() instanceof Player player) {
				double cr = player.getAttributeValue(L2Library.CRIT_RATE.get());
				double cd = player.getAttributeValue(L2Library.CRIT_DMG.get());
				double strength = player.getAttributeValue(L2Library.BOW_STRENGTH.get());
				if (player.getRandom().nextDouble() < cr) {
					strength *= (1 + cd);
				}
				arrow.setBaseDamage((float) (arrow.getBaseDamage() * strength));
			}
		}
	}

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

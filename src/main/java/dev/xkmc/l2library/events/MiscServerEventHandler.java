package dev.xkmc.l2library.events;

import dev.xkmc.l2library.content.explosion.BaseExplosion;
import dev.xkmc.l2library.content.raytrace.RayTraceUtil;
import dev.xkmc.l2library.init.FlagMarker;
import dev.xkmc.l2library.init.L2Library;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = L2Library.MODID, bus = EventBusSubscriber.Bus.GAME)
public class MiscServerEventHandler {

	@SubscribeEvent
	public static void serverTick(ServerTickEvent.Post event) {
		RayTraceUtil.serverTick(event.getServer());
	}

	@SubscribeEvent
	public static void onDetonate(ExplosionEvent.Detonate event) {
		if (event.getExplosion() instanceof BaseExplosion exp) {
			event.getAffectedEntities().removeIf(e -> !exp.hurtEntity(e));
		}
	}

	@SubscribeEvent
	public static void onEntityStruck(EntityStruckByLightningEvent event) {
		if (event.getLightning().getTags().contains(FlagMarker.LIGHTNING)) {
			if (!(event.getEntity() instanceof LivingEntity) || event.getEntity() == event.getLightning().getCause()) {
				event.setCanceled(true);
			}
		}
	}

}

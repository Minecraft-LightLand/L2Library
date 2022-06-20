package dev.xkmc.l2library.base.effects;

import dev.xkmc.l2library.init.L2Library;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class EffectSyncEvents {

	public static final Map<UUID, Map<MobEffect, Integer>> EFFECT_MAP = new HashMap<>();
	public static final Set<MobEffect> TRACKED = new HashSet<>();

	@OnlyIn(Dist.CLIENT)
	public static void sync(EffectToClient eff) {
		Map<MobEffect, Integer> set = EFFECT_MAP.get(eff.entity);
		if (eff.exist) {
			if (set == null) {
				EFFECT_MAP.put(eff.entity, set = new HashMap<>());
			}
			set.put(eff.effect, eff.level);
		} else if (set != null) {
			set.remove(eff.effect);
		}
	}

	@SubscribeEvent
	public static void onPotionAddedEvent(PotionEvent.PotionAddedEvent event) {
		if (TRACKED.contains(event.getPotionEffect().getEffect())) {
			onEffectAppear(event.getPotionEffect().getEffect(), event.getEntityLiving(), event.getPotionEffect().getAmplifier());
		}
	}

	@SubscribeEvent
	public static void onPotionExpiryEvent(PotionEvent.PotionExpiryEvent event) {
		if (event.getPotionEffect() != null && TRACKED.contains(event.getPotionEffect().getEffect())) {
			onEffectDisappear(event.getPotionEffect().getEffect(), event.getEntityLiving());
		}
	}

	@SubscribeEvent
	public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
		if (!(event.getTarget() instanceof LivingEntity le))
			return;
		for (MobEffect eff : le.getActiveEffectsMap().keySet()) {
			if (TRACKED.contains(eff)) {
				onEffectAppear(eff, le, le.getActiveEffectsMap().get(eff).getAmplifier());
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerStopTracking(PlayerEvent.StopTracking event) {
		if (!(event.getTarget() instanceof LivingEntity le))
			return;
		for (MobEffect eff : le.getActiveEffectsMap().keySet()) {
			if (TRACKED.contains(eff)) {
				onEffectDisappear(eff, le);
			}
		}
	}

	@SubscribeEvent
	public static void onServerPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		ServerPlayer e = (ServerPlayer) event.getPlayer();
		if (e != null) {
			for (MobEffect eff : e.getActiveEffectsMap().keySet()) {
				if (TRACKED.contains(eff)) {
					onEffectAppear(eff, e, e.getActiveEffectsMap().get(eff).getAmplifier());
				}
			}
		}
	}

	@SubscribeEvent
	public static void onServerPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		ServerPlayer e = (ServerPlayer) event.getPlayer();
		if (e != null) {
			for (MobEffect eff : e.getActiveEffectsMap().keySet()) {
				if (TRACKED.contains(eff)) {
					onEffectDisappear(eff, e);
				}
			}
		}
	}

	private static void onEffectAppear(MobEffect eff, LivingEntity e, int lv) {
		L2Library.PACKET_HANDLER.toTrackingPlayers(new EffectToClient(e.getUUID(), eff, true, lv), e);
	}

	private static void onEffectDisappear(MobEffect eff, LivingEntity e) {
		L2Library.PACKET_HANDLER.toTrackingPlayers(new EffectToClient(e.getUUID(), eff, false, 0), e);
	}

}

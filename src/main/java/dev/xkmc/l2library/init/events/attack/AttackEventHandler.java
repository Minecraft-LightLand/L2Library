package dev.xkmc.l2library.init.events.attack;

import dev.xkmc.l2library.init.L2Library;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("unused")
public class AttackEventHandler {

	public static final ArrayList<AttackListener> LISTENERS = new ArrayList<>();

	private static final HashMap<UUID, AttackCache> CACHE = new HashMap<>();

	@SubscribeEvent
	public static void onPlayerAttack(AttackEntityEvent event) {
		AttackCache cache = new AttackCache();
		CACHE.put(event.getTarget().getUUID(), cache);
		cache.pushPlayer(event);
	}

	@SubscribeEvent
	public static void onCriticalHit(CriticalHitEvent event) {
		AttackCache cache = new AttackCache();
		CACHE.put(event.getTarget().getUUID(), cache);
		cache.pushCrit(event);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onAttackPre(LivingAttackEvent event) {
		if (CACHE.size() > 1000) {
			L2Library.LOGGER.error("attack cache too large: " + CACHE.size());
		}
		UUID id = event.getEntity().getUUID();
		AttackCache cache = CACHE.get(id);
		boolean replace = cache == null;
		if (!replace)
			replace = cache.getStage().ordinal() >= Stage.HURT.ordinal();
		if (!replace && cache.getPlayerAttackEntityEvent() != null && event.getSource().getEntity() != null)
			replace = event.getSource().getEntity() != cache.getPlayerAttackEntityEvent().getEntity();
		if (replace) {
			cache = new AttackCache();
			CACHE.put(id, cache);
		}
		cache.pushAttack(event);
		DamageSource source = event.getSource();
		if (source.getEntity() instanceof LivingEntity entity) { // direct damage only
			ItemStack stack = entity.getMainHandItem();
			cache.setupAttackerProfile(entity, stack);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onActuallyHurtPre(LivingHurtEvent event) {
		AttackCache cache = CACHE.get(event.getEntity().getUUID());
		if (cache != null)
			cache.pushHurt(event);
		else {
			L2Library.LOGGER.error("incorrect sequence at hurt: " + event.getEntity());
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onDamagePre(LivingDamageEvent event) {
		AttackCache cache = CACHE.get(event.getEntity().getUUID());
		if (cache != null)
			cache.pushDamage(event);
		else {
			L2Library.LOGGER.error("incorrect sequence at damage: " + event.getEntity());
		}
	}

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event) {

	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		CACHE.clear();
	}

}

package dev.xkmc.l2library.init.events;

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

	public interface AttackListener {

		default void onPlayerAttack(AttackCache cache) {

		}

		default void onCriticalHit(AttackCache cache) {

		}

		default void onAttack(AttackCache cache, ItemStack weapon) {
		}

		default void onHurt(AttackCache cache, ItemStack weapon) {
		}

		default void onDamage(AttackCache cache, ItemStack weapon) {
		}

	}

	public enum Stage {
		PREINIT, PLAYER_ATTACK, CRITICAL_HIT, HURT, ACTUALLY_HURT, DAMAGE;
	}

	public static class AttackCache {

		public Stage stage = Stage.PREINIT;
		public AttackEntityEvent player;
		public CriticalHitEvent crit;
		public LivingAttackEvent attack;
		public LivingHurtEvent hurt;
		public LivingDamageEvent damage;

		public LivingEntity target;
		public LivingEntity attacker;
		public ItemStack weapon;

		public float strength = -1;
		public float damage_3, damage_4, damage_5;

		private void pushPlayer(AttackEntityEvent event) {
			stage = Stage.PLAYER_ATTACK;
			player = event;
			strength = event.getPlayer().getAttackStrengthScale(1);
			LISTENERS.forEach(e -> e.onPlayerAttack(this));
		}

		private void pushCrit(CriticalHitEvent event) {
			stage = Stage.CRITICAL_HIT;
			crit = event;
			LISTENERS.forEach(e -> e.onCriticalHit(this));
		}

		private void pushAttack(LivingAttackEvent event) {
			stage = Stage.HURT;
			attack = event;
			target = attack.getEntityLiving();
			damage_3 = event.getAmount();
			LISTENERS.forEach(e -> e.onAttack(this, weapon));
		}

		private void pushHurt(LivingHurtEvent event) {
			stage = Stage.ACTUALLY_HURT;
			hurt = event;
			damage_4 = event.getAmount();
			LISTENERS.forEach(e -> e.onHurt(this, weapon));
		}

		private void pushDamage(LivingDamageEvent event) {
			stage = Stage.DAMAGE;
			damage = event;
			damage_5 = event.getAmount();
			LISTENERS.forEach(e -> e.onDamage(this, weapon));
		}

		private void setupAttackerProfile(LivingEntity entity, ItemStack stack) {
			attacker = entity;
			weapon = stack;
		}

	}

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
		if (CACHE.size() > 100) {
			L2Library.LOGGER.error("attack cache too large: " + CACHE.size());
		}
		UUID id = event.getEntityLiving().getUUID();
		AttackCache cache = CACHE.get(id);
		boolean replace = cache == null;
		if (!replace)
			replace = cache.stage.ordinal() >= Stage.HURT.ordinal();
		if (!replace && cache.player != null && event.getSource().getEntity() != null)
			replace = event.getSource().getEntity() != cache.player.getPlayer();
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
		AttackCache cache = CACHE.get(event.getEntityLiving().getUUID());
		if (cache != null)
			cache.pushHurt(event);
		else {
			L2Library.LOGGER.error("incorrect sequence at hurt: " + event.getEntityLiving());
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onDamagePre(LivingDamageEvent event) {
		AttackCache cache = CACHE.get(event.getEntityLiving().getUUID());
		if (cache != null)
			cache.pushDamage(event);
		else {
			L2Library.LOGGER.error("incorrect sequence at damage: " + event.getEntityLiving());
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

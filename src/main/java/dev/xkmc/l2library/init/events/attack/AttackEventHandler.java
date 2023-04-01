package dev.xkmc.l2library.init.events.attack;

import dev.xkmc.l2library.init.L2Library;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("unused")
public class AttackEventHandler {

	public static final ArrayList<AttackListener> LISTENERS = new ArrayList<>();

	private static final HashMap<UUID, PlayerAttackCache> PLAYER = new HashMap<>();
	private static final HashMap<UUID, AttackCache> CACHE = new HashMap<>();

	@SubscribeEvent
	public static void onPlayerAttack(AttackEntityEvent event) {
		if (event.getEntity().getLevel().isClientSide())
			return;
		PlayerAttackCache cache = new PlayerAttackCache();
		PLAYER.put(event.getEntity().getUUID(), cache);
		ItemStack stack = event.getEntity().getMainHandItem();
		cache.setupAttackerProfile(event.getEntity(), stack);
		cache.pushPlayer(event);
	}

	@SubscribeEvent
	public static void onCriticalHit(CriticalHitEvent event) {
		if (event.getEntity().getLevel().isClientSide())
			return;
		PlayerAttackCache cache = PLAYER.get(event.getEntity().getUUID());
		if (cache == null) cache = new PlayerAttackCache();
		PLAYER.put(event.getTarget().getUUID(), cache);
		cache.pushCrit(event);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onAttackPre(LivingAttackEvent event) {
		if (event.getEntity().getLevel().isClientSide())
			return;
		if (CACHE.size() > 1000) {
			L2Library.LOGGER.error("attack cache too large: " + CACHE.size());
			CACHE.clear();
			return;
		}
		UUID id = event.getEntity().getUUID();
		AttackCache cache = CACHE.get(id);
		if (cache != null && cache.getStage() == Stage.HURT_PRE) {
			cache.recursive++;
			return;
		}
		boolean replace = cache == null;
		PlayerAttackCache prev = null;
		if (!replace)
			replace = cache.getStage().ordinal() >= Stage.HURT_PRE.ordinal();
		Entity attacker = event.getSource().getEntity();
		if (!replace && attacker != null && PLAYER.containsKey(attacker.getUUID()))
			prev = PLAYER.get(attacker.getUUID());
		if (replace) {
			cache = new AttackCache();
			CACHE.put(id, cache);
		}
		if (prev != null)
			cache.setupPlayer(prev);
		DamageSource source = event.getSource();
		if (source.getEntity() instanceof LivingEntity entity) { // direct damage only
			ItemStack stack = entity.getMainHandItem();
			cache.setupAttackerProfile(entity, stack);
		}
		cache.pushAttackPre(event);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onAttackPost(LivingAttackEvent event) {
		if (event.getEntity().getLevel().isClientSide())
			return;
		AttackCache cache = CACHE.get(event.getEntity().getUUID());
		if (cache != null && cache.getStage() == Stage.HURT_PRE) {
			if (cache.recursive > 0) {
				cache.recursive--;
				return;
			}
			cache.pushAttackPost(event);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onActuallyHurtPre(LivingHurtEvent event) {
		if (event.getEntity().getLevel().isClientSide())
			return;
		AttackCache cache = CACHE.get(event.getEntity().getUUID());
		if (cache != null && cache.getStage() == Stage.HURT_POST)
			cache.pushHurtPre(event);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onActuallyHurtPost(LivingHurtEvent event) {
		if (event.getEntity().getLevel().isClientSide())
			return;
		AttackCache cache = CACHE.get(event.getEntity().getUUID());
		if (cache != null && cache.getStage() == Stage.ACTUALLY_HURT_PRE)
			cache.pushHurtPost(event);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onDamagePre(LivingDamageEvent event) {
		if (event.getEntity().getLevel().isClientSide())
			return;
		AttackCache cache = CACHE.get(event.getEntity().getUUID());
		if (cache != null && cache.getStage() == Stage.ACTUALLY_HURT_POST)
			cache.pushDamagePre(event);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onDamagePost(LivingDamageEvent event) {
		if (event.getEntity().getLevel().isClientSide())
			return;
		AttackCache cache = CACHE.get(event.getEntity().getUUID());
		if (cache != null && cache.getStage() == Stage.DAMAGE_PRE)
			cache.pushDamagePost(event);
	}

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event) {

	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		CACHE.clear();
	}

	@Nullable
	public static ResourceKey<DamageType> onDamageSourceCreate(CreateSourceEvent event) {
		if (event.getAttacker().getLevel().isClientSide())
			return null;
		if (PLAYER.containsKey(event.getAttacker().getUUID())) {
			event.setPlayerAttackCache(PLAYER.get(event.getAttacker().getUUID()));
		}
		LISTENERS.forEach(e -> e.onCreateSource(event));
		if (event.getResult() == null) return null;
		return event.getResult().type();
	}

}

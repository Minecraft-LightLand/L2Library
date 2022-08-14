package dev.xkmc.l2library.init.events.attack;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class AttackCache {

	int recursive = 0;

	private boolean damageFrozen = true;

	private Stage stage = Stage.PREINIT;
	private AttackEntityEvent player;
	private CriticalHitEvent crit;
	private LivingAttackEvent attack;
	private LivingHurtEvent hurt;
	private LivingDamageEvent damage;

	private LivingEntity target;
	private LivingEntity attacker;
	private ItemStack weapon;

	private float strength = -1;
	private float damage_pre;
	private float damage_modified;
	private float damage_dealt;

	void pushPlayer(AttackEntityEvent event) {
		stage = Stage.PLAYER_ATTACK;
		player = event;
		strength = event.getEntity().getAttackStrengthScale(1);
		AttackEventHandler.LISTENERS.forEach(e -> e.onPlayerAttack(this));
	}

	void pushCrit(CriticalHitEvent event) {
		stage = Stage.CRITICAL_HIT;
		crit = event;
		AttackEventHandler.LISTENERS.forEach(e -> e.onCriticalHit(this));
	}

	void pushAttackPre(LivingAttackEvent event) {
		stage = Stage.HURT_PRE;
		attack = event;
		target = attack.getEntity();
		damage_pre = event.getAmount();
		AttackEventHandler.LISTENERS.forEach(e -> e.onAttack(this, weapon));
	}

	void pushAttackPost(LivingAttackEvent event) {
		stage = Stage.ACTUALLY_HURT_POST;
	}

	void pushHurtPre(LivingHurtEvent event) {
		stage = Stage.ACTUALLY_HURT_PRE;
		hurt = event;
		damage_modified = event.getAmount();
		damageFrozen = false;
		AttackEventHandler.LISTENERS.forEach(e -> e.onHurt(this, weapon));
		damageFrozen = true;
		if (damage_modified != event.getAmount()) {
			event.setAmount(damage_modified);
		}
		AttackEventHandler.LISTENERS.forEach(e -> e.onHurtMaximized(this, weapon));
	}

	void pushHurtPost(LivingHurtEvent event) {
		stage = Stage.ACTUALLY_HURT_POST;
	}

	void pushDamagePre(LivingDamageEvent event) {
		stage = Stage.DAMAGE_PRE;
		damage = event;
		damage_dealt = event.getAmount();
		AttackEventHandler.LISTENERS.forEach(e -> e.onDamage(this, weapon));
		if (damage_dealt != event.getAmount()) {
			event.setAmount(damage_dealt);
		}
	}

	void pushDamagePost(LivingDamageEvent event) {
		stage = Stage.DAMAGE_POST;
		damage = event;
		damage_dealt = event.getAmount();
		AttackEventHandler.LISTENERS.forEach(e -> e.onDamageFinalized(this, weapon));
	}

	void setupAttackerProfile(LivingEntity entity, ItemStack stack) {
		attacker = entity;
		weapon = stack;
	}

	public Stage getStage() {
		return stage;
	}

	@Nullable
	public AttackEntityEvent getPlayerAttackEntityEvent() {
		return player;
	}

	@Nullable
	public CriticalHitEvent getCriticalHitEvent() {
		return crit;
	}

	@Nullable
	public LivingAttackEvent getLivingAttackEvent() {
		return attack;
	}

	@Nullable
	public LivingHurtEvent getLivingHurtEvent() {
		return hurt;
	}

	@Nullable
	public LivingDamageEvent getLivingDamageEvent() {
		return damage;
	}

	public LivingEntity getAttackTarget() {
		return target;
	}

	public LivingEntity getAttacker() {
		return attacker;
	}

	@Nullable
	public ItemStack getWeapon() {
		return weapon;
	}

	public float getStrength() {
		return strength;
	}

	public float getDamageOriginal() {
		if (stage.ordinal() < Stage.HURT_PRE.ordinal())
			throw new IllegalStateException("dealt damage not calculated yet");
		return damage_pre;
	}

	public float getDamageModified() {
		if (stage.ordinal() < Stage.ACTUALLY_HURT_PRE.ordinal())
			throw new IllegalStateException("dealt damage not calculated yet");
		return damage_modified;
	}

	public void setDamageModified(float damage) {
		if (damageFrozen)
			throw new IllegalStateException("modify hurt damage only on onHurt event.");
		this.damage_modified = damage;
	}

	public float getDamageDealt() {
		if (stage.ordinal() < Stage.DAMAGE_PRE.ordinal())
			throw new IllegalStateException("actual damage not calculated yet");
		return damage_dealt;
	}

	public void setDamageDealt(float damage) {
		if (stage != Stage.DAMAGE_PRE)
			throw new IllegalStateException("set actual damage only on onDamage event.");
		damage_dealt = damage;
	}

}

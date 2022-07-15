package dev.xkmc.l2library.init.events.attack;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

import javax.annotation.Nullable;

public class AttackCache {

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

	void pushAttack(LivingAttackEvent event) {
		stage = Stage.HURT;
		attack = event;
		target = attack.getEntity();
		damage_pre = event.getAmount();
		AttackEventHandler.LISTENERS.forEach(e -> e.onAttack(this, weapon));
	}

	void pushHurt(LivingHurtEvent event) {
		stage = Stage.ACTUALLY_HURT;
		hurt = event;
		damage_modified = event.getAmount();
		AttackEventHandler.LISTENERS.forEach(e -> e.onHurt(this, weapon));
	}

	void pushDamage(LivingDamageEvent event) {
		stage = Stage.DAMAGE;
		damage = event;
		damage_dealt = event.getAmount();
		AttackEventHandler.LISTENERS.forEach(e -> e.onDamage(this, weapon));
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
		return damage_pre;
	}

	public float getDamageModified() {
		return damage_modified;
	}

	public void setDamageModified(float damage) {
		this.damage_modified = damage;
	}

	public float getDamageDealt() {
		return damage_dealt;
	}
}

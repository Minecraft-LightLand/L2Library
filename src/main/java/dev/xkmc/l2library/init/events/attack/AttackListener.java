package dev.xkmc.l2library.init.events.attack;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

public interface AttackListener {

	default void onPlayerAttack(PlayerAttackCache cache) {
	}

	default boolean onCriticalHit(PlayerAttackCache cache, CriticalHitEvent event) {
		return false;
	}

	default void onAttack(AttackCache cache, ItemStack weapon) {
	}

	default void onHurt(AttackCache cache, ItemStack weapon) {
	}

	default void onHurtMaximized(AttackCache cache, ItemStack weapon) {

	}

	default void onDamage(AttackCache cache, ItemStack weapon) {
	}

	default void onDamageFinalized(AttackCache cache, ItemStack weapon) {
	}

	default void onCreateSource(CreateSourceEvent event) {
	}

}

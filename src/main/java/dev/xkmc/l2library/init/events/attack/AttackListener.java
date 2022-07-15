package dev.xkmc.l2library.init.events.attack;

import net.minecraft.world.item.ItemStack;

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

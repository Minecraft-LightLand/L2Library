package dev.xkmc.l2library.init.events.damage;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.function.Supplier;

public interface DamageTypeWrapper {

	ResourceKey<DamageType> type();

	boolean validState(DamageState state);

	boolean isEnabled(DamageState state);

	DamageTypeWrapper enable(DamageState state);

	default void gen(DamageWrapperTagProvider gen, HolderLookup.Provider pvd) {
	}

	Supplier<DamageType> getObject();

}

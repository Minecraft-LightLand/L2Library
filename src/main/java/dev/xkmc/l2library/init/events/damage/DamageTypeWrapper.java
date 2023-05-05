package dev.xkmc.l2library.init.events.damage;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Set;

public interface DamageTypeWrapper {

	ResourceKey<DamageType> type();

	default Holder<DamageType> getHolder(Level level) {
		return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type());
	}

	boolean validState(DamageState state);

	boolean isEnabled(DamageState state);

	@Nullable
	DamageTypeWrapper enable(DamageState state);

	DamageTypeWrapper toRoot();

	default void gen(DamageWrapperTagProvider gen, HolderLookup.Provider pvd) {
	}

	DamageType getObject();

	Set<DamageState> states();
}

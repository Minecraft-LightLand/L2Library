package dev.xkmc.l2library.base.effects.api;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;

public class InherentEffect extends MobEffect {

	protected InherentEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
	}

}

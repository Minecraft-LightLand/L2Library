package dev.xkmc.l2library.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class InherentEffect extends MobEffect {

	protected InherentEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return List.of();
	}
}

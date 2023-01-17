package dev.xkmc.l2library.base.effects;

import net.minecraft.world.effect.MobEffectInstance;

public class EffectProperties {

	public Boolean ambient = null;
	public Boolean visible = null;
	public Boolean showIcon = null;

	public MobEffectInstance set(MobEffectInstance ins) {
		if (ambient != null) ins.ambient = ambient;
		if (visible != null) ins.visible = visible;
		if (showIcon != null) ins.showIcon = showIcon;
		return ins;
	}

}

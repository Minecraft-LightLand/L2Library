package dev.xkmc.l2library.base.effects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.NotNull;

@Event.HasResult
public class ForceAddEffectEvent extends MobEffectEvent {

	public ForceAddEffectEvent(LivingEntity living, @NotNull MobEffectInstance effectInstance) {
		super(living, effectInstance);
	}

	@Override
	@NotNull
	public MobEffectInstance getEffectInstance() {
		return super.getEffectInstance();
	}

}

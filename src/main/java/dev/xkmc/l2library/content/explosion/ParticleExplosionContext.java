package dev.xkmc.l2library.content.explosion;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public record ParticleExplosionContext(ParticleOptions small,
									   ParticleOptions large,
									   Holder<SoundEvent> sound) {

	public static final ParticleExplosionContext INS = new ParticleExplosionContext(
			ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE
	);

	public static ParticleExplosionContext of(float radius) {
		return new ParticleExplosionContext(
				radius < 2 ? ParticleTypes.EXPLOSION : ParticleTypes.EXPLOSION_EMITTER,
				ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE
		);
	}

}

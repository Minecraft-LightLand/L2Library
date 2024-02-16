package dev.xkmc.l2library.base.explosion;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;

public class BaseExplosion extends Explosion {

	public final BaseExplosionContext base;
	public final ModExplosionContext mod;
	public final VanillaExplosionContext mc;
	public final ParticleExplosionContext particle;

	public BaseExplosion(BaseExplosionContext base, VanillaExplosionContext mc, ModExplosionContext mod, ParticleExplosionContext particle) {
		super(base.level(), mc.entity(), mc.source(), mc.calculator(), base.x(), base.y(), base.z(), base.r(), mc.fire(), mc.type(),
				particle.small(), particle.large(), particle.sound());
		this.base = base;
		this.mod = mod;
		this.mc = mc;
		this.particle = particle;
	}

	/**
	 * return false to cancel hurt
	 */
	public boolean hurtEntity(Entity entity) {
		return mod.hurtEntity(entity);
	}

}

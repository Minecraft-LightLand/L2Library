package dev.xkmc.l2library.init.explosion;

import net.minecraft.world.entity.Entity;

public interface ModExplosionContext {

	/**
	 * return false to cancel damage
	 */
	boolean hurtEntity(Entity entity);

}

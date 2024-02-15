package dev.xkmc.l2library.init.explosion;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;

public record VanillaExplosionContext(@Nullable Entity entity, @Nullable DamageSource source,
									  @Nullable ExplosionDamageCalculator calculator,
									  boolean fire, Explosion.BlockInteraction type) {

	public VanillaExplosionContext(Level level, @Nullable Entity entity, @Nullable DamageSource source,
								   @Nullable ExplosionDamageCalculator calculator,
								   boolean fire, Level.ExplosionInteraction type) {
		this(entity, source, calculator, fire, getType(level, entity, type));
	}

	private static Explosion.BlockInteraction getType(Level level, @Nullable Entity entity, Level.ExplosionInteraction type) {
		return switch (type) {
			case NONE -> Explosion.BlockInteraction.KEEP;
			case BLOCK -> getDestroyType(level, GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
			case MOB -> EventHooks.getMobGriefingEvent(level, entity instanceof LivingEntity le ? le : null) ?
					getDestroyType(level, GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) :
					Explosion.BlockInteraction.KEEP;
			case TNT -> getDestroyType(level, GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
			case BLOW -> Explosion.BlockInteraction.TRIGGER_BLOCK;
		};
	}


	private static Explosion.BlockInteraction getDestroyType(Level level, GameRules.Key<GameRules.BooleanValue> pGameRule) {
		return level.getGameRules().getBoolean(pGameRule) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
	}
}

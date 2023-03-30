package dev.xkmc.l2library.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {

	protected AbstractArrowMixin(EntityType<? extends Projectile> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Shadow
	private int life;


	@Inject(at = @At("HEAD"), method = "tickDespawn")
	public void l2library_tickDespawn_fastDespawn(CallbackInfo ci) {
		if (getPersistentData().contains("DespawnFactor"))
			life += getPersistentData().getInt("DespawnFactor") - 1;
	}

}

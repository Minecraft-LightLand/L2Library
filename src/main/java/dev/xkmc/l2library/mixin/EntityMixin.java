package dev.xkmc.l2library.mixin;

import dev.xkmc.l2library.util.raytrace.EntityTarget;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(at = @At("HEAD"), method = "isCurrentlyGlowing", cancellable = true)
	public void isCurrentlyGlowing(CallbackInfoReturnable<Boolean> cir) {
		for (EntityTarget target : EntityTarget.LIST) {
			if (target.target == (Object) this) {
				cir.setReturnValue(true);
				return;
			}
		}
	}

}

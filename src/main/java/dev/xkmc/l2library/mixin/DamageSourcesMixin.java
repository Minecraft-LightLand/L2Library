package dev.xkmc.l2library.mixin;

import dev.xkmc.l2library.init.events.attack.AttackEventHandler;
import dev.xkmc.l2library.init.events.attack.CreateSourceEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSources.class)
public abstract class DamageSourcesMixin {

	@Shadow
	@Final
	private Registry<DamageType> damageTypes;

	@Inject(at = @At("HEAD"), method = "source(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/damagesource/DamageSource;", cancellable = true)
	public void l2library_modifyDamageSource_direct(ResourceKey<DamageType> type, Entity attacker, CallbackInfoReturnable<DamageSource> cir) {
		if (attacker instanceof LivingEntity le) {
			ResourceKey<DamageType> ans = AttackEventHandler.onDamageSourceCreate(new CreateSourceEvent(damageTypes, type, le, null));
			if (ans != null) {
				cir.setReturnValue(new DamageSource(damageTypes.getHolderOrThrow(ans), attacker));
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "source(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/damagesource/DamageSource;", cancellable = true)
	public void l2library_modifyDamageSource_indirect(ResourceKey<DamageType> type, Entity direct, Entity owner, CallbackInfoReturnable<DamageSource> cir) {
		if (owner instanceof LivingEntity le) {
			ResourceKey<DamageType> ans = AttackEventHandler.onDamageSourceCreate(new CreateSourceEvent(damageTypes, type, le, direct));
			if (ans != null) {
				cir.setReturnValue(new DamageSource(damageTypes.getHolderOrThrow(ans), direct, le));
			}
		}
	}

}

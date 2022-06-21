package dev.xkmc.l2library.mixin;

import dev.xkmc.l2library.util.raytrace.FastItem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class ClientLocalPlayerMixin {

	@Shadow
	private boolean startedUsingItem;

	private static boolean in_ai_step = false;

	@Inject(at = @At("HEAD"), method = "aiStep")
	public void aiStep(CallbackInfo ci) {
		in_ai_step = true;
	}

	@Inject(at = @At("HEAD"), method = "isUsingItem", cancellable = true)
	public void isUsingItem(CallbackInfoReturnable<Boolean> cir) {
		if (in_ai_step) {
			in_ai_step = false;
			Player player = (Player) (Object) this;
			if (this.startedUsingItem) {
				ItemStack stack = player.getUseItem();
				if (stack.getItem() instanceof FastItem fast) {
					if (fast.isFast(stack)) {
						cir.setReturnValue(false);
					}
				}
			}
		}
	}


}

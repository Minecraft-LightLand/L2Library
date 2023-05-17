package dev.xkmc.l2library.mixin;

import dev.xkmc.l2library.init.events.screen.base.ScreenTracker;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

	@Inject(at = @At("HEAD"), method = "openMenu")
	public void l2library$openScreen(MenuProvider menu, CallbackInfoReturnable<OptionalInt> cir) {
		ServerPlayer player = Wrappers.cast(this);
		ScreenTracker.get(player).provider = menu;
	}

}

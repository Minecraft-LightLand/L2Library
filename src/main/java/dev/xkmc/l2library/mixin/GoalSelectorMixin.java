package dev.xkmc.l2library.mixin;

import dev.xkmc.l2library.util.GoalFix;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(GoalSelector.class)
public class GoalSelectorMixin {

	@Inject(at = @At("HEAD"), method = "addGoal", cancellable = true)
	public void l2library$addGoal(int p, Goal g, CallbackInfo ci) {
		GoalSelector self = Wrappers.cast(this);
		if (GoalFix.add(p, g, self)) {
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "removeGoal", cancellable = true)
	public void l2library$removeGoal(Goal g, CallbackInfo ci) {
		GoalSelector self = Wrappers.cast(this);
		if (GoalFix.remove(g, self)) {
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "removeAllGoals", cancellable = true)
	public void l2library$removeAllGoals(Predicate<Goal> pred, CallbackInfo ci) {
		GoalSelector self = Wrappers.cast(this);
		if (GoalFix.removeAll(pred, self)) {
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "tick")
	public void l2library$tick$head(CallbackInfo ci) {
		GoalSelector self = Wrappers.cast(this);
		GoalFix.start(self);
	}


	@Inject(at = @At("TAIL"), method = "tick")
	public void l2library$tick$tail(CallbackInfo ci) {
		GoalSelector self = Wrappers.cast(this);
		GoalFix.end(self);
	}

}

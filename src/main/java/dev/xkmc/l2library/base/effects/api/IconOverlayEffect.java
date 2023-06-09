package dev.xkmc.l2library.base.effects.api;

import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public interface IconOverlayEffect extends ClientRenderEffect {

	@Override
	default void render(LivingEntity entity, int lv, Consumer<DelayedEntityRender> adder) {
		adder.accept(getIcon());
	}

	DelayedEntityRender getIcon();

}

package dev.xkmc.l2library.base.effects.api;

import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public interface ClientRenderEffect {

	void render(LivingEntity entity, int lv, Consumer<DelayedEntityRender> adder);

}

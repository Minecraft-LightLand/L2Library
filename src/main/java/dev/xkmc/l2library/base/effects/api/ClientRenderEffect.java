package dev.xkmc.l2library.base.effects.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public interface ClientRenderEffect {

	void render(LivingEntity entity, int lv, Consumer<ResourceLocation> adder);

}

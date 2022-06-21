package dev.xkmc.l2library.base.effects.api;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.effect.MobEffectInstance;

public interface FirstPlayerRenderEffect extends ClientRenderEffect {

	void onClientLevelRender(AbstractClientPlayer player, MobEffectInstance value);

}

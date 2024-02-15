package dev.xkmc.l2library.base.effects;

import dev.xkmc.l2library.init.events.ClientEffectRenderEvents;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public record EffectToClient(int entity, MobEffect effect, boolean exist, int level)
		implements SerialPacketBase<EffectToClient> {

	@Override
	public void handle(@Nullable Player player) {
		ClientEffectRenderEvents.sync(this);
	}

}

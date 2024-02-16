package dev.xkmc.l2library.util.raytrace;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record TargetSetPacket(UUID player, @Nullable UUID target) implements SerialPacketBase<TargetSetPacket> {

	@Override
	public void handle(@Nullable Player player) {
		RayTraceUtil.sync(this);
	}

}

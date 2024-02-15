package dev.xkmc.l2library.serial.config;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public record SyncPacket(String id, ArrayList<PacketHandlerWithConfig.ConfigInstance> map)
		implements SerialPacketBase<SyncPacket> {

	@Override
	public void handle(@Nullable Player player) {
		if (map != null) {
			var handler = PacketHandlerWithConfig.INTERNAL.get(id);
			handler.listener.apply(map);
		}
	}

}

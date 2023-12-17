package dev.xkmc.l2library.capability.conditionals;

import dev.xkmc.l2library.init.L2Library;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface NetworkSensitiveToken {
	void onSync(Player player);

	static <T extends ConditionalToken> void sync(TokenKey<T> key, T token, ServerPlayer sp) {
		L2Library.PACKET_HANDLER.toClientPlayer(TokenToClient.of(key, token), sp);
	}

}

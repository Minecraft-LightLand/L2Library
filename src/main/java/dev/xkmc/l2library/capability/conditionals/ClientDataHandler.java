package dev.xkmc.l2library.capability.conditionals;

import dev.xkmc.l2library.util.Proxy;
import net.minecraft.world.entity.player.Player;

public class ClientDataHandler {

	public static <T extends ConditionalToken> void handle(TokenKey<T> key, T token) {
		Player player = Proxy.getClientPlayer();
		if (player == null) return;
		ConditionalData.HOLDER.get(player).data.put(key, token);
		if (token instanceof NetworkSensitiveToken t) {
			t.onSync(player);
		}
	}

}

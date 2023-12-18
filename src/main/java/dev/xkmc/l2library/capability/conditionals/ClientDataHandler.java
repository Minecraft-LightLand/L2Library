package dev.xkmc.l2library.capability.conditionals;

import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.world.entity.player.Player;

public class ClientDataHandler {

	public static <T extends ConditionalToken> void handle(TokenKey<T> key, T token) {
		Player player = Proxy.getClientPlayer();
		if (player == null) return;
		ConditionalToken old = ConditionalData.HOLDER.get(player).data.put(key, token);
		if (token instanceof NetworkSensitiveToken<?> t) {
			t.onSync(Wrappers.cast(old), player);
		}
	}

}

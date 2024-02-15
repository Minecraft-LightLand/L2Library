package dev.xkmc.l2library.capability.conditionals;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public record TokenToClient(ResourceLocation id, ConditionalToken token)
		implements SerialPacketBase<TokenToClient> {

	public static <T extends ConditionalToken> TokenToClient of(TokenKey<T> key, T token) {
		return new TokenToClient(key.asLocation(), token);
	}

	@Override
	public void handle(@Nullable Player player) {
		ClientDataHandler.handle(TokenKey.of(id), token);
	}

}

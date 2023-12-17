package dev.xkmc.l2library.capability.conditionals;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class TokenToClient extends SerialPacketBase {

	public static <T extends ConditionalToken> TokenToClient of(TokenKey<T> key, T token) {
		return new TokenToClient(key.asLocation(), token);
	}

	@SerialClass.SerialField
	public ResourceLocation id;

	@SerialClass.SerialField
	public ConditionalToken token;

	@Deprecated
	public TokenToClient() {
	}

	public TokenToClient(ResourceLocation id, ConditionalToken token) {
		this.id = id;
		this.token = token;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ClientDataHandler.handle(TokenKey.of(id), token);
	}
}

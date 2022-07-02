package dev.xkmc.l2library.serial.codec;

import dev.xkmc.l2library.serial.unified.PacketContext;
import dev.xkmc.l2library.serial.unified.UnifiedCodec;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import dev.xkmc.l2library.util.code.Wrappers;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;

public class PacketCodec {

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T from(FriendlyByteBuf buf, Class<T> cls, @Nullable T ans) {
		return Wrappers.get(() -> (T) UnifiedCodec.deserializeValue(new PacketContext(buf), buf, TypeInfo.of(cls), ans));
	}

	public static <T> void to(FriendlyByteBuf buf, T obj) {
		PacketCodec.to(buf, obj, Wrappers.cast(obj.getClass()));
	}

	public static <T extends R, R> void to(FriendlyByteBuf buf, T obj, Class<R> r) {
		Wrappers.run(() -> UnifiedCodec.serializeValue(new PacketContext(buf), TypeInfo.of(r), obj));
	}

}

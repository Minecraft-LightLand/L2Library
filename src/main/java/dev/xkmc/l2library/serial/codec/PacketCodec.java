package dev.xkmc.l2library.serial.codec;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.handler.Handlers;
import dev.xkmc.l2library.serial.unified.PacketContext;
import dev.xkmc.l2library.serial.unified.UnifiedCodec;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import dev.xkmc.l2library.util.code.Wrappers;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PacketCodec {

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T from(FriendlyByteBuf buf, Class<T> cls, T ans) {
		return Wrappers.get(() -> (T) UnifiedCodec.deserializeValue(new PacketContext(buf), buf, TypeInfo.of(cls), ans));
	}

	public static <T> void to(FriendlyByteBuf buf, T obj) {
		Wrappers.run(() -> UnifiedCodec.serializeValue(new PacketContext(buf), TypeInfo.of(obj.getClass()), obj));
	}

	public static <T extends R, R> void to(FriendlyByteBuf buf, T obj, Class<R> r) {
		Wrappers.run(() -> UnifiedCodec.serializeValue(new PacketContext(buf), TypeInfo.of(r), obj));
	}

}

package dev.xkmc.l2library.serial.codec;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.handler.Handlers;
import dev.xkmc.l2library.serial.unified.JsonContext;
import dev.xkmc.l2library.serial.unified.UnifiedCodec;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import dev.xkmc.l2library.util.code.Wrappers;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Capable of serializing primitive type, Arrays, Item, ItemStacl, Ingredient
 * <br>
 * Not capable of handling inheritance, collections
 */
public class JsonCodec {

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T from(JsonElement obj, Class<T> cls, @Nullable T ans) {
		return Wrappers.get(() -> (T) UnifiedCodec.deserializeValue(new JsonContext(), obj, TypeInfo.of(cls), ans));
	}

	@Nullable
	public static <T> JsonElement toJson(T obj) {
		return Wrappers.get(() -> UnifiedCodec.serializeValue(new JsonContext(), TypeInfo.of(obj.getClass()), obj));
	}

	@Nullable
	public static <T extends R, R> JsonElement toJson(T obj, Class<R> cls) {
		return Wrappers.get(() -> UnifiedCodec.serializeValue(new JsonContext(), TypeInfo.of(cls), obj));
	}

}
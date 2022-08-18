package dev.xkmc.l2library.serial.codec;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.unified.TagContext;
import dev.xkmc.l2library.serial.unified.UnifiedCodec;
import dev.xkmc.l2library.serial.wrapper.ClassCache;
import dev.xkmc.l2library.serial.wrapper.TypeInfo;
import dev.xkmc.l2library.util.code.Wrappers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class TagCodec {

	@Nullable
	public static <T> T fromTag(CompoundTag tag, Class<?> cls) {
		return fromTag(tag, cls, null, f -> true);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T fromTag(CompoundTag tag, Class<?> cls, @Nullable T obj, Predicate<SerialClass.SerialField> pred) {
		return (T) Wrappers.get(() -> UnifiedCodec.deserializeObject(new TagContext(pred), tag, ClassCache.get(cls), obj));
	}

	@Nullable
	public static CompoundTag toTag(CompoundTag tag, Object obj) {
		return toTag(tag, obj.getClass(), obj);
	}

	@Nullable
	public static CompoundTag toTag(CompoundTag tag, Class<?> cls, Object obj) {
		return toTag(tag, cls, obj, f -> true);
	}

	@Nullable
	public static CompoundTag toTag(CompoundTag tag, Class<?> cls, Object obj, Predicate<SerialClass.SerialField> pred) {
		return Wrappers.get(() -> UnifiedCodec.serializeObject(new TagContext(pred), tag, ClassCache.get(cls), obj));
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T valueFromTag(Tag tag, Class<?> cls, Predicate<SerialClass.SerialField> pred) {
		return (T) Wrappers.get(() -> UnifiedCodec.deserializeValue(new TagContext(pred), tag, TypeInfo.of(cls), null));
	}

	@Nullable
	public static Tag valueToTag(Class<?> cls, Object obj, Predicate<SerialClass.SerialField> pred) {
		return Wrappers.get(() -> UnifiedCodec.serializeValue(new TagContext(pred), TypeInfo.of(cls), obj));
	}

	@Nullable
	public static <T> T valueFromTag(Tag tag, Class<?> cls) {
		return valueFromTag(tag, cls, e -> true);
	}

	@Nullable
	public static Tag valueToTag(Object obj) {
		return valueToTag(obj.getClass(), obj, e -> true);
	}


}
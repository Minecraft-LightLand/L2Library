package dev.xkmc.l2library.serial.codec;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.unified.TagContext;
import dev.xkmc.l2library.serial.unified.UnifiedCodec;
import dev.xkmc.l2library.serial.wrapper.ClassCache;
import dev.xkmc.l2library.util.code.Wrappers;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * Capable of handing primitive types, array, BlockPos, ItemStack, inheritance
 * <br>
 * Not capable of handing collections
 */
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

}
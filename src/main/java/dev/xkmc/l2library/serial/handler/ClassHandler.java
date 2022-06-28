package dev.xkmc.l2library.serial.handler;

import com.google.gson.JsonElement;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings({"unsafe", "unchecked"})
public class ClassHandler<R extends Tag, T> implements JsonClassHandler<T>, NBTClassHandler<R, T>, PacketClassHandler<T> {

	public final Function<Object, JsonElement> toJson;
	public final Function<JsonElement, ?> fromJson;
	public final Function<FriendlyByteBuf, ?> fromPacket;
	public final BiConsumer<FriendlyByteBuf, Object> toPacket;
	public final Function<Tag, ?> fromTag;
	public final Function<Object, Tag> toTag;

	@SuppressWarnings("unchecked")
	@ParametersAreNullableByDefault
	public ClassHandler(@Nonnull Class<T> cls, Function<T, JsonElement> tj, Function<JsonElement, T> fj, Function<FriendlyByteBuf, T> fp,
						BiConsumer<FriendlyByteBuf, T> tp, Function<R, T> ft, Function<T, Tag> tt, @Nonnull Class<?>... others) {
		this.toJson = (Function<Object, JsonElement>) tj;
		this.fromJson = fj;
		this.fromPacket = fp;
		this.toPacket = (BiConsumer<FriendlyByteBuf, Object>) tp;
		fromTag = (Function<Tag, ?>) ft;
		toTag = (Function<Object, Tag>) tt;
		put(cls);
		for (Class<?> c : others)
			put(c);
	}

	private void put(Class<?> cls) {
		if (toJson != null && fromJson != null) Handlers.JSON_MAP.put(cls, this);
		if (fromTag != null && toTag != null) Handlers.NBT_MAP.put(cls, this);
		if (fromPacket != null && toPacket != null) Handlers.PACKET_MAP.put(cls, this);
	}

	@Override
	public T fromTag(Tag tag) {
		return (T) fromTag.apply(tag);
	}

	@Override
	public R toTag(Object obj) {
		return (R) toTag.apply(obj);
	}

	@Override
	public JsonElement toJson(Object obj) {
		return toJson.apply(obj);
	}

	@Override
	public T fromJson(JsonElement e) {
		return (T) fromJson.apply(e);
	}

	@Override
	public void toPacket(FriendlyByteBuf buf, Object obj) {
		toPacket.accept(buf, obj);
	}

	@Override
	public T fromPacket(FriendlyByteBuf buf) {
		return (T) fromPacket.apply(buf);
	}
}

package dev.xkmc.l2library.serial.handler;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.StringTag;

import java.util.function.Function;

public class StringClassHandler<T> extends ClassHandler<StringTag, T> {

	public StringClassHandler(Class<T> cls, Function<String, T> fj, Function<T, String> tp) {
		super(cls, e -> tp == null ? JsonNull.INSTANCE : new JsonPrimitive(tp.apply(e)), e -> {
					if (e.isJsonNull())
						return null;
					String str = e.getAsString();
					if (str.length() == 0)
						return null;
					return fj.apply(str);
				}, p -> {
					String str = p.readUtf();
					if (str.length() == 0)
						return null;
					return fj.apply(str);
				}, (p, t) -> p.writeUtf(t == null ? "" : tp.apply(t)),
				t -> fj.apply(t.getAsString()),
				e -> StringTag.valueOf(tp.apply(e)));
	}

}

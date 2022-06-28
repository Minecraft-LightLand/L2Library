package dev.xkmc.l2library.serial.nulldefer;

import dev.xkmc.l2library.serial.handler.Handlers;
import dev.xkmc.l2library.util.code.Wrappers;

import javax.annotation.Nullable;

public abstract class NullDefer<T> {

	@Nullable
	public static <T> NullDefer<T> get(Class<T> cls) {
		return Wrappers.cast(Handlers.MAP.get(cls));
	}

	protected NullDefer(Class<T> cls) {
		Handlers.MAP.put(cls, this);
	}

	public abstract boolean predicate(T obj);

	public abstract T getNullDefault();

}

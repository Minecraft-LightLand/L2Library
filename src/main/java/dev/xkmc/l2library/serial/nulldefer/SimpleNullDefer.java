package dev.xkmc.l2library.serial.nulldefer;

public final class SimpleNullDefer<T> extends NullDefer<T> {

	private final T val;

	public SimpleNullDefer(Class<T> cls, T val) {
		super(cls);
		this.val = val;
	}

	@Override
	public boolean predicate(T obj) {
		return obj == val;
	}

	@Override
	public T getNullDefault() {
		return val;
	}
}

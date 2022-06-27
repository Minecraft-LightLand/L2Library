package dev.xkmc.l2library.util.code;

public class LazyExc<T> {

	private Wrappers.ExcSup<T> factory;
	private T value;

	public LazyExc(Wrappers.ExcSup<T> factory) {
		this.factory = factory;
	}

	public T get() throws Exception {
		if (factory != null) {
			value = factory.get();
			factory = null;
		}
		assert value != null;
		return value;
	}

}

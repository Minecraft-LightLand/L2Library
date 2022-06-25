package dev.xkmc.l2library.util.code;

import java.util.function.Function;

public class LazyFunction<A, B> {

	public LazyFunction(Function<A, B> sup) {
		this.func = sup;
		this.value = null;
	}

	public static <A, B> LazyFunction<A, B> create(Function<A, B> sup) {
		return new LazyFunction<>(sup);
	}

	private Function<A, B> func;
	private B value;

	public B get(A a) {
		if (func != null) {
			value = func.apply(a);
			func = null;
		}
		return value;
	}

}

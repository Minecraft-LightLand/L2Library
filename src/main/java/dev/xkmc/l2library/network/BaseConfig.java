package dev.xkmc.l2library.network;

import dev.xkmc.l2library.serial.SerialClass;

import java.util.*;
import java.util.function.Function;

@SerialClass
public class BaseConfig {

	public static <T, C> HashSet<T> collectSet(List<C> list, Function<C, Set<T>> getter) {
		return list.stream().reduce(new HashSet<T>(), (a, c) -> {
			a.addAll(getter.apply(c));
			return a;
		}, (a, b) -> {
			a.addAll(b);
			return a;
		});
	}

	public static <T, C> ArrayList<T> collectList(List<C> list, Function<C, List<T>> getter) {
		return list.stream().reduce(new ArrayList<>(), (a, c) -> {
			a.addAll(getter.apply(c));
			return a;
		}, (a, b) -> {
			a.addAll(b);
			return a;
		});
	}

	public static <T, C, K> HashMap<K, T> collectMap(List<C> list, Function<C, Map<K, T>> getter) {
		return list.stream().reduce(new HashMap<>(), (a, c) -> {
			a.putAll(getter.apply(c));
			return a;
		}, (a, b) -> {
			a.putAll(b);
			return a;
		});
	}

}
